package com.vmware.horizontoolset.util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Connection;
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
	


	private static  Comparator<AdminEvent> compartor = new Comparator<AdminEvent>() {
	    public int compare(AdminEvent a, AdminEvent b) {
	    	return a.getTime().compareTo(b.getTime());
	      }
	};
	
	
	public List<Connection> getConnections(String username, String daysToShow) {
		Map<String,Event> connectionEvents = new HashMap<String, Event>();
		if (daysToShow == null) {
			daysToShow = "1";
		}
		eventFilter.setFilterDays(Integer.parseInt(daysToShow));
		eventFilter.setFilterText(username);

		List<AdminEvent> events = AdminEventManager.getInstance().getEventList(
				vdiContext, eventFilter);
		
		java.util.Collections.sort(events, EventDBUtil.compartor);
		
		List<Connection> result = new ArrayList<Connection>();
		for (AdminEvent adminevent : events) {
			String eventname = adminevent.getUsername();
			
			if (eventname!=null && eventname.toLowerCase().contains(username)){
				Event event = new EventImpl(adminevent);
				if (event.getType() == EventType.Connection){
					connectionEvents.put(event.getMachineDNSName(), event);
				}else if (event.getType() == EventType.Disconnection){
					Event connectionEvent = connectionEvents.get(event.getMachineDNSName());
					if (connectionEvent!=null){
						result.add(new ConnectionImpl(connectionEvent, event));				
						connectionEvents.remove(event.getMachineDNSName());
					}
				}

			}
			
		}
		
		
		return result;
	}
    

}
	 