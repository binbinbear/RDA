package com.vmware.horizontoolset.wsproxy;


import java.net.URI;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.console.ConsoleAccessInfo;
import com.vmware.horizontoolset.console.WebMKSController;

/**
 * Rewrite WebSocket request from client to Console Backend (CB).
 *
 *
 */
public class SimpleRewriter implements RequestRewriter {

	private static final Logger log = Logger.getLogger(RequestRewriter.class);
	
	
	
	@Override
	public URI rewriteRequest(URI original) throws Exception {
//		public URI(String scheme,
	   //            String userInfo, String host, int port,
	    //           String path, String query, String fragment)
		
		String query = original.getQuery();
		String uuid = getuuid(query);
		ConsoleAccessInfo info = WebMKSController.getAccessInfo(uuid);
		
		return new URI(info.getProtocol()+"://"+info.getHost()+":"+info.getPort()+info.getUri());
	}

	

	private String getuuid(String query) {
		
		return getArg(query, "uuid");
	}



	
	private String getArg(String query, String key){

		int index = query.indexOf(key+"=") + key.length() +1;
		int endindex = query.indexOf("&", index);
		if (endindex<=0){
			return query.substring(index);
		}else{
			return query.substring(index, endindex);
		}
		
	
	}


}
