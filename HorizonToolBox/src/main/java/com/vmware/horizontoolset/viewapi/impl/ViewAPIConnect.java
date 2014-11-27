package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vim.vmomi.client.http.HttpConfiguration;
import com.vmware.vim.vmomi.client.http.ThumbprintVerifier;

public class ViewAPIConnect extends Connection {

	private static HttpConfiguration httpConfig = HttpConfiguration.Factory.newInstance();
	static {
		httpConfig.setThumbprintVerifier(ThumbprintVerifier.Factory.createAllowAllThumbprintVerifier());
	}
	public ViewAPIConnect(String hostName) {
		super(String.format("https://%s/view-vlsi/sdk", hostName), httpConfig);
	}
}
