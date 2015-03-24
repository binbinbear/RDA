package com.vmware.horizon.auditing.db;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizon.auditing.report.EventType;
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

	private static final String ACCEPT = "has accepted an allocated session";
	private static final String LOGGEDIN = "has logged in to a new session";
	private static final String ON_MACHINE = "running on machine ";
	private static final String DISCONNECT = "has disconnected from machine ";
	private static final String LOG_OFF = "has logged off machine ";
	private static final String LOGOUT = " has logged out";
	private static final String REQUEST_APP= " requested Application ";
	private static final String HAS_EXPIRED = "has expired";
	private static final String HAS_EXPIRED_PREFIX = "session on machine ";

	private EventType type = EventType.Others;
	private String username;
	private String machineName = "";
	private Date time;
	private String sourceName = "";
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

	//create a dummy connect or disconnect event. 
	public EventImpl(Event pairevent, Date time){
		this.username = pairevent.getUserName();
		this.farmName = pairevent.getFarmName();
		this.machineName = pairevent.getMachineDNSName();
		this.time = time;
		if (pairevent.getType()==EventType.Connection){
			this.type = EventType.Disconnection;
		}else if (pairevent.getType()==EventType.Disconnection){
			this.type = EventType.Connection;
		}
		this.sourceName = pairevent.getSourceName();

	}
	private String getSourceByKey(AdminEvent event,String key){
		//get pool name from the message
		List sourcesList = event.getSources();
		for( Object eventSource : sourcesList){
			AdminEventSource source = (AdminEventSource)eventSource;
			if(source.getType().toString().equalsIgnoreCase(key)){
				return source.getName();
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

		if(EventModule.Agent.equals(event.getModule()) && event.isInfo() ){
			if (shortMessage.contains(ACCEPT)){
				this.type = EventType.Connection;
				this.machineName = getValue(shortMessage, ON_MACHINE);
			}else if (shortMessage.contains(DISCONNECT)  ){
				this.type = EventType.Disconnection;
				this.machineName = getValue(shortMessage, DISCONNECT);
			}else if (shortMessage.contains(LOG_OFF)){
				this.type = EventType.Disconnection;
				this.machineName = getValue(shortMessage, LOG_OFF);
			} else if (shortMessage.contains(HAS_EXPIRED)){
				this.type = EventType.Disconnection;
				this.machineName = getValue(shortMessage, HAS_EXPIRED_PREFIX);
			} else {
				this.type = EventType.Others;
			}

			if( null == this.machineName ){
				this.machineName = "";
			}
			this.machineName = this.machineName.toLowerCase();
			String[] sources= {"POOL","FARM", "APPLICATION", "DESKTOP", "RDSSERVER"};
			for (int i=0;i<sources.length;i++){
				this.sourceName = this.getSourceByKey(event, sources[i]);
				if (this.sourceName!=null){
					break;
				}
			}

		} else if (EventModule.Broker.equals(event.getModule())
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
		if(this.username.contains("@")){
			String res[] = this.username.split("@");
			this.username = res[0];
		}
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

		if( this.time.getTime() > o.getTime().getTime() ){
			return -1;
		}else if( this.time.getTime() == o.getTime().getTime() ){
			if(this.type==EventType.Disconnection){
				return -1;
			}
		}
		return 1;

	}

	@Override
	public String getSourceName() {
		return this.sourceName;
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
