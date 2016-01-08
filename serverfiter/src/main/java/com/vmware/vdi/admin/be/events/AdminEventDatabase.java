package com.vmware.vdi.admin.be.events;


import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.objects.EventsDatabase;
import com.vmware.vdi.dbwrapper.DBConnection;
import com.vmware.vdi.dbwrapper.DatabaseConfig;
import com.vmware.vdi.dbwrapper.exceptions.DBConnectionException;

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

    private AdminEventDbQuery dbquery = null;



    public static final String BROKER_SESSIONID_DESC = "BrokerSessionId";

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




    private static final String TOOLBOXTABLE_NAME = "Toolbox";


    public boolean isToolboxTableExists() throws DBConnectionException{

    	return super.getReader().tableExists(TOOLBOXTABLE_NAME);
    }




}
