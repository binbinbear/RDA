package com.vmware.horizontoolset;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.report.ClientReport;
import com.vmware.horizontoolset.report.ReportUtilExtension;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizon.auditing.EventsAuditing;
import com.vmware.horizon.auditing.report.BrokerSession;

@RestController
public class ClientRestController {
	public ClientRestController(){
		log.debug("ClientRestController is created");
	}
	private static Logger log = Logger.getLogger(ClientRestController.class);
	
	private static ClientReport cachedreport =null;
	
	
	public static final int refershInterValSeconds = 300;
	
	
	public static final int MAX_FILE_SIZE = 1024*1024*64;
	
	private static final String defaultDays = "30";
	
	
	static void cleanReport(){
		cachedreport = null;
	}
	
	private static String previousFolder = "";
	

	public synchronized static void updateCachedReport(){
    	log.debug("Start to update cached report");
		if (previousFolder == null || previousFolder.length()==0){
			return;
		}
		long currenttime = new Date().getTime();
    	if (cachedreport !=null && currenttime - cachedreport.getUpdatedDate().getTime() < 1000 *refershInterValSeconds ){
    		 log.debug("No need to update report");
    	}else{
    		//get the folder
    		File spoolFolder = new File(previousFolder);
    		log.debug("spool path:"+spoolFolder.getAbsolutePath());
    		if (spoolFolder.isDirectory()){
    			cachedreport = ReportUtilExtension.generateClientReport(previousFolder);
    		}else{
    			log.debug("Spool folder is not found");
    			if (cachedreport == null){
    				cachedreport = new ClientReport();
    			}
    		}
    		
    	}
		
	}
	
	@RequestMapping("/client/report")
	public ClientReport getclientreport(HttpSession session){
		LDAP ldap = SessionUtil.getLDAP(session);
		if (ldap==null || !ldap.isCEIPEnabled()){
			return cachedreport;
		}
		
		previousFolder = ldap.getCEIPFolder()+File.separator+"spool";
		updateCachedReport();
		return cachedreport;
	}
	
	@RequestMapping("/client/brokersessionlist")
	public List<BrokerSession> getBrokerSessionList(HttpSession session, 
    		@RequestParam(value="user", required=false, defaultValue="") String userName,
    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		ViewAPIService service = SessionUtil.getViewAPIService(session);
		
		log.info("Start to query broker session for "+userName+new Date());
		 
		 int daysToShow = Integer.parseInt(days);
	    	if (daysToShow<=0){
	    		daysToShow = Integer.parseInt(defaultDays);
	    	}
		log.debug("Get Broker Events: "+new Date());
		EventsAuditing auditing = SessionUtil.getSessionObj(session, EventsAuditing.class);
		List<BrokerSession> result = auditing.getBrokerSessions(userName, daysToShow);
		log.info("List broker sessions done. Size = " + result.size());
		
		return result;
	}
}

