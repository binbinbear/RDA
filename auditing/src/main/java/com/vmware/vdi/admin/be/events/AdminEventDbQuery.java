package com.vmware.vdi.admin.be.events;

import org.apache.log4j.Logger;

import com.vmware.vdi.events.enums.EventAttribute;
import com.vmware.vdi.events.server.forwarders.database.connectors.EventDBConnection;

/**
 * The class AdminEventDbQuery provides the functionalities to build database
 * queries by database type.
 *
 */
public abstract class AdminEventDbQuery {
    private static final Logger logger = Logger
            .getLogger(AdminEventDbQuery.class);

    protected final String eventTableName;

    protected final String eventHistoryTableName;

    protected final String argumentTableName;

    protected final String eventColumnList;

    protected final String argumentColumnList;

    /**
     * Constructor.
     *
     * @param tablePrefixName
     */
    public AdminEventDbQuery(String tablePrefixName) {
        this.eventTableName = tablePrefixName
                + EventDBConnection.EVENT_META_TABLE;
        this.eventHistoryTableName = tablePrefixName
                + EventDBConnection.EVENT_META_HISTORICAL_TABLE;
        this.argumentTableName = tablePrefixName
               + EventDBConnection.EVENT_DATA_TABLE;

        this.eventColumnList = this.getEventList();
        this.argumentColumnList = this.getArgumentList();
    }

    /**
     * It returns the query to retrieve the counts for each severity.
     *
     * It asks for one input parameter of time.
     *
     * @return The database query
     */
    public String getSeverityCountQuery() {
        // SELECT COUNT(*) AS count, severity
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT COUNT(*) AS count, ");
        buffer.append(EventAttribute.PROP_SEVERITY.name);

        // FROM vdm_event
        buffer.append(" FROM ");
        buffer.append(this.eventTableName);

        // WHERE time < ?
        buffer.append(" WHERE ");
        buffer.append(EventAttribute.PROP_TIME.name);
        buffer.append(">=?");

        // GROUP BY severity
        buffer.append(" GROUP BY ");
        buffer.append(EventAttribute.PROP_SEVERITY.name);

        String query = buffer.toString();
        return query;
    }

    /**
     * It returns the query to retrieve the total counts for history events.
     *
     * @param recent
     *                The flag if to count from recent event table.
     * @return The database query
     */
    public String getTotalEventCountQuery(boolean recent) {
        // SELECT COUNT(*) AS count
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT COUNT(*) AS count ");

        // FROM vdm_event
        buffer.append(" FROM ");
        if (recent) {
            buffer.append(this.eventTableName);
        } else {
            buffer.append(this.eventHistoryTableName);
        }

        String query = buffer.toString();
        return query;
    }

    /**
     * It returns the query to retrieve the events of certain page.
     *
     * It asks for one input parameter of time.
     *
     * @param filter
     *                The event filter
     * @param index
     *                The first index or number of events to skip
     * @param count
     *                The page size
     *
     * @return The database query
     */
    public abstract String getEventQuery(AdminEventFilter filter, int index,
            int count);


    public abstract String getArgumentsQuery();

    

    /**
     * It builds the event source filtering string.
     *
     * @param filter
     *                The event filter
     * @return The filter string
     */
    protected String getEventSourceQuery(AdminEventFilter filter) {
        StringBuilder buffer = new StringBuilder();

        // AND id=? or AND id IS NOT NULL
        AdminEventSource source = filter.getSource();
        if (source != null) {
            buffer.append(" AND ");
            switch (source.getType()) {
            case POOL:
                buffer.append(EventAttribute.PROP_DESKTOP_ID.name);
                break;
            case DESKTOP:
                buffer.append(EventAttribute.PROP_MACHINE_ID.name);
                break;
            case UDD:
                buffer.append(EventAttribute.PROP_USERDISKPATH_ID.name);
                break;
            case THINAPP:
                buffer.append(EventAttribute.PROP_THINAPP_ID.name);
                break;
            case CVP:
                buffer.append(EventAttribute.PROP_ENDPOINT_ID.name);
                break;
            case APPLICATION:
                buffer.append(EventAttribute.PROP_APPLICATION_ID.name);
                break;
            case FARM:
                buffer.append(EventAttribute.PROP_FARM_ID.name);
                break;
            case RDSSERVER:
                buffer.append(EventAttribute.PROP_RDSSERVER_ID.name);
                break;
            default:
                logger.debug("Unknown event source: " + source.getType());
                filter.setSource(null);
                return "";
            }
            if (filter.getSource().getId() != null) {
                buffer.append(".StrValue=?");
            } else {
                buffer.append(" IS NOT NULL");
                filter.setSource(null);
            }
        }

        String query = buffer.toString();
        return query;
    }

    /**
     * It builds the event user filtering string.
     *
     * @param filter
     *                The event filter
     * @return The filter string
     */
    protected String getEventUserQuery(AdminEventFilter filter) {
        StringBuilder buffer = new StringBuilder();

        // AND usersid=?
        if (filter.getSid() != null) {
            buffer.append(" AND ");
            buffer.append(EventAttribute.PROP_USER_SID.name);
            buffer.append("=?");
        }

        String query = buffer.toString();
        return query;
    }

