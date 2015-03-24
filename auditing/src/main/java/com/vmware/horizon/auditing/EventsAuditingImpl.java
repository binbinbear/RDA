package com.vmware.horizon.auditing;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.db.EventDBUtil;
import com.vmware.horizon.auditing.report.AccumulatedUsageReport;
import com.vmware.horizon.auditing.report.ConcurrentConnectionsReport;
import com.vmware.horizon.auditing.report.Connection;
import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizon.auditing.report.ReportUtil;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;

public class EventsAuditingImpl implements EventsAuditing{
	private EventDBUtil dbutil;
	public EventsAuditingImpl(VDIContext vdiContext) throws ADAMConnectionFailedException{
		this.dbutil = new EventDBUtil(vdiContext);
		
	}
	private static final int max_days=365;
	private Logger log = Logger.getLogger(EventsAuditingImpl.class);
	public List<Connection> getAllConnections(int days){
		return this.getConnections("",days, "");
	}
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(int days){
		return this.getConcurrentConnectionsReport("",days);
	}
	
	public AccumulatedUsageReport getAccumulatedUsageReport(int days){
		return this.getAccumulatedUsageReport("",days);
	}
	
	
	/**
	 * 
	 * @param username  null means all users
	 * @param daysToShow   integer, 30, 7 or any days
	 * @param poolName     null means all pools
	 * @return
	 */
	public List<Connection> getConnections(String username, int daysToShow, String poolName){
		if (username == null){
			username= "";
		}
		if (poolName == null){
			poolName="";
		}
		if (daysToShow<=0 || (daysToShow>max_days)){
			log.error("days can't be less than 0 or bigger than :"+ max_days);
			return null;
		}
		List<Event> events= this.dbutil.getEvents(username, daysToShow, poolName);
		return ReportUtil.getConnections(events, username);
		
	}
	
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(String poolName, int daysToShow){
		//time unit in seconds 
				long timeUnit= daysToShow * 600; 
				return this.getConcurrentConnectionsReport(poolName, daysToShow, timeUnit);
	}
	
	public AccumulatedUsageReport getAccumulatedUsageReport(String poolName,int daysToShow){
		List<Connection> connections= this.getConnections(poolName ,daysToShow,"");
		//time unit in seconds 
		return ReportUtil.generateUsageReport(connections);
		
	}

	@Override
	public void close() throws Exception {
		this.dbutil.close();
		
	}

	@Override
	public ConcurrentConnectionsReport getConcurrentConnectionsReport(
			String poolName, int daysToShow, long period) {
		
		if (daysToShow<=0 || (daysToShow>max_days)){
			log.error("days can't be less than 0 or bigger than :"+ max_days);
			return null;
		}
		
		if (period<=0){
			log.error("peirod can be negative!");
			return null;
		}
		List<Event> events= this.dbutil.getEvents("", daysToShow, poolName);
		
		return ReportUtil.getConcurrentConnectionsReport(events, period);
		
	
	}
	
	
}
