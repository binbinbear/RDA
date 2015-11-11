package com.vmware.vdi.admin.be.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.report.EventType;
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
import com.vmware.vdi.admin.be.events.AdminEventDatabase;

/**
 * The class AdminEventLogProvider provides the functionalities to retrieve
 * event data from database.
 *
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
            logger.error("Failed to load events from database:", e);
            throw e;
        } catch (Throwable e) {
            logger.error("Fatal to load events from database:", e);
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
        String message = (String) event.get(EventAttribute.PROP_EVENT_TEXT);
   
        Date time = (Date) event.get(EventAttribute.PROP_TIME);
        String module = (String) event.get(EventAttribute.PROP_MODULE);
       
        String sid = (String) event.get(EventAttribute.PROP_USER_SID);
        String username = (String) event.get(EventAttribute.PROP_USER_DISPLAY);

        String desktopId = (String) event.get(EventAttribute.PROP_DESKTOP_ID);
        String poolId = (String) event.get(EventAttribute.PROP_POOL_ID);
        
        String ip = (String) event.get(EventAttribute.PROP_CLIENT_IP_ADDRESS);


        String machinename = (String) event.get(EventAttribute.PROP_NODE);
        
        String eventType = (String) event.get(EventAttribute.PROP_TYPE);
        
        // peter: need use static string
        String brokerSessionId = (String)event.get(AdminEventDatabase.BROKER_SESSIONID_DESC);
        
        
        AdminEvent adminevent = new AdminEvent();
        adminevent.setEventId(eventId);
        adminevent.setEventType(eventType);
        adminevent.setTime(time);
        adminevent.setClientIP(ip);
        adminevent.setModule(EventModule.valueOf(module));

        adminevent.setUserSID(sid);
        adminevent.setDesktopId(desktopId);
        adminevent.setPoolId(poolId);
        adminevent.setUsername(username);
        adminevent.setMessage(message);
        
        adminevent.setMachineName(machinename);
        adminevent.setBrokerSessionId(brokerSessionId);

     
        return adminevent;
    }


}
