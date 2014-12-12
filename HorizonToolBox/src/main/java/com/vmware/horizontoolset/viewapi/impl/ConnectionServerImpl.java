package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.ConnectionServer;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer.ConnectionServerInfo;

public class ConnectionServerImpl implements ConnectionServer{

	private String name;
	private String version;
	public ConnectionServerImpl(ConnectionServerInfo info){
		this.name = info.getGeneral().getName();
		this.version= info.getGeneral().getVersion();
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
