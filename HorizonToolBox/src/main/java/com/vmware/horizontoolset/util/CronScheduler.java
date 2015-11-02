package com.vmware.horizontoolset.util;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;

import org.quartz.JobDetail;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.quartz.impl.StdSchedulerFactory;



public class CronScheduler {

	
	private Scheduler scheduler;
	public CronScheduler() throws SchedulerException{
		this.scheduler = new StdSchedulerFactory().getScheduler();
		this.scheduler.start();
	}

	public void addOrUpdateCron(String id, CronJob job) throws SchedulerException{
		
			scheduler.deleteJob(JobKey.jobKey(id));
			
			JobDetail jobdetail = JobBuilder.newJob(InternalJob.class)
					.withIdentity(id).build();
			
			jobdetail.getJobDataMap().put("CronJob", job);
			
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(id)
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

