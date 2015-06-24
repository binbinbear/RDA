package com.vmware.horizon.auditing.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizon.auditing.report.EventType;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.admin.be.events.AdminEvent;
import com.vmware.vdi.admin.be.events.AdminEventFilter;
import com.vmware.vdi.admin.be.events.AdminEventManager;

public class EventDBCache {
	private static Logger log = Logger.getLogger(EventDBCache.class);
	
	
	private static long lastCachedTime = -1;
	private static int cachedDays = 7;
	private static Set<Event> cachedEvents =new HashSet<Event>();
	private static final String filterText = "Agent";
	private static final int pagingSize = 20000;
	private static final long millisecondsHour = 1000L * 3600L;
	private static final long millisecondsDay = millisecondsHour * 24L;
	private static final long millisecondsMonth = millisecondsDay *30L;
	
	public synchronized static void expire() {
		lastCachedTime = -1;
	}
	
	private static void updateCache(VDIContext vdiContext, int recentdays){
		int days = recentdays;
		long currentTime = new Date().getTime();
		if ( cachedEvents.size()>0){
			
			long hours = (currentTime - lastCachedTime)/(millisecondsHour);
			if (lastCachedTime != -1 && hours<=0 && recentdays<=cachedDays){
				log.debug("Cache hit, No need to query again in an hour");
				return;
			}
			days = (int)hours/24 + 1;
			if (days>recentdays || days + cachedDays < recentdays){
				log.debug("Clean cached events since days is "+ days + " while recent days is "+ recentdays+",cached days is " + cachedDays);
				cachedEvents.clear();
				days = recentdays;
				cachedDays = recentdays;
			}else{
				cachedDays = cachedDays + days-1;
				if (cachedDays>30){
					cachedDays = 30;
				}
			}
		}else{
			cachedDays = recentdays;
		}
		
		//remove event older than 1 month
		Iterator<Event> iterator = cachedEvents.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (currentTime -event.getTime().getTime()  > millisecondsMonth) {
				iterator.remove();
			}
		}
	
		
		log.debug("Previous events size:" + cachedEvents.size());
		lastCachedTime = currentTime;

		log.debug("Start to query event within days:" + days);
		AdminEventFilter eventFilter = new AdminEventFilter();
		eventFilter.setFilterDays(days);
		eventFilter.setFilterText(filterText);
		eventFilter.setPageSize(pagingSize);
		List<AdminEvent> adminEvent = new ArrayList<AdminEvent>();
		try {
			adminEvent = AdminEventManager.getInstance().getEventList(
					vdiContext, eventFilter);
		} catch (Exception e) {
			log.error("error getting events", e);
		}
		log.debug("New Events:" + adminEvent.size());

		for (AdminEvent adminevent: adminEvent){
			
			EventImpl event = new EventImpl(adminevent);
			//log.debug("  Type=" + event.getType() + ", msg=" + event.getShortMessage());
			
			if (event.getType() != EventType.Others){
				cachedEvents.add(event);
				//log.debug("  (added)");
			}
			
		}
		log.debug("Events after updating:"+ cachedEvents.size());
	}
	static synchronized List<Event> getEvents(VDIContext vdiContext, int recentdays){
		EventDBCache.updateCache(vdiContext,recentdays);
		
		long currentTime = new Date().getTime();
		
		List<Event> result = new ArrayList<Event>();
		
		
		for (Event event: cachedEvents ){
			if (currentTime -event.getTime().getTime()  < millisecondsDay * recentdays){
				result.add(event);
			}
		}
		log.debug("Current cache size:" + cachedEvents.size());
		log.debug("All events size:" + result.size());
		
		return result;
	}
	
}