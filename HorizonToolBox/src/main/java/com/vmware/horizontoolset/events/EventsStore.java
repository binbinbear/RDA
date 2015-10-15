package com.vmware.horizontoolset.events;

import java.util.Date;
import java.util.List;

import com.vmware.horizon.auditing.report.Event;

public interface EventsStore {
	public boolean forceUpdate();
	public List<Event> getAllConnectionEvents(Date start, Date end);
}
