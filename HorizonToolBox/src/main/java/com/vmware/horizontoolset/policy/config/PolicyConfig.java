package com.vmware.horizontoolset.policy.config;

import java.util.Map;

//配置文件中，policy的数据模型
public class PolicyConfig {
	public String policyId; //Policy的name，自己起名，是AD中实际的Name
	
	public boolean hasOptions; 	//是否包含子配置选项
	//当没有子配置选项时候，使用K，V标识
	public String policyKey;
	public String policyValue;
	
	public Map<String,ItemConfig> items;

	public PolicyConfig(String policyId, boolean hasOptions, String policyKey,
			String policyValue, Map<String, ItemConfig> items) {
		this.policyId = policyId;
		this.hasOptions = hasOptions;
		this.policyKey = policyKey;
		this.policyValue = policyValue;
		this.items = items;
	}
	
	public PolicyConfig(){}
	
	public ItemConfig getItemEntry(String itemId){
		return items.get(itemId);
	}
	
}
