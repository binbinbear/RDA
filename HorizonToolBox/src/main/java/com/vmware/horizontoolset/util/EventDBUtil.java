package com.vmware.horizontoolset.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private static long lastCachedTime = new Date().getTime();
	private static int cachedDays = 7;
	private static Set<Event> cachedEvents =new HashSet<Event>();
	private static final String filterText = "Agent";
	private static final int pagingSize = 10000;
	private static final long millisecondsHour = 1000L * 3600L;
	private static final long millisecondsDay = millisecondsHour * 24L;
	private static final long millisecondsMonth = millisecondsDay *30L;
	
	
	private void updateCache(int recentdays){
		int days = recentdays;
		Long currentTime = new Date().getTime();
		if (cachedEvents!=null && cachedEvents.size()>0){
			
			long hours = (currentTime - lastCachedTime)/(millisecondsHour);
			if (hours<=0 && recentdays<=cachedDays){
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
		log.debug("Previous events size:" + cachedEvents.size());
		lastCachedTime = currentTime;

		log.debug("Start to query event within days:" + days);
		AdminEventFilter eventFilter = new AdminEventFilter();
		eventFilter.setFilterDays(days);
		eventFilter.setFilterText(filterText);
		eventFilter.setPageSize(pagingSize);
		List<AdminEvent> adminEvent = AdminEventManager.getInstance().getEventList(
				vdiContext, eventFilter);
		log.debug("New Events:" + adminEvent.size());
		for (AdminEvent adminevent: adminEvent){
			Event event = new EventImpl(adminevent);
			if (event.getType() != EventType.Others ){
				cachedEvents.add(event);
			}
		}
		log.debug("Events after updating:"+ cachedEvents.size());
	}
	private List<Event> getEvents(int recentdays){
		this.updateCache(recentdays);
		
		long currentTime = new Date().getTime();
		
		List<Event> result = new ArrayList<Event>();
		//remove event older than 1 month
		for (Event event: cachedEvents ){
			if (currentTime -event.getTime().getTime()  > millisecondsMonth){
				cachedEvents.remove(event);
			}else if (currentTime -event.getTime().getTime()  < millisecondsDay * recentdays){
				result.add(event);
			}
		}
		log.debug("Current cache size:" + cachedEvents.size());
		log.debug("All events size:" + result.size());
		
		return result;
	}
	
	public List<Event> getEvents(String username, int daysToShow,String poolName){
		log.debug("start to query events:"+ daysToShow + " user:"+ username+" poolName:"+poolName);
		List<Event> allEvents = this.getEvents(daysToShow);
		log.debug("All Events size:" + allEvents.size());
		List<Event> results = new ArrayList<Event>();
		for (Event event: allEvents){
			if (username == null || username.length() == 0 ||event.getUserName().contains(username)){
				if (poolName == null || poolName.length() == 0 || poolName.equalsIgnoreCase(event.getPoolName())){
					results.add(event);
				}
			}
			
		}
		java.util.Collections.sort(results);
		log.debug("Result Events:"+ results.size());
		return results;
	}
}
	 