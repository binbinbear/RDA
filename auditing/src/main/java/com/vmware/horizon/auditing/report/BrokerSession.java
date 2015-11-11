package com.vmware.horizon.auditing.report;

import java.util.Date;

public interface BrokerSession extends Comparable<BrokerSession>{
	/**
	 * 
	 * @return user name for broker session
	 */
	public String getUserName() ;

	/**
	 * 
	 * @return Time in seconds
	 */
	public long getBrokerSessionTimeRange();
	 
	/**
	 * 
	 * @return loggedin time. 
	 */
	public Date getLoggedInTime();
	
	/**
	 * 
	 * @return logged out time. Warning: this may be null, so use getBrokerSessionTimeRange() may be better most of time
	 */
	public Date getLoggedOutTime();
	
	
	/**
	 * 
	 * @return IP address of clients which logged in broker
	 */
	public String getClientIP();
	
	/**
	 * 
	 * @return broker session Id
	 */
	public String getBrokerSessionId();
	
	public String getLoggedInTimeStr();
	public String getLoggedOutTimeStr();
}
