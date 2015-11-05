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
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;
import com.vmware.vdi.admin.be.events.AdminEvent;
import com.vmware.vdi.admin.be.events.AdminEventFilter;
import com.vmware.vdi.admin.be.events.AdminEventManager;

public class EventDBCache {
	private static Logger log = Logger.getLogger(EventDBCache.class);
	
	
	private static long lastCachedTime = -1;
	
	//maximum days is 90 (3 months)
	private static int cachedDays = 90;
	
	private static Set<Event> cachedEvents =new HashSet<Event>();
	private static final String filterText = "Agent";
	private static int pagingSize = 50000;
	private static final long millisecondsHour = 1000L * 3600L;
	private static final long millisecondsDay = millisecondsHour * 24L;
	private static final long millisecondsAll = millisecondsDay *getCachedDays();
	
	
	public synchronized static void expire() {
		lastCachedTime = -1;
	}

	public static long getLastCachedTime(){
		return lastCachedTime;
	}
	
	public static void updateCache(){
		try {
			updateCache(VDIContextFactory.defaultVDIContext());
		} catch (ADAMConnectionFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Can't update cache with default context", e);
		}
	}
	
	private synchronized static void updateCache(VDIContext vdiContext){
		//by default, query events within cachedDays
		int days = getCachedDays();
		long currentTime = new Date().getTime();
		if ( cachedEvents.size()>0){
			
			long hours = (currentTime - lastCachedTime)/(millisecondsHour);
			if (lastCachedTime != -1 && hours<=1 ){
				log.info("Cache hit, No need to query again in two hours");
				return;
			}
			days = (int)hours/24 + 1;
			if (days>getCachedDays() ){
				log.info("Clean cached events since days is "+ days + ",cached days is " + getCachedDays());
				cachedEvents.clear();
				days = getCachedDays();
			}
		}
		
		//remove event older than cachedDays
		Iterator<Event> iterator = cachedEvents.iterator();
		while (iterator.hasNext()) {
			Event event = iterator.next();
			if (currentTime -event.getTime().getTime()  > millisecondsAll) {
				iterator.remove();
			}
		}
	
		
		log.info("Previous events size:" + cachedEvents.size());
		lastCachedTime = currentTime;

		log.info("Start to query event within days:" + days + " Paging size:"+getPagingSize() );
		AdminEventFilter eventFilter = new AdminEventFilter();
		eventFilter.setFilterDays(days);
		eventFilter.setFilterText(filterText);
		eventFilter.setPageSize(getPagingSize());
		List<AdminEvent> adminEvent = new ArrayList<AdminEvent>();
		try {
			adminEvent = AdminEventManager.getInstance().getEventList(
					vdiContext, eventFilter);
		} catch (Exception e) {
			log.error("error getting events", e);
		}
		log.info("New Events:" + adminEvent.size());

		for (AdminEvent adminevent: adminEvent){
			
			EventImpl event = new EventImpl(adminevent);
			//log.debug("  Type=" + event.getType() + ", msg=" + event.getShortMessage());
			
			if (event.getType() != EventType.Others){
				cachedEvents.add(event);
				//log.debug("  (added)");
			}
			
		}
		log.info("Events after updating:"+ cachedEvents.size());
	}
	
	static List<Event> getEvents(VDIContext vdiContext, int recentdays){
		if (lastCachedTime == -1){
			updateCache(vdiContext);
		}
		
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


	public static int getCachedDays() {
		return cachedDays;
	}


	public static void setCachedDays(int cachedDays) {
		EventDBCache.cachedDays = cachedDays;
	}


	public static int getPagingSize() {
		return pagingSize;
	}


	public static void setPagingSize(int pagingSize) {
		EventDBCache.pagingSize = pagingSize;
	}
	
}
