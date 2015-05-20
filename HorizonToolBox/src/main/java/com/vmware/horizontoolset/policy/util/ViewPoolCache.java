package com.vmware.horizontoolset.policy.util;

import java.util.Date;
import java.util.Map;


public class ViewPoolCache{
	private Date updatedDate;
	private Map<String,String> cached_ou;
	
	public ViewPoolCache(Map<String,String> cached_ou){
		this.updatedDate =new Date();
		this.cached_ou = cached_ou;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	public void updateDate(){
		this.updatedDate = new Date();
	}

	public Map<String, String> getCached_ou() {
		return cached_ou;
	}

	public void setCached_ou(Map<String, String> cached_ou) {
		this.cached_ou = cached_ou;
	}

	
}
