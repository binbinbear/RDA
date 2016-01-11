package com.vmware.vdi.broker.devicefilter;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vdi.broker.toolboxfilter.util.CachedObjs;
import com.vmware.vdi.broker.toolboxfilter.util.JsonUtil;
import com.vmware.vdi.broker.toolboxfilter.util.ToolboxStorage;


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

}
