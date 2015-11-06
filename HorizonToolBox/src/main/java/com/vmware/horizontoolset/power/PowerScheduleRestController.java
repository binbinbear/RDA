package com.vmware.horizontoolset.power;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.CronScheduler;
import com.vmware.horizontoolset.util.SessionUtil;

@RestController
public class PowerScheduleRestController {

	private CronScheduler<PowerOnJob> scheduler;
	
	private Logger log = Logger.getLogger(PowerScheduleRestController.class);
	
	public PowerScheduleRestController(){
		log.info("Start to boot the power management scheduler");
		//this is to start the crontab job
		try{
			scheduler = new CronScheduler<PowerOnJob>();
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}
	}
	
	  @RequestMapping("/power/update")
		public String updatePowerPolicy(HttpSession session,
				@RequestParam(value="poolName", required=true) String poolName,
				@RequestParam(value="cron", required=true) String cron,
				@RequestParam(value="interval",required=false, defaultValue="5000")String interval) {
	    	
	    	try {
	    		int n = Integer.parseInt(interval);
	    		PowerOnJob job = new PowerOnJob(poolName, cron, n);
	    		scheduler.addOrUpdateCron(job);
	    		return "successful ";
	    	} catch (Exception e) {
	    		log.warn("Error updating app limit.", e);
	    		return "failed";
	    	}
	    } 
	
	  @RequestMapping("/power/all")
	    public List<PowerOnJob> getAllPowerPolicies(HttpSession session) {
		  List<PowerOnJob> alljobs = this.scheduler.getAllJobs();
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
