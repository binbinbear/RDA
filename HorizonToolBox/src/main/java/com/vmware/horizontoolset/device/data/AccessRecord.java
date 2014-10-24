package com.vmware.horizontoolset.device.data;


public class AccessRecord extends UniqueRecord {
	
	public static enum AccessResult	{
		ALLOWED,
		BLOCKED,
		ADDED_TO_WHITELIST
	}
	
	public final DeviceInfo deviceInfo;
	public final long time;
	public AccessResult result;
	
	AccessRecord(DeviceInfo di, boolean isAllowed) {
		this.deviceInfo = di;
		time = System.currentTimeMillis();
		this.result = isAllowed ? AccessResult.ALLOWED : AccessResult.BLOCKED;
	}
}
