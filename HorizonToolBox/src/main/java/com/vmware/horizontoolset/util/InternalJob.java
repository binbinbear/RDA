package com.vmware.horizontoolset.util;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class InternalJob implements Job{
	private Logger log = Logger.getLogger(InternalJob.class);
	public InternalJob(){
		
	}
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CronJob cronjob = (CronJob)context.getJobDetail().getJobDataMap().get("CronJob");
		cronjob.execute();
	}
}