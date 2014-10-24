package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.viewapi.ViewAPIService;

public class SessionUtil {

	private static Logger log = Logger.getLogger(SessionUtil.class);
	private static ConcurrentHashMap <String, ToolBoxSession> sessions = new ConcurrentHashMap<String,ToolBoxSession>();
	

	
	//maximum sessions, default is 30
	private static int maximumSessions = 30;
	
	public static void setMaximumSessions(int max){
		SessionUtil.maximumSessions = max;
	}
	
	
	private static void restrictMaxCapacity(){
		
		int toBeRemoved = sessions.size() - maximumSessions;
		if (toBeRemoved <= 0)
			return;
		
		ArrayList<ToolBoxSession> sessionlist = new ArrayList<ToolBoxSession>(sessions.values());
		Collections.sort(sessionlist);
		
		for (int i = 0; i < toBeRemoved; i++){
			HttpSession hsession = sessionlist.get(i).getSession();
			log.debug("Start to release session:" + hsession.getCreationTime());
			releaseSession(hsession);
		}
	}
	
	private synchronized static ToolBoxSession getOrNewToolBoxSession(HttpSession session){
		if (session == null){
			return null;
		}
		ToolBoxSession ts = sessions.get(session.getId());
		if (ts==null){
			ts = new ToolBoxSession(session);
			sessions.put(session.getId(), ts);
			restrictMaxCapacity();
		}
		return ts;
	}
	
	private static <T> T getSessionObj(HttpSession session, Class<T> klass) {
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		return ts == null ? null : ts.get(klass);
	}
	
	public static void setSessionObj(HttpSession session, Object o) {
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		if (ts != null)
			ts.set(o);
	}
	
	public static ViewAPIService getViewAPIService(HttpSession session){
		return getSessionObj(session, ViewAPIService.class);
	}
	
	public static LDAP getLDAP(HttpSession session){
		return getSessionObj(session, LDAP.class);
	}
		
	public static void setUser(HttpSession session, String username){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		if (ts!=null){
			ts.setUser(username);
		}
	}
	
	public static String getuser(HttpSession session){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		return (ts==null)? null:ts.getUser();
	}
	
	public static EventDBUtil getDB(HttpSession session){
		return getSessionObj(session, EventDBUtil.class);
	}
	
	
	public static void releaseSession(HttpSession session){
		if (session == null){
			return;
		}
		
		ToolBoxSession ts = sessions.remove(session.getId());
		if (ts!=null){
			ts.release();
		}
		
		
		//check the sessions to make sure no dead session
		
	}
}
