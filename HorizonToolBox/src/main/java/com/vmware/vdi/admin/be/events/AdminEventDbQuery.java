package com.vmware.vdi.admin.be.events;

import org.apache.log4j.Logger;

import com.vmware.vdi.events.enums.EventAttribute;
import com.vmware.vdi.events.server.forwarders.database.connectors.EventDBConnection;

/**
 * The class AdminEventDbQuery provides the functionalities to build database
 * queries by database type.
 *
 * @author dliu
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

    /**
     * It returns the query to retrieve the event arguments.
     *
     * It asks 'count' of input parameters.
     *
     * @param count
     *                The number of input parameters
     * @return The database query
     */
    public String getArgumentQuery(int count) {
        // SELET *
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT ");
        buffer.append(this.argumentColumnList);

        // FROM argument
        buffer.append(" FROM ");
        buffer.append(this.argumentTableName);

        // WHERE eventid IN (?,...,?)
        buffer.append(" WHERE ");
        buffer.append(EventDBConnection.EVENTID);
        buffer.append(" IN (?");
        for (int index = 1; index < count; index++) {
            buffer.append(",?");
        }
        buffer.append(")");

        String query = buffer.toString();
        return query;
    }

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
        buffer.append("," + EventAttribute.PROP_SEVERITY.name);
        buffer.append("," + EventAttribute.PROP_MODULE.name);
        buffer.append("," + EventAttribute.PROP_SOURCE.name);
        buffer.append("," + EventAttribute.PROP_ACK.name);
        buffer.append("," + EventAttribute.PROP_NODE.name);
        buffer.append("," + EventAttribute.PROP_EVENT_TEXT.name);
        buffer.append("," + EventAttribute.PROP_GROUP_ID.name);

        String mandatoryAttributes = buffer.toString();
        return mandatoryAttributes;
    }

    /**
     * Returns a comma-separated list of optional "columns" (attributes).
     *
     * @return The comma-separated list of optional attributes
     */
    protected String getOptionalEventAttributes() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(EventAttribute.PROP_USER_SID.name);
        buffer.append("," + EventAttribute.PROP_FOLDER_PATH.name);
        buffer.append("," + EventAttribute.PROP_DESKTOP_ID.name);
        buffer.append("," + EventAttribute.PROP_MACHINE_ID.name);
        buffer.append("," + EventAttribute.PROP_USERDISKPATH_ID.name);
        buffer.append("," + EventAttribute.PROP_ENDPOINT_ID.name);
        buffer.append("," + EventAttribute.PROP_THINAPP_ID.name);
        buffer.append("," + EventAttribute.PROP_LUN_ID.name);
        buffer.append("," + EventAttribute.PROP_APPLICATION_ID.name);
        buffer.append("," + EventAttribute.PROP_FARM_ID.name);
        buffer.append("," + EventAttribute.PROP_RDSSERVER_ID.name);

        String optionalAttributes = buffer.toString();
        return optionalAttributes;
    }

    /**
     * It returns the event columns to return from event query.
     *
     * @return The list of event columns
     */
    private String getEventList() {
        return getMandatoryEventAttributes() + ","
                + getOptionalEventAttributes();
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
        buffer.append(", Type");

        buffer.append(", IntValue");
        buffer.append(", StrValue");
        buffer.append(", TimeValue");
        buffer.append(", BooleanValue");

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
        String[] optionalEventAttributes = getOptionalEventAttributes().split(
                ",");

        // mandatory columns
        sb.append(" ");
        // prepend table name to EventId column to disambiguate it
        sb.append(this.eventTableName).append(".")
                .append(getMandatoryEventAttributes());
        // optional columns
        for (String attr : optionalEventAttributes) {
            sb.append(",").append(attr).append(".StrValue AS ").append(attr);
        }
        sb.append(" FROM ");

        return sb.toString();
    }

    /**
     * Return a LEFT JOIN clause built around the optional attributes.
     *
     * @return The LEFT JOIN clause
     */
    protected String getLeftJoinClause() {
        StringBuilder sb = new StringBuilder();
        String[] optionalEventAttributes = getOptionalEventAttributes().split(
                ",");

        for (String attr : optionalEventAttributes) {
            sb.append(" LEFT OUTER JOIN (SELECT EventID, StrValue FROM ");
            sb.append(this.argumentTableName);
            sb.append(" WHERE(Name = '");
            sb.append(attr);
            sb.append("')) ");
            sb.append(attr);
            sb.append(" ON ");
            sb.append(this.eventTableName);
            sb.append(".EventID = ");
            sb.append(attr);
            sb.append(".EventID");
        }

        return sb.toString();
    }
}

/**
 * The class AdminEventSqlServerQuery implements the queries for events and
 * arguments with MS SQL Sever database.
 *
 * @author dliu
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
        buffer.append(getLeftJoinClause());
        buffer.append(" WHERE ");
        buffer.append(EventAttribute.PROP_TIME);
        buffer.append(">=?");
        
        buffer.append(" AND ");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append("=?");

        buffer.append(" ORDER BY ");
        buffer.append(EventAttribute.PROP_TIME);
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

        // (SELECT v.*, ROMNUM AS rn FROM (...))
        buffer.append("(SELECT v.*, ROWNUM AS rn FROM ");

        // (SELECT ROWNUM, * FROM event WHERE time=? ORDER BY time DESC,
        // eventid DESC) AS v
        buffer.append("(SELECT");
        buffer.append(getColumnListFrom());
        buffer.append(this.eventTableName);
        buffer.append(getLeftJoinClause());
        buffer.append(" WHERE ");
        buffer.append(EventAttribute.PROP_TIME);
        buffer.append(">=?");
        buffer.append(" AND ");
        buffer.append(EventAttribute.PROP_MODULE.name);
        buffer.append("=?");
        
        buffer.append(" ORDER BY ");
        buffer.append(EventAttribute.PROP_TIME);
        buffer.append(" DESC");
      
        buffer.append(") v");

        buffer.append(")");

        // WHERE (ROWNUM>=index) AND (ROWNUM<index+count)
        buffer.append(" WHERE (rn>");
        buffer.append(index);
        buffer.append(") AND (rn<=");
        buffer.append(index + count);
        buffer.append(")");

        String query = buffer.toString();
        return query;
    }
}
