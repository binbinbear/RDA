package com.vmware.horizontoolset.util;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

public class EventDBUtil implements AutoCloseable {
	private static Logger log = Logger.getLogger(EventDBUtil.class);
	private VDIContext vdiContext;
	
	
	public EventDBUtil(String username, String password, String domain) throws ADAMConnectionFailedException{
		vdiContext = VDIContextFactory.VDIContext(username, password, domain);
	}

	@Override
	public void close() {
		if (this.vdiContext!=null){
			try{
				this.vdiContext.disposeContext();
			}catch(Exception ex){
				log.warn("can't disconnect from context",ex);
			}
		}
	}
	
	public List<Event> getEvents(String username, int daysToShow,String poolName){
		log.debug("start to query events:"+ daysToShow + " user:"+ username+" poolName:"+poolName);
		List<Event> allEvents = EventDBCache.getEvents(this.vdiContext,daysToShow);
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
	 