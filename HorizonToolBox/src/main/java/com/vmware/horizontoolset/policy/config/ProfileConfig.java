package com.vmware.horizontoolset.policy.config;

import java.util.HashMap;
import java.util.Map;

import com.vmware.horizontoolset.policy.config.ItemConfig.PolEntryType;
import com.vmware.horizontoolset.util.JsonUtil;

public class ProfileConfig {
	private Map<String,PolicyConfig> policies;

	public ProfileConfig(Map<String, PolicyConfig> policies) {
		this.policies = policies;
	}
	
	public ProfileConfig(){
		policies = new HashMap<String, PolicyConfig>();
	}
	
	public PolicyConfig getPolicy(String policyId){
		return policies.get(policyId);
	}


	public static void main(String[] args){
		ProfileConfig allPol = new ProfileConfig();

		PolicyConfig pConf1 = new PolicyConfig();
		//pConf1.policyId="Configure SSL connections to satisfy Security Tools";
		pConf1.policyId="SSL";
		pConf1.hasOptions=true;
		Map<String,ItemConfig> itemsMap = new HashMap<String,ItemConfig>();
		ItemConfig item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ask_clients_to_use_vcs_sni", PolEntryType.REG_DWORD);         // checkbox       
		ItemConfig item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ca_cert_store_name", PolEntryType.REG_SZ);					// ROOT
		ItemConfig item3 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.cert_store_name", PolEntryType.REG_SZ);						// name
		ItemConfig item4 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_deny_legacy_connections", PolEntryType.REG_DWORD);	// 最上方，checkBox
		ItemConfig item5 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_min_key_length", PolEntryType.REG_DWORD);			// 1024,2048,4096
		ItemConfig item6 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_type", PolEntryType.REG_DWORD);					    // select
		ItemConfig item7 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_disable_aes_128", PolEntryType.REG_DWORD);				// checkBox
		itemsMap.put("policy2-3-6", item1);        // checkbox       
		itemsMap.put("policy2-3-5", item2);        // ROOT           
		itemsMap.put("policy2-3-3", item3);        // name           
		itemsMap.put("policy2-3-1", item4);        // 最上方，checkBox   
		itemsMap.put("policy2-3-4", item5);        // 1024,2048,4096 
		itemsMap.put("policy2-3-2", item6);        // select         
		itemsMap.put("policy2-3-7", item7);        // checkBox   
		pConf1.items = itemsMap;
		allPol.policies.put("SSL", pConf1);
		
		String ConfigJson = JsonUtil.javaToJson(allPol);
		System.out.println(ConfigJson);
		JsonUtil.save("c:/AboutPol/PolicyConfig.json", allPol);
	}
}
