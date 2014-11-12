package com.vmware.horizontoolset.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.viewapi.RDS;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

public class EventDBUtil implements AutoCloseable {
	private static Logger log = Logger.getLogger(EventDBUtil.class);
	private final VDIContext vdiContext;
	private final boolean isDefault;
	private ViewAPIService viewapi;
	
	public EventDBUtil(String username, String password, String domain, ViewAPIService viewapi) throws ADAMConnectionFailedException{
		vdiContext = VDIContextFactory.VDIContext(username, password, domain);
		this.viewapi = viewapi;
		isDefault = false;
	}

	private EventDBUtil() throws ADAMConnectionFailedException {
		vdiContext = VDIContextFactory.defaultVDIContext();
		viewapi = null;
		isDefault = true;
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
		this.viewapi = null;
		
		if (this.vdiContext!=null){
			
			try{
				if (isDefault)
					VDIContext.release(vdiContext);
				else
					vdiContext.disposeContext();
			} catch(Exception ex) { 
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
			try{
				if (username == null || username.length() == 0 ||(event.getUserName()!=null && event.getUserName().contains(username))){
					String farmname="";
					if (event.getPoolName()==null || event.getPoolName().isEmpty()){
						//try to set farm name
						farmname=getFarmName(event.getMachineDNSName());
						event.setFarmName(farmname);
					}
					if (poolName == null || poolName.length() == 0 || poolName.equalsIgnoreCase(event.getPoolName())){
						results.add(event);
					}else if (farmname!=null && farmname.equalsIgnoreCase(poolName)){
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
		return allEvents;
	}
	
	private Map<String, String> rds2Farm;
	private String getFarmName(String RDSName) {
		if (RDSName == null || RDSName.isEmpty()){
			return null;
		}
		if (viewapi == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
		if (rds2Farm == null){
			rds2Farm = new HashMap<String, String>();
			List<RDS> rdshosts =this.viewapi.getAllRDS();
			for (RDS rds: rdshosts){
				String thisname = rds.getName().split("[.]")[0];
				rds2Farm.put(thisname.toLowerCase(), rds.getFarmName());
				log.debug("Put into map rds:"+ thisname.toLowerCase()+" farm:"+ rds.getFarmName());
			}
		}
		
		String farm = rds2Farm.get(RDSName.split("[.]")[0].toLowerCase());
		return farm;
	}
}
	 