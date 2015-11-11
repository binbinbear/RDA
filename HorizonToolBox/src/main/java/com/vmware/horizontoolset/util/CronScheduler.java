package com.vmware.horizontoolset.util;


import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;

import org.quartz.JobDetail;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.quartz.impl.StdSchedulerFactory;




public class CronScheduler<T extends CronJob> {

	private static Logger log = Logger.getLogger(CronScheduler.class);
	private Scheduler scheduler;
	public CronScheduler() {
		try{
			this.scheduler = new StdSchedulerFactory().getScheduler();
			this.scheduler.start();
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}
		
	}


	
	public void addOrUpdateCron( T job) throws SchedulerException{
			scheduler.deleteJob(JobKey.jobKey(job.getKey()));
			
			JobDetail jobdetail = JobBuilder.newJob(InternalJob.class)
					.withIdentity(job.getKey()).build();
			
			jobdetail.getJobDataMap().put("CronJob", job);
			
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(job.getKey())
					.withSchedule(
						CronScheduleBuilder.cronSchedule(job.getCron())).build();
			
			scheduler.scheduleJob(jobdetail, trigger);

	}

	public void clear() throws SchedulerException{
		this.scheduler.clear();
	}
	

	public void removeCron(String id) throws SchedulerException{
		scheduler.deleteJob(JobKey.jobKey(id));
	}
	
}

