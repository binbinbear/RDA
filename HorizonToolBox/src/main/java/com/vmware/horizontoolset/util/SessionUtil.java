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
	
	
	private static void clearSessions(){
		
		ArrayList<ToolBoxSession> sessionlist = new ArrayList<ToolBoxSession>(sessions.values());
		Collections.sort(sessionlist);
		int toberemoved = sessions.size() - maximumSessions;
		for (int i=0;i<toberemoved; i++){
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
			if (sessions.size()> maximumSessions){
				clearSessions();
			}
		}
		return ts;
	}
	
	public static ViewAPIService getViewAPIService(HttpSession session){
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		return (ts==null)? null: ts.getViewapi();
	}
	public static void setViewAPIService(HttpSession session, ViewAPIService viewapi){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		if (ts!=null){
			ts.setViewapi(viewapi);
		}
		
	}
	
	
	
	public static LDAP getLDAP(HttpSession session){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		return (ts==null)? null:ts.getLdap();
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
	
	public static void setLDAP(HttpSession session, LDAP ldap){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		if (ts!=null){
			ts.setLdap(ldap);
		}
	}
	
	
	public static void setDB(HttpSession session, EventDBUtil eventDB){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		if (ts!=null){
			ts.setDb(eventDB);
		}
	}
	
	public static EventDBUtil getDB(HttpSession session){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		return (ts==null)? null:ts.getDb();
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
