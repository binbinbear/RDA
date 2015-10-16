package com.vmware.horizon.auditing.report;

import java.util.Date;


public interface Connection extends Comparable<Connection>{
	public String getUserName() ;
	public String getMachineName();
	

	/**
	 * 
	 * @return pool name for desktop connection, or farm name for application/rdsh connections
	 */
	public String getSourceName();
	

	/**
	 * 
	 * @return Time in seconds
	 */
	public long getUsageTime();
	
	public Date getConnectionTime();
	
	/**
	 * 
	 * @return loggedin time. Warning: this may be null, so use getConnectionTime() may be better most of time
	 */
	public Date getLoggedInTime();
	
	/**
	 * 
	 * @return delay time for this login in MILLISECONDS, loggedInTime - connectionTime. 0 means unknown
	 */
	public long getLoginDelayTime();
	
	public Date getDisconnectionTime();

	public String getDisConnectionTimeStr();
	public String getConnectionTimeStr();
	public Event getConnectionEvent();
	public Event getDisconnectionEvent();

	public String getClientIP();

}
