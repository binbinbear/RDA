package com.vmware.horizontoolset.devicefilter;

import java.util.List;

public class DeviceFilterPolicy {
	private String poolName;

	private boolean isBlack;

	public DeviceFilterPolicy(String poolname){
		this.poolName = poolname;
	}

	private List<DeviceFilterItem> items;

	public String getPoolName() {
		return poolName;
	}



	public boolean isBlack() {
		return isBlack;
	}

	public void setBlack(boolean isBlack) {
		this.isBlack = isBlack;
	}

	public List<DeviceFilterItem> getItems() {
		return items;
	}

	public void setItems(List<DeviceFilterItem> items) {
		this.items = items;
	}

}
