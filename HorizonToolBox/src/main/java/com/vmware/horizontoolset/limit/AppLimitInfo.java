package com.vmware.horizontoolset.limit;
public class AppLimitInfo {

	public final String appId;
	public int limit;
	public int concurrency;
	public boolean messageSent;
	public int exceeded;
	
	public AppLimitInfo(String appId, int limit, int concurrency) {
		this.appId = appId;
		this.limit = limit;
		this.concurrency = concurrency;
	}
	
	public boolean isLimitExceeded() {
		if (limit <= 0 || concurrency <= limit) {
			exceeded = 0;
			return false;
		}
		
		exceeded = 1;
		return true;
	}
}