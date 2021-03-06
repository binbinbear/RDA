package com.vmware.horizontoolset.power;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.vmware.horizontoolset.util.CronScheduler;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.ToolboxStorage;

//this must be singleton
public class PersistancePowerScheduler extends CronScheduler<PowerOnJob>{

	private static Logger log = Logger.getLogger(PersistancePowerScheduler.class);

	private static PersistancePowerScheduler _instance ;

	public static PersistancePowerScheduler getInstance(){
		if (_instance == null){
			_instance = new PersistancePowerScheduler();
		}
		return _instance;
	}



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
			log.error("Can't get jobs from LDAP",ex);
		}
	}

}

class PowerOnJobStorage {

	private static final String mapkey = PowerOnJob.class.getSimpleName();
	public static Map<String, PowerOnJob> getAllPowerOnJobs(){
		Map<String, PowerOnJob> alljobs = new HashMap<String, PowerOnJob>();
		Map<String, String> alljobstrs = ToolboxStorage.getStorage().getMap(mapkey);
		for (String key: alljobstrs.keySet()){
			PowerOnJob job = JsonUtil.jsonToJava(alljobstrs.get(key), PowerOnJob.class);
			if (job!=null){
				alljobs.put(key, job);
			}

		}
		return alljobs;
	}

	public static boolean savePowerOnJob(PowerOnJob job){
		Map<String, String> alljobstrs = ToolboxStorage.getStorage().getMap(mapkey);
		alljobstrs.put(job.getKey(), JsonUtil.javaToJson(job));
		ToolboxStorage.getStorage().setMap(mapkey, alljobstrs);
		return true;
	}

	public static boolean removePowerOnJob(String jobKey){
		Map<String, String> alljobstrs = ToolboxStorage.getStorage().getMap(mapkey);
		alljobstrs.remove(jobKey);
		ToolboxStorage.getStorage().setMap(mapkey, alljobstrs);
		return true;
	}

	public static boolean clear(){

		ToolboxStorage.getStorage().setMap(mapkey, new HashMap<String, String>());
		return true;
	}

}
