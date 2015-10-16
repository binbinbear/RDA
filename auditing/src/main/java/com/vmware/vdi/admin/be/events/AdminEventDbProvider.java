package com.vmware.vdi.admin.be.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.adam.AdamEventsDatabaseManager;
import com.vmware.vdi.adamwrapper.exceptions.ADAMServerException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.objects.EventsDatabase;
import com.vmware.vdi.adamwrapper.objects.SVIConstants.SviOperation;
import com.vmware.vdi.admin.ui.common.Util;
import com.vmware.vdi.dbwrapper.exceptions.DBConnectionException;
import com.vmware.vdi.events.Event;
import com.vmware.vdi.events.Event.EventAttributeRenderer;
import com.vmware.vdi.events.enums.EventAttribute;
import com.vmware.vdi.events.enums.EventModule;
import com.vmware.vdi.events.enums.EventSeverity;
import com.vmware.vdi.events.server.forwarders.database.connectors.EventDBConnection;

/**
 * The class AdminEventLogProvider provides the functionalities to retrieve
 * event data from database.
 *
 * @author dliu
 *
 */
public class AdminEventDbProvider implements AdminEventProvider {

    private static class AdminRenderer implements EventAttributeRenderer {
        private static boolean equals(Enum<?> key, Object value) {
            return key.toString().equals(value);
        }

        public Object render(String name, Object value) {
            if (equals(EventAttribute.PROP_SVI_OPERATION, name)) {
                if (equals(SviOperation.rebalance, value)) {
                    return Util.getString("SVIOperationRebalance");
                }

                if (equals(SviOperation.refresh, value)) {
                    return Util.getString("SVIOperationRefresh");
                }

                if (equals(SviOperation.resync, value)) {
                     return Util.getString("SVIOperationResync");
                }
            }

            return value;
        }
    }

    private static final Logger logger = Logger
            .getLogger(AdminEventDbProvider.class);

