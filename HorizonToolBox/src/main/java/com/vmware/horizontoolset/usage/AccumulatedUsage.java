package com.vmware.horizontoolset.usage;

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
	@Override
	public int compareTo(AccumulatedUsage o) {
		return 0;
	}
}
