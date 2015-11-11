package com.vmware.horizontoolset.power;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;


import com.vmware.horizontoolset.util.CronScheduler;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;

//this must be singleton
public class PersistancePowerScheduler extends CronScheduler<PowerOnJob>{

	private static PersistancePowerScheduler _instance = new PersistancePowerScheduler();
	
	public static PersistancePowerScheduler getInstance(){
		return _instance;
	}
	
	private static Logger log = Logger.getLogger(PersistancePowerScheduler.class);
	
	@Override
	public  void addOrUpdateCron(PowerOnJob job) throws SchedulerException {
		super.addOrUpdateCron(job);
		PowerOnJobStorage.savePowerOnJob(job);
	}

	@Override
	public void clear() throws SchedulerException {
		super.clear();
		PowerOnJobStorage.clear();
	}

	@Override
	public void removeCron(String id) throws SchedulerException {
		
		super.removeCron(id);
		PowerOnJobStorage.removePowerOnJob(id);
	}
	
	public Collection<PowerOnJob> getAllJobs(){
		return PowerOnJobStorage.getAllPowerOnJobs().values();
	}

	private PersistancePowerScheduler()  {
		super();
		try{
			Collection<PowerOnJob> alljobs = this.getAllJobs();
			for (PowerOnJob job: alljobs){
				super.addOrUpdateCron(job);
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}
	}

}

class PowerOnJobStorage {

	private static final String mapkey = PowerOnJob.class.getSimpleName();
	public static Map<String, PowerOnJob> getAllPowerOnJobs(){
		Map<String, PowerOnJob> alljobs = new HashMap<String, PowerOnJob>();
		Map<String, String> alljobstrs = SharedStorageAccess.getMap(mapkey);
		for (String key: alljobstrs.keySet()){
			PowerOnJob job = JsonUtil.jsonToJava(alljobstrs.get(key), PowerOnJob.class);
			if (job!=null){
				alljobs.put(key, job);
			}
			
		}
		return alljobs;
	}
	
	public static boolean savePowerOnJob(PowerOnJob job){
		Map<String, String> alljobstrs = SharedStorageAccess.getMap(mapkey);
		alljobstrs.put(job.getKey(), JsonUtil.javaToJson(job));
		SharedStorageAccess.setMap(mapkey, alljobstrs);	
		return true;
	}
	
	public static boolean removePowerOnJob(String jobKey){
		Map<String, String> alljobstrs = SharedStorageAccess.getMap(mapkey);
		alljobstrs.remove(jobKey);
		SharedStorageAccess.setMap(mapkey, alljobstrs);	
		return true;
	}
	
	public static boolean clear(){
		
		SharedStorageAccess.setMap(mapkey, new HashMap<String, String>());	
		return true;
	}
	
}
