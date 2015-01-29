package com.vmware.horizontoolset.usage;

import java.util.Date;


public interface Connection extends Comparable<Connection>{
	public String getUserName() ;
	public String getMachineName();
	

	/**
	 * 
	 * @return pool name for desktop connection
	 */
	public String getPoolName();
	
	/**
	 * 
	 * @return farm name for app connection
	 */
	public String getFarmName();
	/**
	 * 
	 * @return Time in seconds
	 */
	public long getUsageTime();
	
	public Date getConnectionTime();
	
	public Date getDisconnectionTime();

	public String getDisConnectionTimeStr();
	public String getConnectionTimeStr();
	public Event getConnectionEvent();
	public Event getDisconnectionEvent();


}
