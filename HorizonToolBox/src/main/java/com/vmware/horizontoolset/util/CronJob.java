package com.vmware.horizontoolset.util;

public interface CronJob {
	public void execute();
	//cron description,  Seconds Minutes Hours DayofMonth Month DayofWeek , example, 0 * * * * ?
	public String getCron();
}
