package com.vmware.horizontoolset.util;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.usage.EventType;
import com.vmware.vdi.admin.be.events.AdminEvent;
import com.vmware.vdi.admin.be.events.AdminEventSource;
import com.vmware.vdi.events.enums.EventModule;

/**
 * @author Administrator
 *
 */
public class EventImpl implements Event{
	private static Logger log = Logger.getLogger(EventImpl.class);
	
	@Override
	public int hashCode() {
		return this.eventId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj instanceof EventImpl) {
        	EventImpl anotherObj = (EventImpl) obj;
            return this.eventId == anotherObj.eventId && this.time.equals(anotherObj.time);
        }
        return false;
	}

	private static final String ACCEPT = "has accepted an allocated session";
	private static final String ON_MACHINE = "running on machine ";
	private static final String DISCONNECT = "has disconnected from machine ";
	private static final String LOG_OFF = "has logged off machine ";
	private static final String LOGOUT = " has logged out";
	private static final String REQUEST_APP= " requested Application ";
	
	private EventType type = EventType.Others;
	private String username;
	private String machineName = "";
	private Date time;
	private String poolName = "";
	private int eventId = 0;
	private String shortMessage;
	
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
		this.eventId = event.getEventId();
		this.username = event.getUsername();
		if (this.username==null){
			this.username="";
		}
		this.username = this.username.toLowerCase();
		this.shortMessage = event.getShortMessage();
		this.time = event.getTime();

		if(event.getModule().equals(EventModule.Agent) && event.isInfo() ){
			if (shortMessage.contains(ACCEPT)){
				this.type = EventType.Connection;
				this.machineName = getValue(shortMessage, ON_MACHINE);
			}else if (shortMessage.contains(DISCONNECT)  ){
				this.type = EventType.Disconnection;
				this.machineName = getValue(shortMessage, DISCONNECT);
			}else if (shortMessage.contains(LOG_OFF)){
				this.type = EventType.Disconnection;
				this.machineName = getValue(shortMessage, LOG_OFF);
			} else {
				this.type = EventType.Others;
			}
			
			//get pool name from the message
			List sourcesList = event.getSources();
			for( Object eventSource : sourcesList){
				AdminEventSource source = (AdminEventSource)eventSource;
				if(source.getType().toString().equals("POOL")){
					this.poolName = source.getName();
					//break;
				}
				log.debug("Event source:" + source.getType().toString() + " source name:" + source.getName());
				
			}
		} else if (event.getModule().equals(EventModule.Broker) 
				&& (event.isInfo() || event.isAuditSuccess())) {
			if (shortMessage.contains(LOGOUT)) {
				this.type = EventType.Logout;
			} else if (shortMessage.contains(REQUEST_APP)) {
				this.type = EventType.RequestApp;
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

	

	/**
	 * sort by descent time, then by username
	 */
	@Override
	public int compareTo(Event o) {
		if (this.time.after(o.getTime())){
			return -1;
		}else if (this.time.before(o.getTime())){
			return 1;
		}
		return this.username.compareTo(o.getUserName());
	}

	@Override
	public String getPoolName() {
		return this.poolName;
	}

	private String farmName;
	@Override
	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}

	@Override
	public String getFarmName() {
		return this.farmName;
	}

	@Override
	public String getShortMessage() {
		return shortMessage;
	}

}
