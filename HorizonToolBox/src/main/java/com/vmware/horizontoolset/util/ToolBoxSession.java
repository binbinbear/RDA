package com.vmware.horizontoolset.util;


import javax.servlet.http.HttpSession;

import com.vmware.horizontoolset.viewapi.ViewAPIService;

public class ToolBoxSession implements Comparable<ToolBoxSession>{
	private LDAP ldap;
	private ViewAPIService viewapi;
	private String user;
	private EventDBUtil db;
	private HttpSession session;
	private long createdTime;
	public ToolBoxSession(HttpSession session){
		this.session = session;
		this.createdTime = session.getCreationTime();
	}
	
	public HttpSession getSession(){
		return this.session;
	}
	
	public LDAP getLdap() {
		return ldap;
	}
	public void setLdap(LDAP ldap) {
		this.ldap = ldap;
	}
	public ViewAPIService getViewapi() {
		return viewapi;
	}
	public void setViewapi(ViewAPIService viewapi) {
		this.viewapi = viewapi;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public EventDBUtil getDb() {
		return db;
	}
	public void setDb(EventDBUtil db) {
		this.db = db;
	}
	
	
	public void release(){
		if (viewapi!=null){
			viewapi.disconnect();
			viewapi = null;
		}
		
		
		if (ldap!=null){
			ldap.close();
			ldap = null;
		}
		
		
		if (db !=null){
			db.disConnect();
			db = null;
		}
		if (session!=null ){
			session.invalidate();
			session = null;
		}
	}


	@Override
	public int compareTo(ToolBoxSession o) {
		if (o==null){
			return 1;
		}
		if(this.createdTime > o.createdTime){
			return 1;
		}else if (this.createdTime == o.createdTime){
			return 0;
		}
		return -1;
	}
}
