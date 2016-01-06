package com.vmware.horizontoolset.devicefilter;

import java.util.Date;

public class DeviceFilterResult {
	private String poolName;


	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	private String ip;
	private String mac;
	private String os;
	private String version;
	private Date time;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}


}
