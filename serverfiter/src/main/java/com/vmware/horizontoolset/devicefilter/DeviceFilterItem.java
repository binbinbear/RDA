package com.vmware.horizontoolset.devicefilter;

public class DeviceFilterItem {

	public DeviceFilterItem(DeviceFilterEnum type, String reg){
		this.type = type;
		this.reg = reg;
	}
	private DeviceFilterEnum type;
	public DeviceFilterEnum getType() {
		return type;
	}
	public void setType(DeviceFilterEnum type) {
		this.type = type;
	}
	private String reg;
	public String getReg() {
		return reg;
	}
	public void setReg(String reg) {
		this.reg = reg;
	}

}
