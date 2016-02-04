package com.vmware.horizontoolset.devicefilter;

import java.util.Map;

import com.vmware.horizontoolset.util.StringUtil;


public class DeviceFilterItem {

	public DeviceFilterItem(DeviceFilterEnum type, String reg){
		this.type = type;
		this.reg = reg;
		this.grep = DeviceFilterGrep.MATCHES;
	}

	private DeviceFilterGrep grep;

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


	//true for matched, false for mis-matched
	public boolean checkMatched(Map<String, String> env){
		String key = this.type.toString();
		String value = env.get(key);
		if (!StringUtil.isEmpty(value) && reg.matches(value)){
			return true;
		}
		return false;

	}
	public DeviceFilterGrep getGrep() {
		return grep;
	}
	public void setGrep(DeviceFilterGrep grep) {
		this.grep = grep;
	}
}
