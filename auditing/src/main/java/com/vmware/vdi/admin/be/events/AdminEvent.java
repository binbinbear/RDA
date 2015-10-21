package com.vmware.vdi.admin.be.events;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vmware.vdi.events.enums.EventModule;
import com.vmware.vdi.events.enums.EventSeverity;

/**
 * It defines the event object in admin console.
 *
 * @author dliu
 *
 */
public class AdminEvent {
    private int eventId;

    private String shortMessage;


    private Date time;
    
    private String clientIP;

    private EventModule module;

    private String moduleString;

    private String userSID;

    private String username;
    
    private String machineName;
    
    private String poolId;
    
    private String desktopId;
    
    /**
     * Get the short version of the message for the event
     *
     * @return The message
     */
    public String getMessage() {
        return shortMessage;
    }


    /**
     * Set the event message
     *
     * @param shortMessage
     *            The event message
     */
    public void setMessage(String msg) {
        this.shortMessage = msg;
    }

    /**
     * Get the time at which the event occurred
     *
     * @return The event time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Set the time at which the event occurred
     *
     * @param time
     *            The event time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * It returns the localized string representation for the time.
     *
     * @return The localized time.
     */
    public String getTimeString() {
        return com.vmware.vdi.admin.ui.common.Util.getLocalizedDateString(
                getTime(), DateFormat.SHORT, DateFormat.MEDIUM);
    }

    public void setTimeString(String time) {
        // do nothing
    }

    /**
     * Get the module in which the event occurred
     *
     * @return
     */
    public EventModule getModule() {
        return module;
    }

    /**
     * Set the module in which the event occurred
     *
     * @param module
     */
    public void setModule(EventModule module) {
        this.module = module;
    }

    /**
     * It returns the localized string for event module.
     *
     * @return the localized module
     */
    public String getModuleString() {
        if (this.moduleString != null) {
            return this.moduleString;
        }
        return com.vmware.vdi.admin.ui.common.Util
                .getLocalizedEventModule(this);
    }

    /**
     * @param moduleString
     *            the moduleString to set
     */
    public void setModuleString(String moduleString) {
        this.moduleString = moduleString;
    }


    /**
     * It returns the localized string for event severity.
     *
     * @return The localized event severity.
     */
    public String getSeverityString() {
        return com.vmware.vdi.admin.ui.common.Util
                .getLocalizedEventSeverity(this);
    }

    public void setSeverityString(String severity) {
        // do nothing
    }



    /**
     * @return the userSID
     */
    public String getUserSID() {
        return userSID;
    }

    /**
     * @param userSID
     *            the userSID to set
     */
    public void setUserSID(String userSID) {
        this.userSID = userSID;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

   
    /**
     * @return the eventId
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * @param eventId
     *            the eventId to set
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

   

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public String getDesktopId() {
		return desktopId;
	}

	public void setDesktopId(String desktopId) {
		this.desktopId = desktopId;
	}
}