    private static final AdminRenderer RENDERER = new AdminRenderer();

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vdi.admin.be.events.AdminEventProvider#getSeverityCount(com
     *      .vmware.vdi.adamwrapper.ldap.VDIContext,
     *      com.vmware.vdi.admin.be.events.AdminEventFilter)
     */
    public Map<EventSeverity, Integer> getSeverityCounts(VDIContext ctx,
            AdminEventFilter filter) {
        Map<EventSeverity, Integer> adminevents = null;

        AdminEventDatabase database = null;
        try {
            EventsDatabase dbconfig = this.getDatabaseConfiguration(ctx);
            if (dbconfig != null) {
                database = new AdminEventDatabase(dbconfig);
                if (database.connect(dbconfig)) {
                    adminevents = database.readSeverityCount(filter);
                }
            }
        } catch (ADAMServerException e) {
            logger.debug("Failed to load event counts from database:", e);
        } catch (Throwable e) {
            logger.debug("Fatal to load event counts from database:", e);
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DBConnectionException e) {
                    logger.warn("Could not close database connection: "
                            + e.getMessage());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not close database connection: "
                                + e.getMessage(), e);
                    }
                }
            }
        }
        return adminevents;
    }

    /**
     * It returns the event counts from event table.
     *
     * @param ctx
     *                The VDIContext
     * @param recent
     *                The flag if to count from recent event table or history
     *                table
     * @return The total events from history table
     */
    public int getTotalEventCount(VDIContext ctx, boolean recent) {
        AdminEventDatabase database = null;
        try {
            EventsDatabase dbconfig = this.getDatabaseConfiguration(ctx);
            if (dbconfig != null) {
                database = new AdminEventDatabase(dbconfig);
                if (database.connect(dbconfig)) {
                    return database.getTotalEventCount(recent);
                }
            }
        } catch (ADAMServerException e) {
            logger.debug("Failed to load event counts:", e);
        } catch (Throwable e) {
            logger.debug("Fatal to load event counts:", e);
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DBConnectionException e) {
                    logger.warn("Could not close database connection: "
                            + e.getMessage());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not close database connection: "
                                + e.getMessage(), e);
                    }
                }
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vdi.admin.be.events.AdminEventProvider#getEventList(com.vmware
     *      .vdi.adamwrapper.ldap.VDIContext,
     *      com.vmware.vdi.admin.be.events.AdminEventFilter)
     */
    public List<AdminEvent> getEventList(VDIContext ctx, AdminEventFilter filter)
            throws Exception {
        List<AdminEvent> adminevents = null;

        long starttime = System.currentTimeMillis();
        AdminEventDatabase database = null;
        try {
            EventsDatabase dbconfig = this.getDatabaseConfiguration(ctx);
            if (dbconfig != null) {
                database = new AdminEventDatabase(dbconfig);
                if (database.connect(dbconfig)) {
                    Map<Integer, Event> events = database.readEvents(ctx,
                            filter);

                    long endtime = System.currentTimeMillis();
                    logger.debug("Loading event data (ms): "
                            + (endtime - starttime));

                    adminevents = this.buildAdminEvent(events);
                }
            }
        } catch (ADAMServerException e) {
            logger.debug("Failed to load events from database:", e);
            throw e;
        } catch (Throwable e) {
            logger.debug("Fatal to load events from database:", e);
            throw new ADAMServerException(
                    ADAMServerException.ERROR_EVENT_DATABASE_CONNECT);
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DBConnectionException e) {
                    logger.warn("Could not close database connection: "
                            + e.getMessage());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not close database connection: "
                                + e.getMessage(), e);
                    }
                }
            }
        }

        return adminevents;
    }

    /**
     * It reads the database configuration from ADAM.
     *
     * Only the first one is returned.
     *
     * @param ctx
     *                The VDIContext
     * @return The database configuration
     * @throws ADAMServerException
     */
    private EventsDatabase getDatabaseConfiguration(VDIContext ctx)
            throws ADAMServerException {
        List<EventsDatabase> dbconfigs = AdamEventsDatabaseManager
                .getInstance().getAll(ctx);
        if (dbconfigs.isEmpty()) {
            logger.debug("Database configuration not found");
            return null;
        }
        return dbconfigs.get(0);
    }

    /**
     * It converts event objects to admin event objects.
     *
     * @param events
     *                The list of event IDs and their event objects
     * @return The list of admin event objects
     */
    private List<AdminEvent> buildAdminEvent(Map<Integer, Event> events) {
        List<AdminEvent> adminevents = new ArrayList<AdminEvent>(events.size());
        for (Event event : events.values()) {
            AdminEvent adminevent = this.buildAdminEvent(event);
            adminevents.add(adminevent);
        }
        return adminevents;
    }

    /**
     * It builds an admin event object from an event object.
     *
     * @param event
     *                The event object
     * @return The admin event object
     */
    private AdminEvent buildAdminEvent(Event event) {
        int eventId = (Integer) event.get(EventDBConnection.EVENTID);
        String defTemplate = (String) event.get(EventAttribute.PROP_EVENT_TEXT);
        String type = (String) event.get(EventAttribute.PROP_TYPE);
        Date time = (Date) event.get(EventAttribute.PROP_TIME);
        String module = (String) event.get(EventAttribute.PROP_MODULE);
        String severity = (String) event.get(EventAttribute.PROP_SEVERITY);
        String thread = (String) event.get(EventAttribute.PROP_SOURCE);
        String sid = (String) event.get(EventAttribute.PROP_USER_SID);
        String username = (String) event.get(EventAttribute.PROP_USER_DISPLAY);

        String desktopId = (String) event.get(EventAttribute.PROP_DESKTOP_ID);
        String poolId = (String) event.get(EventAttribute.PROP_POOL_ID);
        
        
        String ip = (String) event.get(EventAttribute.PROP_CLIENT_IP_ADDRESS);
        
        String template = AdminEventManager.getInstance().getLocaleTemplate(
                type, defTemplate);
        String message = event.renderLocalizedMessage(template, RENDERER);

        String machinename = (String) event.get(EventAttribute.PROP_NODE);
        AdminEvent adminevent = new AdminEvent();
        adminevent.setEventId(eventId);
        adminevent.setTime(time);
        adminevent.setClientIP(ip);
        adminevent.setModule(EventModule.valueOf(module));
        adminevent.setSeverity(EventSeverity.valueOf(severity));
        adminevent.setThread(thread);
        adminevent.setUserSID(sid);
        adminevent.setDesktopId(desktopId);
        adminevent.setPoolId(poolId);
        adminevent.setUsername(username);
        adminevent.setMessage(message);
        
        adminevent.setMachineName(machinename);
        // builds the event sources
        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_DESKTOP_ID,
                EventAttribute.PROP_DESKTOP_DISPLAY, AdminEventSource.Type.POOL);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_MACHINE_ID,
                EventAttribute.PROP_MACHINE_NAME, AdminEventSource.Type.DESKTOP);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_USERDISKPATH_ID,
                EventAttribute.PROP_USERDISKPATH_NAME,
                AdminEventSource.Type.UDD);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_ENDPOINT_ID,
                EventAttribute.PROP_ENDPOINT_DISPLAY, AdminEventSource.Type.CVP);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_THINAPP_ID,
                EventAttribute.PROP_THINAPP_DISPLAY,
                AdminEventSource.Type.THINAPP);

        this.buildUnaryEventSource(adminevent, event,
                EventAttribute.PROP_AGENT_ERROR,
                AdminEventSource.Type.ERROR_CODE);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_APPLICATION_ID,
                EventAttribute.PROP_APPLICATION_DISPLAY_NAME,
                AdminEventSource.Type.APPLICATION);

        this.buildEventSource(adminevent, event, EventAttribute.PROP_FARM_ID,
                EventAttribute.PROP_FARM_DISPLAY_NAME,
                AdminEventSource.Type.FARM);

        this.buildEventSource(adminevent, event,
                EventAttribute.PROP_RDSSERVER_ID,
                EventAttribute.PROP_RDSSERVER_DISPLAY_NAME,
                AdminEventSource.Type.RDSSERVER);

        this.buildUnaryEventSource(adminevent, event,
                EventAttribute.PROP_PROTOCOL_ERROR,
                AdminEventSource.Type.ERROR_CODE);

        return adminevent;
    }

    /**
     * Create an Event source representing a scalar attribute of an Event.
     *
     * @param adminevent
     *            The admin event object
     * @param event
     *            The event object
     * @param sourcevalue
     *            The attribute containing the scalar value
     * @param type
     *            The source type
     */
    private void buildUnaryEventSource(AdminEvent adminevent, Event event,
            EventAttribute sourcevalue, AdminEventSource.Type type) {
        String value = (String) event.get(sourcevalue);
        if (value != null) {
            AdminEventSource source = new AdminEventSource(value, value, type);
            adminevent.addSource(source);
        }
    }

    /**
     * It builds the event source for an admin event.
     *
     * @param adminevent
     *                The admin event object
     * @param event
     *                The event object
     * @param sourceid
     *                The source ID
     * @param sourcename
     *                The source name
     * @param type
     *                The source type
     */
    private void buildEventSource(AdminEvent adminevent, Event event,
            EventAttribute sourceid, EventAttribute sourcename,
            AdminEventSource.Type type) {
        String id = (String) event.get(sourceid);
        if (id != null) {
            String name = (String) event.get(sourcename);
            if (name == null) {
                name = id;
            }
            AdminEventSource source = new AdminEventSource(id, name, type);
            adminevent.addSource(source);
        }
    }
}
