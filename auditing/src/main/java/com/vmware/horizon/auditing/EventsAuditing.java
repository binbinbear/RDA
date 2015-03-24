package com.vmware.horizon.auditing;

import java.util.List;

import com.vmware.horizon.auditing.report.AccumulatedUsageReport;
import com.vmware.horizon.auditing.report.ConcurrentConnectionsReport;
import com.vmware.horizon.auditing.report.Connection;


public interface EventsAuditing extends AutoCloseable{
	public List<Connection> getAllConnections(int days);
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(int days);
	
	public AccumulatedUsageReport getAccumulatedUsageReport(int days);
	
	
	/**
	 * 
	 * @param username  null means all users
	 * @param daysToShow   integer, 30, 7 or any days
	 * @param poolName     null means all pools
	 * @return
	 */
	public List<Connection> getConnections(String username, int daysToShow, String poolName);
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(String poolName, int daysToShow);
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(String poolName, int daysToShow, long period);
	
	public AccumulatedUsageReport getAccumulatedUsageReport(String poolName,int daysToShow);
}
