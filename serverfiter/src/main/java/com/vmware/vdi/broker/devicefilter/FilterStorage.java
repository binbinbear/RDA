package com.vmware.vdi.broker.devicefilter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vdi.broker.toolboxfilter.util.JsonUtil;
import com.vmware.vdi.broker.toolboxfilter.util.StringUtil;
import com.vmware.vdi.broker.toolboxfilter.util.ToolboxStorage;




public class FilterStorage {
	private static Logger log = Logger.getLogger(FilterStorage.class);
	private static final String AccessPolicy = "AccessPolicy_";

	private static final String isBlackKey="adminDescription";



	public DeviceFilterPolicy getPolicy(String poolname){
		if (poolname==null){
			log.error("Why pool name is null here?!");
			return null;
		}

		String namekey = AccessPolicy + poolname;

		String isBlack = ToolboxStorage.getStorage().get(namekey,isBlackKey );
		if (StringUtil.isEmpty(isBlack)){
			//no policy
			return null;
		}
		//there is a policy
		boolean isBlackBool = Boolean.parseBoolean(isBlack);
		DeviceFilterPolicy policy = new DeviceFilterPolicy(poolname);
		policy.setIsBlack(isBlackBool);
		log.debug("Policy is black:" +isBlackBool + " for pool:"+poolname);

		List<DeviceFilterItem> filteritems = new ArrayList<DeviceFilterItem>();
		List<String> items = ToolboxStorage.getStorage().getList(namekey);
		for (String policyStr: items){
			try{
				DeviceFilterItem item = JsonUtil.jsonToJava(policyStr, DeviceFilterItem.class);
				filteritems.add(item);
			}catch(Exception ex){
				log.error("Can't get policy for str:"+policyStr, ex);
			}

		}

		policy.setItems(filteritems);

		return policy;

	}


	public void addOrUpdate(DeviceFilterPolicy policy){
		String namekey = AccessPolicy + policy.getPoolName();
		ToolboxStorage.getStorage().set(namekey, isBlackKey, String.valueOf(policy.getIsBlack()));
		List<String> items = new ArrayList<String>();
		for (DeviceFilterItem item: policy.getItems()){
			items.add(JsonUtil.javaToJson(item));
		}
		ToolboxStorage.getStorage().setList(namekey, items);
	}

	public void remove(String poolname){
		if (StringUtil.isEmpty(poolname)){
			log.error("Why pool name is null here when removing?!");
			return;
		}
		String namekey = AccessPolicy + poolname;
		ToolboxStorage.getStorage().delete(namekey);

	}

}
