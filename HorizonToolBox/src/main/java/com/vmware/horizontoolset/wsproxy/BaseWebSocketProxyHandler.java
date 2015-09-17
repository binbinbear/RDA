package com.vmware.horizontoolset.wsproxy;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * This base handler: 
 * 	1. tunnels messages to/from the associated peer handler,
 * 	2. maintains connection status (e.g. close peer on connection close).
 * 	3. cache flooding message when one any of the peers is not ready yet.
 * 
 * The handler itself is state-less. The state should all be kept in session.
 * 
 *
 */
public abstract class BaseWebSocketProxyHandler implements WebSocketHandler {
	
	//private static final Logger log = Logger.getLogger(BaseWebSocketProxyHandler.class);
	
	static final String PEER_SESSION = "PEER_SESSION";
	private static final String PENDING_MESSAGES = "PENDING_MESSAGES";
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		close(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus closeStatus) throws Exception {
		close(session);
	}

	static void close(WebSocketSession session) {

		// must assure both sessions (if any) are closed.
		//peer session should be retrieved first
		
		WebSocketSession peerSession = null;
		try {
			peerSession = (WebSocketSession) session.getAttributes().remove(PEER_SESSION);
		} catch (Exception e) {
		}
		
		try {
			session.close();
		} catch (Exception e) {
		}
		
		if (peerSession != null) {
			try {
				peerSession.close();
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		WebSocketSession peer = getPeerSession(session);
		
		if (peer != null) {
			
			flushPendingMessages(session, peer);
			
			//redirect message
			peer.sendMessage(message);
			
		} else {
			//queue the message.
			List<WebSocketMessage<?>> msgs = getPendingMessages(session);
			msgs.add(message);
		}
	}
	
	private WebSocketSession getPeerSession(WebSocketSession thisSession) {
		return (WebSocketSession) thisSession.getAttributes().get(PEER_SESSION);
	}
	
	private static List<WebSocketMessage<?>> removePendingMessages(WebSocketSession session) {
		@SuppressWarnings("unchecked")
		List<WebSocketMessage<?>> pendingMessages = (List<WebSocketMessage<?>>) session.getAttributes().remove(PENDING_MESSAGES);
		return pendingMessages;
	}
	
	private static List<WebSocketMessage<?>> getPendingMessages(WebSocketSession session) {
		@SuppressWarnings("unchecked")
		List<WebSocketMessage<?>> pendingMessages = (List<WebSocketMessage<?>>) session.getAttributes().get(PENDING_MESSAGES);
		
		if (pendingMessages == null) {
			pendingMessages = new ArrayList<WebSocketMessage<?>>();
			session.getAttributes().put(PENDING_MESSAGES, pendingMessages);
		}
		return pendingMessages;
	}
	
	static void flushPendingMessages(WebSocketSession thisSession, WebSocketSession peerSession) throws IOException {

		//flush pending messages if any
		List<WebSocketMessage<?>> pendingMessages = removePendingMessages(thisSession);
		if (pendingMessages != null) {
			for (WebSocketMessage<?> m : pendingMessages)
				peerSession.sendMessage(m);
		}
	}
}
