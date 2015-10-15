package com.vmware.horizontoolset.wsproxy;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.vmware.horizontoolset.console.VMServiceImplVCenter;

public class Interceptor extends HttpSessionHandshakeInterceptor{
	private static Logger log = Logger.getLogger(Interceptor.class);

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
        		log.debug("protocols.size() :" + protocols.size());
        		/*String targetProtocol = protocols.get(0);
        		log.debug("Get the protocols :" + targetProtocol);
        		//the default one is binary
        		for (String p: protocols){
        			log.debug("Get the protocols pppp:" + p);
        			if (p.equalsIgnoreCase("binary")){
        				targetProtocol = p;
        				break;
        			}
        		}*/
        		
        		String targetProtocol = "";
        		for (String protocol:protocols){
        			String[] ptls = protocol.split(",");
        			if (targetProtocol.isEmpty()){
        				targetProtocol = ptls[0];
        			}
        			for (String p:ptls){
        				if (p.equalsIgnoreCase("binary")){
            				targetProtocol = p;
            				break;
            			}
        			}
        			if (targetProtocol.equalsIgnoreCase("binary")){
        				break;
        			}
        		}
        		
        		response.getHeaders().add(key, targetProtocol);
        	}
    	}


        super.afterHandshake(request, response, wsHandler, ex);
    }
}
