package com.vmware.horizontoolset.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.AccumulatedUsage;
import com.vmware.horizontoolset.usage.Connection;
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
	
	public static AccumulatedUsageReport generateUsageReport(List<Connection> connections){
		
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
		
		AccumulatedUsageReport report = new AccumulatedUsageReport(list);
		return report;
	}

	
}
