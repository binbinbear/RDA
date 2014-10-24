package com.vmware.horizontoolset.device.data;


public class WhitelistRecord extends UniqueRecord {
	
	public DeviceInfo deviceInfo;
	public long lastAccessTime;
	
	public WhitelistRecord(DeviceInfo di) {
		this.deviceInfo = di;
		this.lastAccessTime = -1;	//never accessed.
	}
}
