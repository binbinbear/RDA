package com.vmware.vdi.broker.devicefilter;

import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vdi.broker.toolboxfilter.util.StringUtil;

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

	private static Logger log = Logger.getLogger(DeviceFilterItem.class);
	//true for matched, false for mis-matched
	public boolean checkMatched(Map<String, String> env){

		String key = this.type.toString();
		String value = env.get(key);
		log.info("key:"+ key + " value in environment:"+ value+ " reg in filter:"+ reg);
		if (!StringUtil.isEmpty(value) && reg.matches(value)){
			log.info("rule matched");
			return true;
		}
		log.info("rule not matched");
		return false;

	}
}
