package com.vmware.horizontoolset.devicefilter;

import java.util.List;
import java.util.Map;

public class DeviceFilterPolicy {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poolName == null) ? 0 : poolName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceFilterPolicy other = (DeviceFilterPolicy) obj;
		if (poolName == null) {
			if (other.poolName != null)
				return false;
		} else if (!poolName.equals(other.poolName))
			return false;
		return true;
	}


	private String poolName;

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

	//true for allow, false for dis-allow;
	public boolean checkAccess(Map<String, String> envInfo){

		for (DeviceFilterItem item : items){
			if (item.checkMatched(envInfo)){
				if (this.isBlack){
					//if this is a black list and one black item is matched, we block directly
					return false;
				}
			}else{
				if (!this.isBlack){
					//if this is a white list and one white item is matched, we allow directly
					return true;
				}
			}
		}
		//if this is a white list and none item is matched, we block; if this is a black list and none is matched, we allow;
		return this.isBlack;
	}
}
