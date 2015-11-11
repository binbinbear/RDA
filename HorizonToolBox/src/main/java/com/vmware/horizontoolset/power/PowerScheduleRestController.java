package com.vmware.horizontoolset.power;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.catalina.manager.util.SessionUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.CronScheduler;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;


/**
 *
 * @author Administrator
 *
 */
@RestController
public class PowerScheduleRestController {

	private PersistancePowerScheduler scheduler;
	
	private Logger log = Logger.getLogger(PowerScheduleRestController.class);
	
	public PowerScheduleRestController(){
		log.info("Start to boot the power management scheduler");
		//this is to start the crontab job
		this.scheduler = PersistancePowerScheduler.getInstance();
	}
	
	  @RequestMapping("/power/update")
		public String updatePowerPolicy(HttpSession session,
				@RequestParam(value="poolName", required=true) String poolName,
				@RequestParam(value="cron", required=true) String cron,
				@RequestParam(value="interval",required=false, defaultValue="5000")String interval) {
	    	log.info("Start to update policy:"+ poolName + " cron: "+  cron  );
	    	try {
	    		int n = Integer.parseInt(interval);
	    		PowerOnJob job = new PowerOnJob(poolName, cron, n);
	    		scheduler.addOrUpdateCron(job);
	    		
	    		return "successful ";
	    	} catch (Exception e) {
	    		log.warn("Error updating power policy for pool "+ poolName + " cron:"+cron, e);	    		
	    		return "failed";
	    	}
	    } 
	  
	  @RequestMapping("/power/remove")
			public String removePowerPolicy(HttpSession session,
					@RequestParam(value="poolName", required=true) String poolName) {
		  log.info("Start to remove policy:"+ poolName );
		    	try {
		    		scheduler.removeCron(poolName);			    		
		    		return "successful ";
		    	} catch (Exception e) {
		    		log.warn("Error removing power policy.", e);
		    		return "failed";
		    	}
		    } 
	
	  @RequestMapping("/power/all")
	    public List<PowerOnJob> getAllPowerPolicies(HttpSession session) {
		  	ArrayList<PowerOnJob> alljobs = new ArrayList<PowerOnJob>();
		  	alljobs.addAll(scheduler.getAllJobs());
		  	
			List<String> pools = SessionUtil.getAllDesktopPools(session);
			for (String pool:pools){
				PowerOnJob j = new PowerOnJob(pool);
				if (!alljobs.contains(j)){
					alljobs.add(j);
				}
			}
		  	
		  	return alljobs;
		}
	  
	  
}
