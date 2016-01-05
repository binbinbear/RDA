package com.vmware.horizon.auditing.db;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.horizon.auditing.report.BrokerSession;
import com.vmware.horizon.auditing.report.Event;

import org.apache.log4j.Logger;

@JsonIgnoreProperties(value={"brokerSessionTimeRange", "loggedInTime", "loggedOutTime"})
public class BrokerSessionImpl implements BrokerSession {
	private final static String INVALID_USER = "Invalid User";
	private final static String NO_CLIENT_IP = "Cannot get client IP";
	private final static String NO_VALID_SESSIONID = "No valid session ID";
	private final static int UNKNOWN_SESSION_TIMERANGE = 0;
	private final static String UNKNOWN_TIME_SPAN = "Unknown Time";
	private static Logger log = Logger.getLogger(BrokerSessionImpl.class);
	private Event loggedInEvent = null;
	private Event loggedOutEvent = null;
	private String userName = null;
	private String clientIP = null;

	private String sessionID;


	public BrokerSessionImpl(Event loggedInEvent, Event loggedOutEvent) {
		if((loggedInEvent == null) || (loggedOutEvent == null)) {
			// logs for exception
			log.error("loggedin event or logged out events are NULL for borker session");
		}
		this.userName = INVALID_USER;
		this.clientIP = NO_CLIENT_IP;
		this.loggedInEvent = loggedInEvent;
		this.loggedOutEvent = loggedOutEvent;
		if(loggedInEvent != null) {
			this.sessionID = this.loggedInEvent.getBrokerSessionId();
			this.userName = loggedInEvent.getUserName();
			this.clientIP = loggedInEvent.getClientIP();	

		} else if(loggedOutEvent != null) {
			this.userName = loggedOutEvent.getUserName();
			this.sessionID = loggedOutEvent.getBrokerSessionId();
		}
		
		
		
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionID == null) ? 0 : sessionID.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BrokerSessionImpl other = (BrokerSessionImpl) obj;
		if (sessionID == null) {
			if (other.sessionID != null)
				return false;
		} else if (!sessionID.equals(other.sessionID))
			return false;
		return true;
	}



	@Override
	public int compareTo(BrokerSession o) {
		if (this == o){
			return 0;
		}
		
		if (o==null){
			return 1;
		}
		
		if(this.getLoggedInTime().before(o.getLoggedInTime())){
			return 1;
		}else if (this.getLoggedInTime().after(o.getLoggedInTime())){
			return -1;
		}else if (this.getLoggedOutTime().before(o.getLoggedOutTime())){
			return 1;
		}else if (this.getLoggedOutTime().after(o.getLoggedOutTime())){
			return -1;
		}
		return 0;
		
	}

	@Override
	public String getUserName() {
		return this.userName;
	}

	@Override
	public long getBrokerSessionTimeRange() {
		if((this.loggedInEvent != null) && (this.loggedOutEvent != null)) {
			long timeRange = this.loggedOutEvent.getTime().getTime() - this.loggedInEvent.getTime().getTime();
			return timeRange>0 ? timeRange : UNKNOWN_SESSION_TIMERANGE;
		} else {
			return UNKNOWN_SESSION_TIMERANGE;
		}
	}

	@Override
	public Date getLoggedInTime() {
		if (this.loggedInEvent != null) {
			return this.loggedInEvent.getTime();
		} else {
			return new Date(0);
		}
	}

	@Override
	public Date getLoggedOutTime() {
		if(this.loggedOutEvent != null) {
			return this.loggedOutEvent.getTime();
		} else {
			return new Date(0);
		}
	}

	@Override
	public String getClientIP(){
		return this.clientIP;
	}
	
	@Override
	public String getBrokerSessionId() {
		if(this.loggedInEvent != null) {
			return this.loggedInEvent.getBrokerSessionId();
		} else if (this.loggedOutEvent != null) {
			return this.loggedOutEvent.getBrokerSessionId();
		} else {
			return NO_VALID_SESSIONID;
		}
	}
	
	@Override
	public String getLoggedInTimeStr() {
		Date timeLoggedIn = getLoggedInTime();
		if(timeLoggedIn == null || timeLoggedIn.getTime() <= 0) {
			return UNKNOWN_TIME_SPAN;
		} else {
			return timeLoggedIn.toString();
		}
	}
	
	@Override
	public String getLoggedOutTimeStr() {
		Date timeLoggedOut = getLoggedOutTime();
		if(timeLoggedOut == null || timeLoggedOut.getTime() <= 0) {
			return UNKNOWN_TIME_SPAN;
		} else {
			return timeLoggedOut.toString();
		}
	}
}
