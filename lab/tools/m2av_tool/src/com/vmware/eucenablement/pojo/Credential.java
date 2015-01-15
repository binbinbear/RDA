package com.vmware.eucenablement.pojo;

import com.vmware.eucenablement.horizontoolset.av.api.VolumeAPI;

public class Credential {
	public String name;
	public String password;
	public String server;
	public String domain;

	public Credential(String name, String password, String server, String domain) {
		super();
		this.name = name;
		this.password = password;
		this.server = server;
		this.domain = domain;
	}

	public boolean isLogin(VolumeAPI v_api) {
		try {
			v_api.connect(server, domain, name, password);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
