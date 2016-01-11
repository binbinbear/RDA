package com.vmware.horizontoolset.devicefilter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.ToolboxStorage;
import com.vmware.horizontoolset.viewapi.operator.CachedObjs;



public class FilterStorage {
	private static Logger log = Logger.getLogger(FilterStorage.class);
	private static final String AccessPolicy = "AccessPolicy";
	//dont' use casche for now
	public final CachedObjs<DeviceFilterPolicy> policies = new CachedObjs<DeviceFilterPolicy>(1) {

		@Override
		protected void populateCache(List<DeviceFilterPolicy> objects) {

			objects.clear();
			List<String> allpolicies = ToolboxStorage.getStorage().getList(AccessPolicy);
			for (String policyStr: allpolicies){
				try{
					DeviceFilterPolicy policy = JsonUtil.jsonToJava(policyStr, DeviceFilterPolicy.class);
					objects.add(policy);
				}catch(Exception ex){
					log.error("Can't get policy for str:"+policyStr, ex);
				}

			}
			log.info("Policy result in Storage:"+objects.size());
		}
	};


	public void addOrUpdate(DeviceFilterPolicy policy){
		List<DeviceFilterPolicy> allpolicies = this.policies.get(true);
		allpolicies.remove(policy);
		allpolicies.add(policy);
		List<String> allnew = new ArrayList<String>();
		for (DeviceFilterPolicy p: allpolicies){
			allnew.add(JsonUtil.javaToJson(p));
		}

		ToolboxStorage.getStorage().setList(AccessPolicy, allnew);
		this.policies.get(true);

	}

}
