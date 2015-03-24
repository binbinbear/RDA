package com.vmware.horizon.auditing.report;

import java.util.Date;

public interface Event extends Comparable<Event>{
	public EventType getType();
	public String getUserName();
	public Date getTime();
	public String getMachineDNSName();
	//for desktop pools, it's pool name; for rdsh/applications, it's farm name;
	public String getSourceName();
	public String getShortMessage();
	
	public void setFarmName(String farmName);
	public String getFarmName();
}
