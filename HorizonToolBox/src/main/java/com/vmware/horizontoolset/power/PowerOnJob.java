package com.vmware.horizontoolset.power;


import com.vmware.horizontoolset.util.CronJob;

public class PowerOnJob implements CronJob{

	private String poolName;
	private String cron;
	
	
	public PowerOnJob(String poolName, String cron){
		this.poolName = poolName;
		this.cron = cron;
	}
	
	public void setPoolName(String poolName){
		this.poolName = poolName;
	}
	
 	public String getPoolName() {
		return poolName;
	}

	public void setCron(String cron){
		this.cron = cron;
	}
	
	@Override
	public void execute()  {
		for (int i=0;i<10;i++){
			System.out.println(this.poolName + " : Power on "+i);
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public String getCron() {
		
		return cron;
	}

}
