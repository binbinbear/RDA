package com.vmware.horizontoolset.usage;

import java.util.Date;

public interface Event extends Comparable<Event>{
	public EventType getType();
	public String getUserName();
	public Date getTime();
	public String getMachineDNSName();
	public String getMessage();
}
