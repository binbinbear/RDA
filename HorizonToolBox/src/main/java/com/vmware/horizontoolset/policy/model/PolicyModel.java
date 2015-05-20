package com.vmware.horizontoolset.policy.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyModel {
	public String policyId;
	public int enabled;		// 0-enabled, 1-disabled 2-not configured
	public Map<String,String> items;
	
	public PolicyModel(String policyId, String enabled,
			Map<String, String> items) {
		this.policyId = policyId;
		this.enabled = Integer.parseInt(enabled);;
		this.items = items;
	}
	
	public PolicyModel(){
		
	}
	
	@Override
	public String toString(){
		return "policyId="+policyId+", enabled="+enabled+", itemsMap: "+items.toString();
	}
}
