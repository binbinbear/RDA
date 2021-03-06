package com.vmware.vdi.admin.be.events;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.adam.AdamEventMonitorManager;
import com.vmware.vdi.adamwrapper.exceptions.ADAMServerException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.objects.AdminEventMonitorConfiguration;
import com.vmware.vdi.adamwrapper.objects.EventsDatabase;
import com.vmware.vdi.admin.ui.common.Util;
import com.vmware.vdi.dbwrapper.DBConnection;
import com.vmware.vdi.dbwrapper.DatabaseConfig;
import com.vmware.vdi.dbwrapper.exceptions.DBConnectionException;
import com.vmware.vdi.dbwrapper.structure.Column;
import com.vmware.vdi.events.Event;
import com.vmware.vdi.events.enums.EventAttribute;
import com.vmware.vdi.events.enums.EventSeverity;
import com.vmware.vdi.events.server.forwarders.database.connectors.EventDBConnection;

/**
 * The class AdminEventDatabase provides the functionalities to retrieve event
 * data from database.
 *
 *
 */
public class AdminEventDatabase extends DBConnection {

    public AdminEventDatabase(EventsDatabase dbConfig) throws DBConnectionException {
        super(new DatabaseConfig(dbConfig.getHostname(), dbConfig.getPort(),
                DatabaseConfig.DatabaseType.valueOf(dbConfig.getServerType()
                        .toString()), dbConfig.getDatabaseName(), dbConfig
                        .getUsername(), dbConfig.getPassword(), dbConfig
                        .getTablePrefix()));

        this.event_longevity = dbConfig.getEventLongevity();
    }

    protected EventsDatabase.EventLongevity event_longevity;

    private static final Logger logger = Logger
            .getLogger(AdminEventDatabase.class);

    private static final int EVENT_PAGE_SIZE = 2000;

    private static final int ARGUMENT_PAGE_SIZE = 500;

    private AdminEventDbQuery dbquery = null;

    private static final String ADMIN_EVENT_COUNT = "AdminEventCount";

    private static boolean adminEventCountInitialized = false;

    private static int adminEventCount = 0;

    public static final String BROKER_SESSIONID_DESC = "BrokerSessionId";

