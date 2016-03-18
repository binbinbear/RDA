package com.vmware.horizon.auditing.db;

import java.util.Date;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizon.auditing.report.EventType;
import com.vmware.vdi.admin.be.events.AdminEvent;
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

	private static final String LOGGEDIN = "has logged in to a new session";
	private static final String USER = "user ";

	private static final String DISCONNECT = "has disconnected from machine ";
	private static final String LOG_OFF = "has logged off machine ";
	private static final String LOGOUT = " has logged out";
	private static final String REQUEST_APP= " requested Application ";
	private static final String HAS_EXPIRED = "has expired";
	private static final String HAS_EXPIRED_PREFIX = "session on machine ";

	/**
	 * *List the event type description in database table
	 */
	private static final String TYPE_BROKER_USERLOGGEDIN = "BROKER_USERLOGGEDIN";
	private static final String TYPE_BROKER_USERLOGGEDOUT = "BROKER_USERLOGGEDOUT";


	private EventType type = EventType.Others;
	private String username;
	public void setUsername(String username) {

		if (username ==null){
			username = "";
		}
		username = username.toLowerCase();
		if(username.contains("@")){
			String res[] = username.split("@");
			username = res[0];
		}
		this.username = username;

	}

	private String machineName = "";
	public void setMachineName(String machineName) {
		if(machineName ==null){
			machineName = "";
		}

		this.machineName = machineName.toLowerCase();
	}

	private Date time;
	private String sourceName = "";
	private int eventId = 0;
	private String shortMessage;

	private String clientIp;
	private String brokerSessionId = "";

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
		setUsername(pairevent.getUserName());

		setMachineName(pairevent.getMachineDNSName());
		this.time = time;
		if (pairevent.getType()==EventType.Connection){
			this.type = EventType.Disconnection;
		}else if (pairevent.getType()==EventType.Disconnection){
			this.type = EventType.Connection;
		}
		this.sourceName = pairevent.getSourceName();
		this.clientIp = pairevent.getClientIP();
		this.brokerSessionId = pairevent.getBrokerSessionId();

	}

	public EventImpl(AdminEvent event){
		this.eventId = event.getEventId();
		setUsername( event.getUsername());


		this.shortMessage = event.getMessage().toLowerCase();
		this.time = event.getTime();
		this.clientIp = event.getClientIP();
		this.brokerSessionId = event.getBrokerSessionId();
		String eventTypeInDatabase = event.getEventType();
		//if(EventModule.Agent.equals(event.getModule()) && event.isInfo() ){
		if(EventModule.Agent.equals(event.getModule())){
			if (shortMessage.contains(ACCEPT)){
				this.type = EventType.Connection;

			}else if (shortMessage.contains(DISCONNECT)  ){
				this.type = EventType.Disconnection;

			}else if (shortMessage.contains(LOG_OFF)){
				this.type = EventType.Disconnection;

			} else if (shortMessage.contains(HAS_EXPIRED)){
				this.type = EventType.Disconnection;

			} else {
				this.type = EventType.Others;
			}

			setUsername( event.getUsername());
			if (this.username == null || this.username.length() == 0){
				setUsername( getValue(shortMessage, USER));
			}
			setMachineName(event.getMachineName());


			this.sourceName = event.getDesktopId();
			if (this.sourceName == null || this.sourceName.length() == 0){
				this.sourceName = event.getPoolId();
			}

		} else if (EventModule.Broker.equals(event.getModule())) {
			if(eventTypeInDatabase.equals(TYPE_BROKER_USERLOGGEDIN)) {
				this.type = EventType.BrokerLoggedin;
			} else if(eventTypeInDatabase.equals(TYPE_BROKER_USERLOGGEDOUT)) {
				this.type = EventType.BrokerLoggedout;
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

		if( this.time.getTime() > o.getTime().getTime() ){
			return -1;
		}else if( this.time.getTime() == o.getTime().getTime() ){
			return o.getType().compareTo(this.getType());
		}
		return 1;

	}

	@Override
	public String getSourceName() {
		return this.sourceName;
	}


	@Override
	public String getShortMessage() {
		return this.shortMessage;
	}

	@Override
	public String getClientIP() {

		return this.clientIp;
	}

	@Override
	public String getBrokerSessionId() {
		return this.brokerSessionId;
	}

}
