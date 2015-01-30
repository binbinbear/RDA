package com.vmware.horizontoolset.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.AccumulatedUsage;
import com.vmware.horizontoolset.usage.ConcurrentConnection;
import com.vmware.horizontoolset.usage.Connection;
import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.usage.EventType;
import com.vmware.horizontoolset.util.ConnectionImpl;
import com.vmware.horizontoolset.util.EventImpl;
import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewType;
import com.vmware.horizontoolset.viewapi.impl.SessionFarmImpl;
import com.vmware.horizontoolset.viewapi.impl.SessionPoolImpl;


public class ReportUtil {
	private static Logger log = Logger.getLogger(ReportUtil.class);
	
	
	
	public static SnapShotReport generateSnapShotReport(List<SnapShotViewPool> list){
		SnapShotReport report = new SnapShotReport();
		for (SnapShotViewPool pool:list){
			log.debug("new pool into report:"+pool.getName());
			pool.upateReport(report);
		}
		return report;
	}
	
	public static SessionReport generateSessionReport(List<SessionPool> pools, List<SessionFarm> farms){
		return new SessionReport(pools, farms);
	}
	
	private static void addorput(HashMap<String, Integer>map, String key){
		if (map.containsKey(key)){
			int value= map.get(key);
			map.put(key, value+1);
		}else{
			map.put(key, 1);
		}
	}
	public static SessionReport generateSessionReport(List<Session> sessions){
		HashMap<String, Integer> poolmap = new HashMap<String, Integer>();
		HashMap<String, ViewType> pooltype = new HashMap<String, ViewType>();
		HashMap<String, Integer> farmmap = new HashMap<String, Integer>();
		for (Session session:sessions){
			if (session.getType()== ViewType.APP){
				addorput(farmmap, session.getPoolOrFarmName());
			}else{
				addorput(poolmap, session.getPoolOrFarmName());
				pooltype.put(session.getPoolOrFarmName(), session.getType());
			}
		}
		
		ArrayList<SessionFarm> farms = new ArrayList<SessionFarm>();
		for(String name: farmmap.keySet()){
			farms.add(new SessionFarmImpl(name, farmmap.get(name)));
		}
		
		ArrayList<SessionPool> pools = new ArrayList<SessionPool>();
		for(String name: poolmap.keySet()){
			pools.add(new SessionPoolImpl(name, pooltype.get(name), poolmap.get(name)));
		}
		
	    if (farms.size()>0){
		    Collections.sort(farms,new Comparator<SessionFarm>(){  
	            public int compare(SessionFarm arg0, SessionFarm arg1) {  
	                return arg1.getAppSessionCount()- arg0.getAppSessionCount();
	            }  
	        });  
	    }
		
	    if (pools.size()>0){
		    Collections.sort(pools,new Comparator<SessionPool>(){  
	            public int compare(SessionPool arg0, SessionPool arg1) {  
	                return arg1.getSessionCount()- arg0.getSessionCount();
	            }  
	        });  
	    }
	    
		return generateSessionReport(pools, farms);
	}
	
	public static ClientReport generateClientReport(String sPoolFolder){
		
		return CEIPReportUtil.generateReport(sPoolFolder);
	}
	
	
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
					}else{
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
