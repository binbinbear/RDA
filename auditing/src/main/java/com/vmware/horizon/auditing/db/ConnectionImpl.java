package com.vmware.horizon.auditing.db;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.horizon.auditing.report.Connection;
import com.vmware.horizon.auditing.report.Event;


@JsonIgnoreProperties(value={"connectionEvent", "disconnectionEvent"})
public class ConnectionImpl implements Connection{

	private String username;
	private Event connectEvent;
	private Event disConnectEvent;
	
	private Event loggedInEvent;
	public ConnectionImpl(Event connectEvent, Event loggedInEvent , Event disconnectEvent){
		this.username = connectEvent.getUserName();
		this.loggedInEvent = loggedInEvent;
		this.connectEvent = connectEvent;
		this.disConnectEvent = disconnectEvent;
	}

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
		if (this.getDisconnectionTime().getTime() == 0){
			return ((new Date()).getTime() - this.getConnectionTime().getTime())/1000; 
		}
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
		
		return this.getDisconnectionTime().compareTo(o.getDisconnectionTime());
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
	public String getSourceName() {
		return this.connectEvent.getSourceName();
	}


	@Override
	public Date getLoggedInTime() {
		if (this.loggedInEvent == null) return null;
		return this.loggedInEvent.getTime();
	}




	@Override
	public long getLoginDelayTime() {
		Date loggedInTime = this.getLoggedInTime();
		if (loggedInTime == null) return 0;
		long delay= loggedInTime.getTime() - this.getConnectionTime().getTime();
		return delay>0 ? delay: 0;
	}


	@Override
	public String getDisConnectionTimeStr() {
		Date time = this.getDisconnectionTime();
		if (time.getTime() == 0L){
			return "unknown";
		}
		return time.toString();
	}




	@Override
	public String getConnectionTimeStr() {
		Date time = this.getConnectionTime();
		if (time.getTime() == 0L){
			return "unknown";
		}
		return time.toString();
	}



}