    /**
     * Returns a comma-separated list of mandatory "columns" (attributes).
     *
     * @return The comma-separated list of mandatory attributes
     */
    protected String getMandatoryEventAttributes() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(EventDBConnection.EVENTID);
        buffer.append("," + EventAttribute.PROP_TIME);
        buffer.append("," + EventAttribute.PROP_TYPE.name);
        buffer.append("," + EventAttribute.PROP_MODULE.name);
        buffer.append("," + EventAttribute.PROP_NODE.name);
        buffer.append("," + EventAttribute.PROP_EVENT_TEXT.name);
        
        String mandatoryAttributes = buffer.toString();
        return mandatoryAttributes;
    }



    /**
     * It returns the event columns to return from event query.
     *
     * @return The list of event columns
     */
    private String getEventList() {
        return getMandatoryEventAttributes() ;
    }

    /**
     * It returns the argument columns to retrieve from argument query.
     *
     * @return The list of argument columns
     */
    private String getArgumentList() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(", Name");
        buffer.append(", StrValue");

        String query = buffer.toString();
        return query;
    }

    /**
     * Return a projection clause built around the optional attributes.
     *
     * @return The projection clause
     */
    protected String getColumnListFrom() {
        StringBuilder sb = new StringBuilder();


        // mandatory columns
        sb.append(" ");
        // prepend table name to EventId column to disambiguate it
        sb.append(this.eventTableName).append(".")
                .append(getMandatoryEventAttributes());
      
        sb.append(" FROM ");

        return sb.toString();
    }


}

/**
 * The class AdminEventSqlServerQuery implements the queries for events and
 * arguments with MS SQL Sever database.
 *
 *
 */
class AdminEventSqlServerQuery extends AdminEventDbQuery {

    public AdminEventSqlServerQuery(String tablePrefixName) {
        super(tablePrefixName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vdi.admin.be.events.AdminEventDbQuery#getEventQuery(com.vmware
     *      .vdi.admin.be.events.AdminEventFilter, int, int)
     */
    @Override
    public String getEventQuery(AdminEventFilter filter, int index, int count) { // SELEC TOP count * FROM
        StringBuilder buffer = new StringBuilder();

        // (SELECT TOP index+count * FROM event WHERE time=? ORDER BY time DESC,
        // eventid DESC) AS v
        buffer.append("SELECT TOP ");
        buffer.append(count);
        buffer.append(getColumnListFrom());
        buffer.append(this.eventTableName);
      
        buffer.append(" WHERE ");
        buffer.append(EventAttribute.PROP_TIME);
        buffer.append(">=?");
        
        buffer.append(" AND (");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append(" = 'Agent' ");
        buffer.append(" OR (");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append(" = 'Broker'");
        buffer.append(" AND ");
        buffer.append(EventAttribute.PROP_TYPE.name);
        buffer.append(" IN ('BROKER_USERLOGGEDIN','BROKER_USERLOGGEDOUT')))");


        buffer.append(" ORDER BY ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(" DESC");
        
        String query = buffer.toString();
        
        return query;
    }

	@Override
	public String getArgumentsQuery() {
		// TODO Auto-generated method stub
		 // SELEC TOP count * FROM
        StringBuilder buffer = new StringBuilder();

        buffer.append("SELECT  ");
        buffer.append(this.argumentColumnList);
        buffer.append(" FROM  ");
        buffer.append(this.argumentTableName);
      
        buffer.append(" WHERE ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(">=?");
        
        buffer.append(" AND ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append("<=?");
        
        buffer.append(" ORDER BY ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(" DESC");
        
        String query = buffer.toString();
        
        return query;
    
	}
}

class AdminEventOracleQuery extends AdminEventDbQuery {

    public AdminEventOracleQuery(String tablePrefixName) {
        super(tablePrefixName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vdi.admin.be.events.AdminEventDbQuery#getEventQuery(com
     *      .vmware .vdi.admin.be.events.AdminEventFilter, int, int)
     */
    @Override
    public String getEventQuery(AdminEventFilter filter, int index, int count) { // SELEC * FROM
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT ");
        buffer.append(this.eventColumnList);
        buffer.append(" FROM ");

        buffer.append(this.eventTableName);
        
        buffer.append(" WHERE ");
        buffer.append(EventAttribute.PROP_TIME);
        buffer.append(">=?");
   
        buffer.append(" AND (");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append(" = 'Agent' ");
        buffer.append(" OR (");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append(" = 'Broker'");
        buffer.append(" AND ");
        buffer.append(EventAttribute.PROP_TYPE.name);
        buffer.append(" IN ('BROKER_USERLOGGEDIN','BROKER_USERLOGGEDOUT')))");
        
        buffer.append(" ORDER BY ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(" DESC");


        String query = buffer.toString();
        return query;
    }

	@Override
	public String getArgumentsQuery() { // SELEC * FROM
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT ");
        buffer.append(this.argumentColumnList);
        buffer.append(" FROM ");

        buffer.append(this.argumentTableName);
        
        buffer.append(" WHERE ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(">=?");
        buffer.append(" AND ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append("<=?");
        
        buffer.append(" ORDER BY ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(" DESC");


        String query = buffer.toString();
        return query;
    }
}
