package com.vmware.horizon.auditing.db;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

public class EventDBUtil implements AutoCloseable {
	private static Logger log = Logger.getLogger(EventDBUtil.class);
	private VDIContext vdiContext;
	
	public EventDBUtil(VDIContext vdiContext) throws ADAMConnectionFailedException{
		this.vdiContext = vdiContext;
	}

	private EventDBUtil() throws ADAMConnectionFailedException {
		vdiContext = VDIContextFactory.defaultVDIContext();
	}
	
	public static EventDBUtil createDefault() {
		try {
			return new EventDBUtil();
		} catch (Exception e) {
			log.error("Fail creating EventDBUtil.", e);
		}
		return null;
	}
	
	@Override
	public void close() {
		
		if (vdiContext != null) {
			vdiContext.close();
			vdiContext = null;
		}
	}
	
	public List<Event> getEvents(String username, int daysToShow,String sourceName){
		if (username == null){
			username="";
		}
		username = username.toLowerCase();
		log.debug("start to query events:"+ daysToShow + " user:"+ username+" sourceName:"+sourceName);
		List<Event> allEvents = EventDBCache.getEvents(this.vdiContext,daysToShow);
		log.debug("All Events size:" + allEvents.size());
		List<Event> results = new ArrayList<Event>();
		for (Event event: allEvents){
			try{
				if ( username.length() == 0 ||event.getUserName().contains(username)){
				
					if (sourceName == null || sourceName.length() == 0 || sourceName.equalsIgnoreCase(event.getSourceName())){
						results.add(event);
					}
				}
			}catch(Exception e){
				log.warn("Ignore error event,", e);
			}

		}
		java.util.Collections.sort(results);
		log.debug("Result Events:"+ results.size());
		return results;
	}
	
	public List<Event> getEvents(int daysToShow) {
		return getEvents(daysToShow, false);
	}
	
	public List<Event> getEvents(int daysToShow, boolean forceReload) {
		if (forceReload)
			EventDBCache.expire();
		List<Event> allEvents = EventDBCache.getEvents(vdiContext, daysToShow);
		log.debug("All Events size:" + allEvents.size());
		
		//sort by time, ascending (early event first)
		Collections.sort(allEvents, new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2) {
				return (int)(o1.getTime().getTime() - o2.getTime().getTime());
			}
		});
		
		return allEvents;
	}
	

}
	 