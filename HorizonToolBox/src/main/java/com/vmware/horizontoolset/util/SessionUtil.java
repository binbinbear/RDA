package com.vmware.horizontoolset.util;

import javax.servlet.http.HttpSession;

import com.vmware.horizontoolset.viewapi.ViewAPIService;

public class SessionUtil {
	protected static final String viewAPIKey = "viewapi";
	public static ViewAPIService getViewAPIService(HttpSession session){
		return (ViewAPIService)session.getAttribute(viewAPIKey);
	}
	protected static final String ldapKey = "ldap";
	public static LDAP getLDAP(HttpSession session){
		return (LDAP)session.getAttribute(ldapKey);
	}
	
	
	public static void setViewAPIService(HttpSession session, ViewAPIService viewapi){
		session.setAttribute(viewAPIKey, viewapi);
	}

	protected static final String userKey= "user";
	public static void setUser(HttpSession session, String username){
		session.setAttribute(userKey, username);
	}
	
	public static String getuser(HttpSession session){
		return (String)session.getAttribute(userKey);
	}
	
	public static void setLDAP(HttpSession session, LDAP ldap){
		session.setAttribute(ldapKey, ldap);
	}
	
	protected static final String dbKey = "db";
	
	public static void setDB(HttpSession session, EventDBUtil eventDB){
		session.setAttribute(dbKey, eventDB);
	}
	
	public static EventDBUtil getDB(HttpSession session){
		return (EventDBUtil)session.getAttribute(dbKey);
	}
	
	
	public static void releaseSession(HttpSession session){
		ViewAPIService service = getViewAPIService(session);
		if (service!=null){
			service.disconnect();
		}
		
		LDAP ldap = getLDAP(session);
		if (ldap!=null){
			ldap.close();
		}
		
		EventDBUtil db = getDB(session);
		if (db !=null){
			db.disConnect();
		}
		
		
	}
}
