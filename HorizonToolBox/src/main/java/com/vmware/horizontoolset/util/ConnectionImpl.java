package com.vmware.horizontoolset.util;


import java.util.Date;

import com.vmware.horizontoolset.usage.Connection;
import com.vmware.horizontoolset.usage.Event;

public class ConnectionImpl implements Connection{

	private String username;
	private Event connectEvent;
	private Event disConnectEvent;
	
	
	
	public ConnectionImpl(Event connectEvent, Event disconnectEvent){
		this.username = connectEvent.getUserName();
		this.connectEvent = connectEvent;
		this.disConnectEvent = disconnectEvent;
	}
	
	
	@Override
	public String getUserName() {
		return username;
	}



	@Override
	public long getUsageTime() {
		
		return (this.getDisconnectionTime().getTime() - this.getConnectionTime().getTime())/1000;
	}


	@Override
	public String getMachineName() {
		return this.connectEvent.getMachineDNSName();
	}


	@Override
	public Date getConnectionTime() {
		return this.connectEvent.getTime();
	}


	@Override
	public Date getDisconnectionTime() {
		return this.disConnectEvent.getTime();
	}




}
