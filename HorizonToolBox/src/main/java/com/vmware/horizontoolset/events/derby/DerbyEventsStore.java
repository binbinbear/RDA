package com.vmware.horizontoolset.events.derby;

import java.util.Date;
import java.util.List;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizontoolset.events.EventsStore;

public class DerbyEventsStore implements EventsStore {

	public DerbyEventsStore(){
		
	}

	@Override
	public boolean forceUpdate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Event> getAllConnectionEvents(Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
