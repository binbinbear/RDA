package com.vmware.horizon.auditing.report;

public class AccumulatedUsage implements Comparable<AccumulatedUsage>{
	private String userName;
	private long usageTime;
	public AccumulatedUsage(String userName, long usageTime){
		this.userName = userName;
		this.usageTime = usageTime;
	}
	public String getUserName() {
		return userName;
	}
	public long getUsageTime() {
		return usageTime;
	}
	
	public void addTime(long newtime){
		this.usageTime  =this.usageTime + newtime;
	}

	public int compareTo(AccumulatedUsage o) {
		if (this.usageTime>o.usageTime){
			return -1;
		}else if (this.usageTime<o.usageTime){
			return 1;
		}
		return userName.compareTo(o.userName);
	}
}
