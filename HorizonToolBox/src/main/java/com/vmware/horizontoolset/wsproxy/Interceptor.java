package com.vmware.horizontoolset.wsproxy;


import java.util.List;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class Interceptor extends HttpSessionHandshakeInterceptor{

	@Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
 
    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
    	
    	//TODO
    	//As a proxy, this should be negotiated during handshake.
    	//Response what server responses. Currently use only binary
    	String key = "sec-websocket-protocol";
    	if(!response.getHeaders().containsKey(key)){
        	List<String> protocols = request.getHeaders().get(key);
        	
        	if (protocols!=null && protocols.size()>0){
        		String targetProtocol = protocols.get(0);
        		//the default one is binary
        		for (String p: protocols){
        			if (p.equalsIgnoreCase("binary")){
        				targetProtocol = p;
        				break;
        			}
        		}
        		response.getHeaders().add(key, targetProtocol);
        	}
    	}


        super.afterHandshake(request, response, wsHandler, ex);
    }
}
