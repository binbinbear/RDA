package com.vmware.horizontoolset.util;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class ToolBoxSession implements Comparable<ToolBoxSession>{
	
	private static Logger log = Logger.getLogger(ToolBoxSession.class);
	
	private Map<Class<?>, Object> apis = new HashMap<>();
	
	private String user;
	
	private String JsonURL;
	
	private HttpSession session;
	
	private long createdTime;
	
	public ToolBoxSession(HttpSession session){
		this.session = session;
		this.createdTime = session.getCreationTime();
	}
	
	public HttpSession getSession(){
		return this.session;
	}
	
	public <T> T get(Class<? extends T> klass) {
		@SuppressWarnings("unchecked")
		T t = (T) apis.get(klass);
		if (t==null){
			for (Class<?> c : apis.keySet()) {
				if (klass.isAssignableFrom(c)) {
					return (T)apis.get(c);
				}
			}
		}
		return t;
	}
	
	public void set(Object o) {
		log.info("try to set object:"+o.getClass().getCanonicalName());
		if (apis.containsKey(o.getClass())){
			log.warn("Already contains api: " + o.getClass());
		}
		
		apis.put(o.getClass(), o);
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	
	public void release(){
		
		for (Object o : apis.values()) {
			if (o instanceof AutoCloseable) {
				try {
					((AutoCloseable)o).close();
				} catch (Throwable t) {
					log.warn("Error closing api: " + o.getClass(), t);
				}
			}
		}
		
		apis.clear();
		
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
	
	
	public void setJsonURL(String jsonURL){
		this.JsonURL = jsonURL;
	}
	
	
	public String getJsonURL(){
		return JsonURL;
	}
}
