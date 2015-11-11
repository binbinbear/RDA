package com.vmware.horizontoolset.power;

import java.util.Date;

public class PowerOnJobResult {
	private Date starttime;
	private Date endtime;
	private String poolname;
	public PowerOnJobResult(String poolname){
		this.starttime = new Date();
		this.poolname = poolname;
	}
	
	private int successful = 0;
	private int failed = 0;
	void success(){
		successful ++;
	}
	
	void fail(String vmname){
		failed ++;
	}
	void end(){
		this.endtime = new Date();
	}
	
	public Date getStartTime(){
		return starttime;
		
	}
	
	
	public Date getEndTime(){
		return endtime;
		
	}
	
	public int getSuccessful(){
		return this.successful;
	}
	
	public int getFailed(){
		return this.failed;
	}
	
	public String getPoolName(){
		return this.poolname;
	}
	
}
