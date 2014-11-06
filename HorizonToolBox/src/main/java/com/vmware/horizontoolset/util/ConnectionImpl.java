package com.vmware.horizontoolset.util;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.horizontoolset.usage.Connection;
import com.vmware.horizontoolset.usage.Event;


@JsonIgnoreProperties(value={ "disconnectionEvent"})
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


	@Override
	public int compareTo(Connection o) {
		if (this.getConnectionTime().before(o.getConnectionTime())){
			return 1;
		}else if (this.getConnectionTime().after(o.getConnectionTime())){
			return -1;
		}
		
		return this.getDisconnectionTime().compareTo(o.getConnectionTime());
	}


	@Override
	public Event getConnectionEvent() {
		return this.connectEvent;
	}


	@Override
	public Event getDisconnectionEvent() {
		return this.disConnectEvent;
	}


	@Override
	public String getPoolName() {
		return this.connectEvent.getPoolName();
	}


	@Override
	public String getFarmName() {
		
		return this.connectEvent.getFarmName();
	}



}
