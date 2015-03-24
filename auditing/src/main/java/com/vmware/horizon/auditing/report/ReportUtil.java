package com.vmware.horizon.auditing.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.db.ConnectionImpl;
import com.vmware.horizon.auditing.db.EventImpl;


public class ReportUtil {
	private static Logger log = Logger.getLogger(ReportUtil.class);
	
	
	
	/**
	 * Get all connection time and disconnection time for a specific user or all users
	 * @param events
	 * @param userName     null to get all users
	 * @return 
	 */
	public static List<Connection> getConnections(List<Event> events, String userName) {
		if (userName == null){
			userName = "";
		}
		userName = userName.toLowerCase();
		List<Connection> result = new ArrayList<Connection>();
		Set<String> usedConnectionEvents = new HashSet<String>();
		
		Map<String,Event> connectionEvents = new HashMap<String, Event>();
		
		Map<String,Event> loggedInEvents = new HashMap<String, Event>();
		
		if (events.size()==0){
			return result;
		}
		
		int eventsSize = events.size();
		Date earliestDate = events.get(eventsSize -1 ).getTime();
		
		 log.debug("before tranverse events: "+new Date());
		for (int i = eventsSize -1; i>=0; i--) {
			Event event = events.get(i);
			String eventUserName = event.getUserName();
			
			if (eventUserName!=null && ( userName.length() == 0 || eventUserName.contains(userName))){
				
				String key = event.getMachineDNSName() + eventUserName;
				if (event.getType() == EventType.Connection){
					connectionEvents.put(key, event);
				} else if (event.getType() == EventType.Loggedin){
					loggedInEvents.put(key, event);
				} else if (event.getType() == EventType.Disconnection){
					Event connectionEvent = connectionEvents.get(key);
					if (connectionEvent!=null){
						ConnectionImpl connection = new ConnectionImpl(connectionEvent, loggedInEvents.get(key), event);
						result.add(connection);
						
						connectionEvents.remove(key);
						loggedInEvents.remove(key);
						usedConnectionEvents.add(key);
					}else if (! usedConnectionEvents.contains(key)){
						usedConnectionEvents.add(key);
						connectionEvent = new EventImpl(event, earliestDate);
						//if a disconnect event happens without a connect event, this should be a long time event.
						ConnectionImpl connection = new ConnectionImpl(connectionEvent, loggedInEvents.get(key), event);
						result.add(connection );
					}
				}

			}
		}
		Date unknownDate = new Date(0);
		//for the connectionEvents without disconnection events, they should be connecting events
		for (Event event: connectionEvents.values()){
			ConnectionImpl connection = new ConnectionImpl(event, new EventImpl(event, unknownDate));
			result.add(connection );
		}
		 log.debug("after tranverse events: "+new Date());
		
		
		java.util.Collections.sort(result);
		log.debug("All connections:" + result.size());
		return result;
	}
	
	private static int MAXIMUM_ACCUMULATED_REPORT_SIZE = 32;
	/**
	 * Get accumulated using time for all users
	 * @param connections
	 * @return
	 */
	public static AccumulatedUsageReport generateUsageReport(List<Connection> connections){
		log.debug("Start to generate usage report from connections:"+ connections.size());
		HashMap<String, AccumulatedUsage> map = new HashMap<String, AccumulatedUsage>( );
		for (Connection connection:connections ){
			String username = connection.getUserName();
			long time= connection.getUsageTime();
			AccumulatedUsage usage = map.get(username);
			if (usage == null){
				usage = new  AccumulatedUsage(username, time);
				map.put(username, usage);
			}else{
				usage.addTime(time);
			}
		}
		
		//move from map to list
		ArrayList<AccumulatedUsage> list= new ArrayList<AccumulatedUsage>(map.values());
		
		Collections.sort(list);
		int end = list.size()-1; 
		for (int i=end;i>=MAXIMUM_ACCUMULATED_REPORT_SIZE;i--){
			list.remove(i);
		}
		AccumulatedUsageReport report = new AccumulatedUsageReport(list);
		log.info("Report size:" + list.size());
		return report;
	}
	
	/**
	 * Get concurrent connections for the past week/month
	 * 
	 * @param events all connection/disconnection event
	 * @param timeUnit   number of seconds
	 * @param viewAPIService 
	 * @return
	 */
	public static ConcurrentConnectionsReport getConcurrentConnectionsReport(List<Event> events, long timeUnit) {
		
		if (events == null || events.size() == 0){
			return null;
		}
		log.debug("Start to generate concurrent connections report from event:" + events.size() + "Timeunit:" + timeUnit);
		timeUnit = timeUnit* 1000;
		List<Connection> connections = ReportUtil.getConnections(events, null);
		
		events.clear();
		for (Connection c: connections){
			events.add(c.getConnectionEvent());
			if (c.getDisconnectionTime().getTime()>0){
				events.add(c.getDisconnectionEvent());
			}
			
		}
		
		if (events.size()==0){
			return null;
		}
		java.util.Collections.sort(events);
		
		

		int i = events.size()-1;
		long previousTime = events.get(i).getTime().getTime();
		int currentMax = 0;
		int currentConCurrent = 0;
		List<ConcurrentConnection> result = new ArrayList<ConcurrentConnection>();
		for ( ;i>=0;i--) {
			Event event = events.get(i);
			if (i==0 || event.getTime().getTime() - previousTime > timeUnit){
				result.add(new ConcurrentConnection(new Date(previousTime), currentMax));
				long diffs = (event.getTime().getTime() - previousTime)/timeUnit;
				if (diffs>1L){
					result.add(new ConcurrentConnection(new Date(previousTime + timeUnit), currentConCurrent));
					if (diffs>2L){
						result.add(new ConcurrentConnection(new Date(previousTime + timeUnit*(diffs-1)), currentConCurrent));
					}
				}
				previousTime = previousTime + timeUnit * diffs;
				currentMax = 0;
			
			}
			
			if (event.getType() == EventType.Connection){
				currentConCurrent ++;
			}else if (event.getType() == EventType.Disconnection && currentConCurrent>0){
				currentConCurrent --;
			}
			if (currentConCurrent > currentMax){
				currentMax = currentConCurrent;
			}
			
			
		}
		
		
		log.debug("Report size:" + result.size());
		return new ConcurrentConnectionsReport(result);
	}

}
