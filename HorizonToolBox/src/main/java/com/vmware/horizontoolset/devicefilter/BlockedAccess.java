package com.vmware.horizontoolset.devicefilter;

import java.util.Date;
import java.util.Map;

public class BlockedAccess {

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	private String mac;
	private String ip;
	private String user;
	private String pool;
	private String device;

	private String type;

	private Date date;
	public BlockedAccess(Map<String, String> envInfo, String pool){
		 mac = envInfo.get("MAC_Address");
		 ip = envInfo.get("IP_Address");
		 device = envInfo.get("Machine_Name");
		 user = envInfo.get("LoggedOn_Username");
		 setType(envInfo.get("Type"));
		 this.pool = pool;
		 this.setDate(new Date());
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
