package com.vmware.horizontoolset.policy.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyModel {	// java后端数据模型
	public String PolicyId;
	public int enabled;		// 0-enabled, 1-disabled 2-not configured
	public Map<String,String> items;
	
	
	public PolicyModel(String policyId, int enabled,
			Map<String, String> items) {
		this.PolicyId = policyId;
		this.enabled = enabled;
		this.items = items;
	}
	
	public PolicyModel(){
		
	}
}