    /**
     * Query the default number of events the back end fetches with each
     * database query.
     *
     * On first invocation, check whether an override has been set in LDAP, and
     * if so use it.
     *
     * If there is no override in LDAP, or if it not a legal value (non-integer
     * or less than zero), then fallback to the default EVENT_PAGE_SIZE value.
     *
     * @param ctx
     *                VDI context
     * @return The number of rows
     */
    public static synchronized int getAdminEventCount(VDIContext ctx) {
        if (!adminEventCountInitialized) {
            String rows = null;
            Map<String, String> nvps = null;
            AdminEventMonitorConfiguration eventConfiguration;
            try {
                eventConfiguration = AdamEventMonitorManager.getInstance()
                        .getEventMonitorConfguration(ctx);
                nvps = eventConfiguration.getNamesToValues();
                rows = nvps.get(ADMIN_EVENT_COUNT);
            } catch (ADAMServerException e) {
                logger.debug("Failed to obtain event configuration from LDAP");
            }
            if (!Util.isEmpty(rows)) {
                try {
                    adminEventCount = Integer.parseInt(rows);
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Number of admin events to display set from LDAP: "
                                        + adminEventCount);
                    }
                } catch (NumberFormatException e) {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Invalid value for number of admin events to display set from LDAP: "
                                        + rows);
                    }
                }
            }
            if (adminEventCount <= 0) {
                adminEventCount = EVENT_PAGE_SIZE;
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("Number of admin events to display set by default: "
                                    + adminEventCount);
                }
            }
            adminEventCountInitialized = true;
        }
        return adminEventCount;
    }

    /**
     * It tries to create a database connection.
     *
     * @param dbconfig
     *                The database configuration
     * @return True when the database connection is established successfully,
     *         false when failed to establish the database connection
     *
     */
    public boolean connect(EventsDatabase dbconfig) throws Exception {
        // build the query provider by database type
        switch (config.getServerType()) {
        case SQLSERVER:
            this.dbquery = new AdminEventSqlServerQuery(config.getTablePrefix());
            break;
        case ORACLE:
            this.dbquery = new AdminEventOracleQuery(config.getTablePrefix());
            break;
        default:
            logger.debug("Use MS SQL Server query for unknown DB: "
                    + config.getServerType());
            return false;
        }

        try {
            super.connect();
        } catch (DBConnectionException e) {
            logger.debug("Could not connect to events database: "
                    + e.getMessage(), e);
            throw e;
        }

        return this.checkConnection();
    }

    /**
     * It returns the counts for each event severity.
     *
     * @param filter
     *                The event filter
     * @return The list of event severities and their counts
     */
    public Map<EventSeverity, Integer> readSeverityCount(AdminEventFilter filter) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -filter.getFilterDays());
        Date time = calendar.getTime();

        String query = this.dbquery.getSeverityCountQuery();

        // load the event objects
        Map<EventSeverity, Integer> counts = new HashMap<EventSeverity, Integer>();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, new java.sql.Timestamp(time.getTime()));
            stmt.execute();

            this.readSeverityCount(counts, stmt);
        } catch (SQLException e) {
            logger.debug("Failed to load event counts from database:", e);
            return null;
        } finally {
            this.closeStatement(stmt);
        }
        return counts;
    }

    /**
     * It returns the total event counts.
     *
     * @param recent
     *                The flag if to count from recent event table or history
     *                table.
     * @return The total event counts
     */
    public int getTotalEventCount(boolean recent) {
        String query = this.dbquery.getTotalEventCountQuery(recent);

        // load the event objects
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(query);
            stmt.execute();
            return this.readTotalEventCounts(stmt);
        } catch (SQLException e) {
            logger.debug("Failed to load history event counts:", e);
        } finally {
            this.closeStatement(stmt);
        }
        return 0;
    }

    /**
     * It loads the event objects from database.
     *
     * @param filter
     *                The event filter
     * @return The list of event IDs and their objects
     */
    public Map<Integer, Event> readEvents(VDIContext ctx,
            AdminEventFilter filter) {
    	logger.info("Start query events from DB ");
        // calculate the page size and starting index
        int index = filter.getPageIndex() * filter.getPageSize();
        int count = filter.getPageSize();
        if (count <= 0) {
            count = getAdminEventCount(ctx);
        }

        // calculate the time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -filter.getFilterDays());
        Date time = calendar.getTime();

        String query = this.dbquery.getEventQuery(filter, index, count);
        logger.info("Query string: " + query + " Filter: " + filter.getFilterText());

        // load the event objects
        Map<Integer, Event> events = new HashMap<Integer, Event>();
        PreparedStatement stmt = null;
        try {
        	logger.info("Start prepaire statement");
            // prepare the event query statement
            stmt = conn.prepareStatement(query);

            // prepare the event query parameters
            int param = 1;
            stmt.setTimestamp(param++, new java.sql.Timestamp(time.getTime()));
            //stmt.setString(param++, filter.getFilterText());
            logger.info("Start Execute statement");
            // execute the event query statement
            stmt.execute();
            logger.info("Start reading events");
            // load event objects from database
            this.readEvents(events, stmt, index, count);
        } catch (SQLException e) {
            logger.error("Failed to load events from database:", e);
            return null;
        } finally {
            this.closeStatement(stmt);
        }


        logger.info("DB query finished successfully with events:"+events.size());
        // load the event arguments for event objects
       // if (!this.readEventArguments(events, time)) {
        //    return null;
      //  }
        return events;
    }

    /**
     * It reads the event counts for each severity from the database statement.
     *
     * @param counts
     *                The list of event severities and their counts
     * @param stmt
     *                The database statement to retrieve event counts
     * @throws SQLException
     */
    private void readSeverityCount(Map<EventSeverity, Integer> counts,
            PreparedStatement stmt) throws SQLException {
        ResultSet resultset = stmt.getResultSet();
        while (resultset != null) {
            while (resultset.next()) {
                readSeverityCount(counts, resultset);
            }
            if (stmt.getMoreResults()) {
                resultset = stmt.getResultSet();
            } else {
                resultset = null;
            }
        }
    }

    /**
     * It reads the total event counts for the event table.
     *
     * @param stmt
     *                The database statement
     * @return The total event counts
     * @throws SQLException
     */
    private int readTotalEventCounts(PreparedStatement stmt)
            throws SQLException {
        ResultSet resultset = stmt.getResultSet();
        if (resultset != null) {
            if (resultset.next()) {
                int columnIndex = 1;
                return resultset.getInt(columnIndex++);
            }
        }
        return 0;
    }

    /**
     * It reads the event severity and its count from result set.
     *
     * @param counts
     *                The list of event severities and their counts.
     * @param resultset
     *                The result set
     * @throws SQLException
     */
    private void readSeverityCount(Map<EventSeverity, Integer> counts,
            ResultSet resultset) throws SQLException {
        int columnIndex = 1;
        int count = resultset.getInt(columnIndex++);
        String severity = resultset.getString(columnIndex++);

        counts.put(EventSeverity.valueOf(severity), count);
    }

    /**
     * It loads the event objects from the database statement.
     *
     * @param events
     *                The list of event IDs and their objects
     * @param stmt
     *                The database statement to retrieve events
     * @param index
     *                The start index
     * @param count
     *                The number of events to load
     * @throws SQLException
     */
    private void readEvents(Map<Integer, Event> events, PreparedStatement stmt,
            int index, int count) throws SQLException {
        ResultSet resultset = stmt.getResultSet();
        int biggestEventID = -1;
        int smallestEventID = Integer.MAX_VALUE;
        while ((resultset != null) && (events.size() < count)) {
            while (resultset.next() && (events.size() < count)) {
                int eventId = resultset.getInt(1);
                if (eventId > biggestEventID){
                	biggestEventID = eventId;
                }
                if (eventId < smallestEventID){
                	smallestEventID = eventId;
                }
                Event event = this.readEvent(resultset);
                if (event != null) {
                    events.put(eventId, event);
                }
            }
            if (stmt.getMoreResults()) {
                resultset = stmt.getResultSet();
            } else {
                resultset = null;
            }
        }


        String argquery = this.dbquery.getArgumentsQuery();

        PreparedStatement argstmt = null;
        try {
        	logger.info("Start prepaire argument statement");
            // prepare the event query statement
        	argstmt = conn.prepareStatement(argquery);

            // prepare the event query parameters
        	argstmt.setInt(1, smallestEventID);
        	argstmt.setInt(2, biggestEventID);
            logger.info("Start Execute Argument statement");
            // execute the event query statement
            argstmt.execute();
            logger.info("Start reading arguments");
            // load event objects from database

            ResultSet argresultset = argstmt.getResultSet();

            while ((argresultset != null)) {
                while (argresultset.next() ) {
                    int eventId = argresultset.getInt(1);

                    Event event = events.get(eventId);
                    if (event != null) {
                        //get argument here
                    	this.readArgument(argresultset, event);
                    }
                }
                if (argstmt.getMoreResults()) {
                	argresultset = argstmt.getResultSet();
                } else {
                	argresultset = null;
                }
            }

        } catch (SQLException e) {
            logger.error("Failed to load events from arugment table:", e);

        } finally {
            this.closeStatement(argstmt);
        }

    }

    /**
     * It loads an event object from database.
     *
     * @param resultset
     *                The result set of database query
     * @return The event object
     * @throws SQLException
     */
    private Event readEvent(ResultSet resultset) throws SQLException {
        int columnIndex = 1;
        int eventId = resultset.getInt(columnIndex++);
        Date time = resultset.getTimestamp(columnIndex++);
        String type = resultset.getString(columnIndex++);

        String module = resultset.getString(columnIndex++);

        String node = resultset.getString(columnIndex++);
        String text = resultset.getString(columnIndex++);


        Event event = new Event();
        event.put(EventDBConnection.EVENTID, new Integer(eventId));
        event.put(EventAttribute.PROP_TIME, time);
        event.put(EventAttribute.PROP_TYPE, type);

        event.put(EventAttribute.PROP_MODULE, module);


        event.put(EventAttribute.PROP_NODE, node);
        event.put(EventAttribute.PROP_EVENT_TEXT, text);



        return event;
    }


    private void readArgument(ResultSet resultset, Event event) throws SQLException {
//    	buffer.append(EventDBConnection.EVENTID);
//        buffer.append(", Name");
//        buffer.append(", StrValue");
        int columnIndex = 1;
        int eventId = resultset.getInt(columnIndex++);

        String name = resultset.getString(columnIndex++);
        String strvalue = resultset.getString(columnIndex++);
        if (EventAttribute.PROP_DESKTOP_ID.name.equalsIgnoreCase(name)){
        	event.put(EventAttribute.PROP_DESKTOP_ID, strvalue);
        }else if (EventAttribute.PROP_POOL_ID.name.equalsIgnoreCase(name)){
        	event.put(EventAttribute.PROP_POOL_ID, strvalue);
        }else if (EventAttribute.PROP_CLIENT_IP_ADDRESS.name.equalsIgnoreCase(name)){
        	event.put(EventAttribute.PROP_CLIENT_IP_ADDRESS, strvalue);
        }else if (BROKER_SESSIONID_DESC.equalsIgnoreCase(name)) {
        	event.put(BROKER_SESSIONID_DESC, strvalue);
        }
        else {
        	event.put(name, strvalue);
        }
    }



    /**
     * It closes a database statement.
     *
     * @param stmt
     *                The database statement to close
     */
    private void closeStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.debug("Failed to close SQL statement:", e);
            }
        }
    }

    private static final String TOOLBOXTABLE_NAME = "Toolbox";
    private static final String TOOLBOX_Column_ID = "ID";
    private static final String TOOLBOX_Column_KEY1 = "KEY1";
    private static final String TOOLBOX_Column_KEY2 = "KEY2";
    private static final String TOOLBOX_Column_Value = "VALUE";

    //return true if the table is created successfully
    //return false if the table exists there
    //throw exception if the table can't be created
    public boolean createToolobxTable() throws DBConnectionException{
    	if (isToolboxTableExists()){
    		//table exist, not create again.
    		return false;
    	}
    	Column[] cols = new Column[4];
        Column id = new Column(TOOLBOX_Column_ID, Column.DataType.INTEGER);
        id.setPrimaryKey(true);

        Column k1 = new Column(TOOLBOX_Column_KEY1, Column.DataType.TEXT);
        k1.setIndexed(true);

        Column k2 = new Column(TOOLBOX_Column_KEY2, Column.DataType.TEXT);

        Column value = new Column(TOOLBOX_Column_Value, Column.DataType.TEXT);

        cols[0] = id;
        cols[1] = k1;
        cols[2] = k2;
        cols[3] = value;

    	super.getWriter().createTable(TOOLBOXTABLE_NAME, cols);

    	return true;
    }

    public boolean isToolboxTableExists() throws DBConnectionException{

    	return super.getReader().tableExists(TOOLBOXTABLE_NAME);
    }



}
