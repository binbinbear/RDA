package com.vmware.horizontoolset;


import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.db.EventDBCache;
import com.vmware.horizon.auditing.report.ReportUtil;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;


public class Application {
	private static Logger log = Logger.getLogger(Application.class);

    
    public static final String MAINPAGE = "toolset";
    
    public Application(){
    	log.info("Horizon Tool Set Application is being loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    public void setViewServerPath(String viewServerPath){
    	String sysDriver = System.getProperty("user.home");
		sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		LDAP.setViewServerPath(sysDriver + viewServerPath.substring(viewServerPath.indexOf(":")-1));
    	//LDAP.setViewServerPath(viewServerPath);
    }
    
	public void setEventCachedDays(int days){
		EventDBCache.setCachedDays(days);
	}
	
	public void setEventPagingSize(int size){
		EventDBCache.setPagingSize(size);
	}
	
    
	public void init(){
		Thread t = new Thread(new Runnable(){
		    public void run(){
				log.info("UPdating event db cache!");
				EventDBCache.updateCache();
				log.info("Updated event db cache!");
			}
		});
		t.start();
	}
	
	public void setMaximumSessions(String maximumSessions){
		int maxSession = Integer.parseInt(maximumSessions);
		log.info("maximumSessions is set to be:"+maxSession);
		SessionUtil.setMaximumSessions(maxSession);
	}
	
	
	public void setConnTimeout(int connTimeout){
		log.info("connectionTimeout is set to be:"+connTimeout);
		ReportUtil.setConnectionTimeout(connTimeout);
	}
	
	
}
