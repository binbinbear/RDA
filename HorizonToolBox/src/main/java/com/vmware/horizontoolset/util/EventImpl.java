package com.vmware.horizontoolset.util;

import java.util.Date;
import java.util.List;

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

	private static final String accept = "has accepted an allocated session";
	private static final String onMachine = "running on machine ";
	private static final String disconnect = "has disconnected from machine ";
	private static final String logoff = "has logged off machine ";
	
	private EventType type = EventType.Others;
	private String username;
	private String machineName = "";
	private Date time;
	private String poolName = "";
	private int eventId = 0;
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
		String message = event.getShortMessage();
		this.time = event.getTime();
		 
		if(event.getModule().equals(EventModule.Agent) && event.isInfo() ){
			if (event.getShortMessage().contains(accept)){
				this.type = EventType.Connection;
				this.machineName = getValue(message, onMachine);
			}else if (event.getShortMessage().contains(disconnect)  ){
				this.type = EventType.Disconnection;
				this.machineName = getValue(message, disconnect);
				
			}else if ( event.getShortMessage().contains(logoff)){
				this.type = EventType.Disconnection;
				this.machineName = getValue(message, logoff);
			}
			//get pool name from the message
			List sourcesList = event.getSources();
			for( Object eventSource : sourcesList){
				AdminEventSource source = (AdminEventSource)eventSource;
				if(source.getType().toString().equals("POOL")){
					this.poolName = source.getName();
					break;
				}				
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

}
