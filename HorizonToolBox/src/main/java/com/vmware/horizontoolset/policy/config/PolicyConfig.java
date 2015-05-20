package com.vmware.horizontoolset.policy.config;

import java.util.Map;

public class PolicyConfig {
	
	public static enum PolicyType{
		PCOIP,		
		COMMON,		// enable时,Data值为true; disable时,Data值为false
		USB,
		DEF
	}
	
	public String policyId;
	
	//disabled情况下，当没有子配置选项时候，使用K，V标识	
	public String policyKey;
	public String policyValue;
	public boolean reverse;   //标志位，当没有子配置项时候，enable/disable的值是 0/1 还是 1/0
	
	public PolicyType pType;
	
	public Map<String,ItemConfig> items;	//子配置项

	public PolicyConfig(String policyId, String policyKey,
			String policyValue, boolean reverse) {
		
		this.policyId = policyId;
		this.policyKey = policyKey;
		this.policyValue = policyValue;
		this.reverse = reverse;
		this.items = null;
		this.pType = PolicyType.DEF;
	}
	
	//没有子配置项，单独配置common，enable为true时
	public PolicyConfig(String policyId, String policyKey,
			String policyValue, boolean reverse, PolicyType pType) {
		
		this.policyId = policyId;
		this.policyKey = policyKey;
		this.policyValue = policyValue;
		this.reverse = reverse;
		this.items = null;
		this.pType = pType;
	}
	

	public PolicyConfig(String policyId, Map<String, ItemConfig> items) {

		this.policyId = policyId;
		this.policyKey = null;
		this.policyValue = null;
		this.reverse = false;
		this.items = items;
		this.pType = PolicyType.DEF;
	}
	
	public ItemConfig getItemEntry(String itemId){
		return items.get(itemId);
	}
	
	@Override
	public String toString(){
		if(items != null){
			return " id="+policyId+", itemMap: "+items.toString();
		}else{
			return " id="+policyId+", policyKey="+policyKey+", policyValue="+policyValue;
		}
		
	}
	
}