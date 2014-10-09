package com.vmware.horizontoolset.usage;

import java.util.Date;


public interface Connection extends Comparable<Connection>{
	public String getUserName() ;
	public String getMachineName();
	public String getPoolName();
	/**
	 * 
	 * @return Time in seconds
	 */
	public long getUsageTime();
	
	public Date getConnectionTime();
	
	public Date getDisconnectionTime();

	public Event getConnectionEvent();
	public Event getDisconnectionEvent();

	public String getConnectionType();
}
