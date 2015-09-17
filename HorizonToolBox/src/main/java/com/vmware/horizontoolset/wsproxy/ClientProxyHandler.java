package com.vmware.horizontoolset.wsproxy;




import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * Client proxy handler is based on the base handler (which handles message tunneling),
 * with additional logic to establish to-server handler on connection established.
 * 
 */
public class ClientProxyHandler extends BaseWebSocketProxyHandler {
	
	private static final Logger log = Logger.getLogger(ClientProxyHandler.class);
	
	private RequestRewriter config;
	
	public void setConfig(RequestRewriter conf) {
		config = conf;
	}

	
	
	@Override
	public void afterConnectionEstablished(final WebSocketSession session)
			throws Exception {
		
		try {
			log.debug("After Connection established in client proxy handler");
			ServerProxyHandler serverHandler = new ServerProxyHandler();
			log.debug("Server Proxy Handerl is ready");
			
			WebSocketHttpHeaders headers = new WebSocketHttpHeaders(session.getHandshakeHeaders());
			
			StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
			log.debug("Websocket client is ready");
			
			URI oldUri = session.getUri();
			URI targetServerUri = config.rewriteRequest(session.getUri());
			log.debug("Try to create new connection to "+targetServerUri.toString());
			if (oldUri.toASCIIString().equals(targetServerUri.toASCIIString()))
				throw new Exception("It seems the URI rewriting failed. The URI is not changed. This is likely an issue with configuration, or request context.");
			
			ListenableFuture<WebSocketSession> future = webSocketClient.doHandshake(
					serverHandler, headers, targetServerUri);
			log.debug("Handshake is successful");
			
			ListenableFutureCallback<WebSocketSession> callback = new ListenableFutureCallback<WebSocketSession>() {
	
				@Override
				public void onSuccess(WebSocketSession result) {
					session.getAttributes().put(PEER_SESSION, result);
					result.getAttributes().put(PEER_SESSION, session);
					
					//flush any pending messages
					try {
						BaseWebSocketProxyHandler.flushPendingMessages(session, result);
						BaseWebSocketProxyHandler.flushPendingMessages(result, session);
					} catch (Exception e) {
						log.debug("Error flushing pending messages.", e);
						close(session);
					}
				}
	
				@Override
				public void onFailure(Throwable t) {
					try {
						session.close();
					} catch (IOException e) {
						log.info("Error closing peer session", e);
					}
					log.info("Fail establishing WS proxy connection to server", t);
				}
			};
			
			future.addCallback(callback);
		} catch (Exception e) {
			log.info("Error establishing WebSocket server proxy", e);
			try {
				session.close();
			} catch (IOException e1) {
			}
		}
	}
}
