package com.vmware.vdi.broker.devicefilter;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DeviceFilterPolicy {
	private String poolName;

	private static Logger log = Logger.getLogger(DeviceFilterPolicy.class);
	private boolean isBlack;

	public DeviceFilterPolicy(String poolname){
		this.poolName = poolname;
	}

	private List<DeviceFilterItem> items;

	public String getPoolName() {
		return poolName;
	}

	public boolean getIsBlack(){
		return this.isBlack;
	}



	public void setIsBlack(boolean isBlack){
		this.isBlack = isBlack;
	}

	public List<DeviceFilterItem> getItems() {
		return items;
	}

	public void setItems(List<DeviceFilterItem> items) {
		this.items = items;
	}


	private boolean _checkAccess (Map<String, String> envInfo){
		log.info("This poolname:" + this.poolName+" This isBlack:"+ this.isBlack);
		for (DeviceFilterItem item : items){
			if (item.checkMatched(envInfo)){
				log.info("item matched:"+ item.getReg());
				if (this.isBlack){
					//if this is a black list and one black item is matched, we block directly
					log.info("block due to black list");
					return false;
				}else{
					log.info("allow due to white list");
					//if this is a white list and one white item is matched, we allow directly
					return true;
				}
			}
		}
		//if this is a white list and none item is matched, we block; if this is a black list and none is matched, we allow;
		return this.isBlack;
	}

	//true for allow, false for dis-allow;
	public boolean checkAccess(Map<String, String> envInfo){
		boolean isAllowed = this._checkAccess(envInfo);
		if (!isAllowed){
			//write log to a special file
			BlockedLogger.getInstance().logBlockedAccess(new BlockedAccess(envInfo, this.poolName));
		}

		return isAllowed;
	}


}
