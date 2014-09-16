package com.vmware.horizontoolset.util;

import java.util.Date;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.usage.EventType;
import com.vmware.vdi.admin.be.events.AdminEvent;
import com.vmware.vdi.events.enums.EventModule;

public class EventImpl implements Event{
	private static final String accept = "has accepted an allocated session";
	private static final String onMachine = "running on machine ";
	private static final String disconnect = "has disconnected from machine ";
	private static final String logoff = "has logged off machine ";
	
	private EventType type = EventType.Others;
	private String username;
	private String machineName = "";
	private Date time;
	private String message;
	private String getValue(String all, String key){
		//get pool name from the message
		int index = all.indexOf(key) + key.length();
		
		while(index<all.length() && all.indexOf(index)==' '){
			index++;
		}
		if (index<all.length()){
			int endindex = all.indexOf(' ', index);
			if (endindex> index){
				return all.substring(index, endindex);
			}else{
				return all.substring(index);
			}
			
		}
		return null;
		
	}
	
	public EventImpl(AdminEvent event){
		
		this.username = event.getUsername();
		this.message = event.getShortMessage();
		this.time = event.getTime();
		 
		if(event.getModule().equals(EventModule.Agent) && event.isInfo() ){
			if (event.getShortMessage().contains(accept)){
				this.type = EventType.Connection;
				//get pool name from the message
				this.machineName = getValue(message, onMachine);
			}else if (event.getShortMessage().contains(disconnect)  ){
				this.type = EventType.Disconnection;
				this.machineName = getValue(message, disconnect);
				
			}else if ( event.getShortMessage().contains(logoff)){
				this.type = EventType.Disconnection;
				this.machineName = getValue(message, logoff);
			}
		}
	
	}
	
	@Override
	public EventType getType() {
		return this.type;
	}

	@Override
	public String getUserName() {
		return this.username;
	}

	@Override
	public Date getTime() {
		return this.time;
	}

	@Override
	public String getMachineDNSName() {
		return this.machineName;
	}

	
	@Override
	public String getMessage() {
		return this.message;
	}

}
