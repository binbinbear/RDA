package com.vmware.horizontoolset.limit;
public class AppLimitInfo {

	public final String appId;
	public int limit;
	public int concurrency;
	
	public AppLimitInfo(String appId, int limit, int concurrency) {
		this.appId = appId;
		this.limit = limit;
		this.concurrency = concurrency;
	}
}