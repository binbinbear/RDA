package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	//TODO: persistance, and load
	private Map<String, T> alljobs;
	
	private Scheduler scheduler;
	public CronScheduler() throws SchedulerException{
		this.scheduler = new StdSchedulerFactory().getScheduler();
		this.scheduler.start();
		this.alljobs = new HashMap<String,T>();
	}


	public List<T> getAllJobs(){
		return new ArrayList<T>(alljobs.values());
		
	}
	
	public void addOrUpdateCron( T job) throws SchedulerException{
		
			this.alljobs.put(job.getKey(),job);
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

