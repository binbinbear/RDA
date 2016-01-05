package com.vmware.horizon.auditing;


import java.util.List;

import com.vmware.horizon.auditing.report.AccumulatedUsageReport;
import com.vmware.horizon.auditing.report.ConcurrentConnectionsReport;
import com.vmware.horizon.auditing.report.Connection;
import com.vmware.horizon.auditing.report.BrokerSession;


public interface EventsAuditing extends AutoCloseable{
	/**
	 * 
	 * @param days   the number of days till today, for example, 7 for the last week; should be less or equal than 31
	 * @return  a List of Connection Object
	 */
	public List<Connection> getAllConnections(int days);
	
	/**
	 * 
	 * @param days the number of days till today, for example, 7 for the last week; should be less or equal than 31
	 * @return ConcurrentConnectionsReport
	 */
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(int days);
	
	/**
	 * 
	 * @param days ConcurrentConnectionsReport
	 * @return AccumulatedUsageReport
	 */
	public AccumulatedUsageReport getAccumulatedUsageReport(int days);
	
	
	/**
	 * 
	 * @param username  null means all users
	 * @param daysToShow   integer, 30, 7 or any days; should be less or equal than 31
	 * @param poolName     null means all pools
	 * @return
	 */
	public List<Connection> getConnections(String username, int daysToShow, String poolName);
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(String poolName, int daysToShow);
	
	/**
	 * 
	 * @param poolName null means all pools
	 * @param daysToShow   integer, 30, 7 or any days; should be less or equal than 31
	 * @param period  the report period in seconds, for example,  3600 means that report concurrent connection number once a hour
	 * @return ConcurrentConnectionsReport
	 */
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(String poolName, int daysToShow, long period);
	
	public AccumulatedUsageReport getAccumulatedUsageReport(String poolName,int daysToShow);
	
	/**
	 * 
	 * @param username  null means all users
	 * @param daysToShow   integer, 30, 7 or any days; should be less or equal than 31
	 * @return
	 */
	public List<BrokerSession> getBrokerSessions(String username, int daysToShow);
}
