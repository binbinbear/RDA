package com.vmware.horizontoolset.util;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.usage.EventType;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.admin.be.events.AdminEvent;
import com.vmware.vdi.admin.be.events.AdminEventFilter;
import com.vmware.vdi.admin.be.events.AdminEventManager;
public class EventDBUtil {
	private static Logger log = Logger.getLogger(EventDBUtil.class);
	 private AdminEventFilter eventFilter = new AdminEventFilter();
	private VDIContext vdiContext;
	public EventDBUtil(String username, String password, String domain) throws ADAMConnectionFailedException{
		vdiContext = VDIContextFactory.VDIContext(username, password, domain);
	}
	
	public void disConnect(){
		if (this.vdiContext!=null){
			try{
				this.vdiContext.disposeContext();
			}catch(Exception ex){
				log.warn("can't disconnect from context",ex);
			}
		}
		
	}
	
	
	public List<Event> getEvents(String username, int daysToShow){
		log.debug("start to query events:"+ daysToShow + " user:"+ username);
		eventFilter.setFilterDays(daysToShow);
		eventFilter.setFilterText(username);

		List<AdminEvent> adminevents = AdminEventManager.getInstance().getEventList(
				vdiContext, eventFilter);
		
		List<Event> allEvents = new ArrayList<Event>();
		for (AdminEvent adminevent: adminevents){
			Event event = new EventImpl(adminevent);
			if (event.getType() != EventType.Others ){
				allEvents.add(event);
			}
		}
		java.util.Collections.sort(allEvents);
		log.debug("Events:"+ allEvents.size());
		return allEvents;
	}
	

    

}
	 