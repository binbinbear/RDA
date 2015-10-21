package com.vmware.horizontoolset.viewapi.operator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.horizontoolset.util.StringUtil;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.client.Connection;
@JsonIgnoreProperties(value={"conn"})
public class Machine {

	private final Connection conn;
	private  String name;
	private String vmid;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public Connection getConn() {
		return conn;
	}

	public MachineSummaryView getSummaryView() {
		return summaryView;
	}
	private String dnsname;
	private  String os;
	private  String state;
	private  String pool;
	private String vcenterId;
	public final MachineSummaryView summaryView;

	public Machine(Connection conn, MachineSummaryView v) {
		this.conn = conn;
		this.summaryView = v;
		this.name = v.base.name;
		this.state = v.base.basicState;
		
		this.setDnsname(v.base.dnsName);
		this.setVcenterId(v.summaryData.virtualCenter.id);
		this.setVmid(v.id.id);
		
		this.os = "unknown";
		if (!StringUtil.isEmpty(v.base.operatingSystem)){
			if (v.base.operatingSystem.contains("Windows 7")){
				this.os = "windows7";
			}else if (v.base.operatingSystem.contains("Windows 7")){
				this.os = "windows7";
			}else if (v.base.operatingSystem.contains("Windows 8")){
				this.os = "windows8";
			}else if (v.base.operatingSystem.contains("Windows Server 2008")){
				this.os = "windows2k8";
			}else if (v.base.operatingSystem.contains("Windows Server 2012")){
				this.os = "windows2k12";
			}
		}
		
	}
	
	public Machine(Connection conn, MachineSummaryView v, String poolname) {
		this(conn,v);
		this.pool = poolname;
		
	}

	public String getVmid() {
		return vmid;
	}

	public void setVmid(String vmid) {
		this.vmid = vmid;
	}

	public String getVcenterId() {
		return vcenterId;
	}

	public void setVcenterId(String vcenterId) {
		this.vcenterId = vcenterId;
	}

	public String getDnsname() {
		return dnsname;
	}

	public void setDnsname(String dnsname) {
		this.dnsname = dnsname;
	}
}
