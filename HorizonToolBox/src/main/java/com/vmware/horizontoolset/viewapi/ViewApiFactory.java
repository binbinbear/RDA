package com.vmware.horizontoolset.viewapi;

import com.vmware.horizontoolset.viewapi.impl.ViewAPIServiceImpl;

public class ViewApiFactory {
	
	public static ViewAPIService createNewAPIService(String server, String username, String password, String domain){
		return new ViewAPIServiceImpl(server, username, password, domain);

	}
	

}
