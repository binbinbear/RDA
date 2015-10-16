package com.vmware.horizontoolset;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizon.auditing.EventsAuditing;
import com.vmware.horizon.auditing.report.ConcurrentConnectionsReport;
import com.vmware.horizontoolset.report.ReportUtilExtension;
import com.vmware.horizontoolset.report.SessionReport;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;

@RestController
public class SessionRestController {
	private static Logger log = Logger.getLogger(SessionRestController.class);
	
	public static final int refershInterValSeconds = 300;
	private static SessionReport cachedreport =null;
	private static long timestamp;
	public SessionRestController(){
		log.debug("Create Session Rest Controller");
	}
	
	
	
	@RequestMapping("/session/report")
    public synchronized SessionReport getSessionsReport(HttpSession session) {
    	long currenttime = new Date().getTime();
    	if (cachedreport !=null && currenttime - timestamp < 1000 *refershInterValSeconds ){
    		 log.debug("Receive get request for clients, and reuse previous report");
    	}else{
    		timestamp = currenttime;
        	try{
                log.debug("Receive get request for clients");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                int allCount = service.getSessionCount();
                if (allCount<500){
                	List<Session> sessions = service.getAllSessions();
                	cachedreport = ReportUtilExtension.generateSessionReport(sessions);
                }else{
                    List<SessionPool> pools = service.getSessionPools();
                    List<SessionFarm> farms = service.getSessionFarms();
                    cachedreport = ReportUtilExtension.generateSessionReport(pools, farms);
                }

               
        	}catch(Exception ex){
        		log.error("Exception, return to login",ex);
        		return cachedreport;
        	}
    	}
    	return cachedreport;

    }
	
	static void cleanReport(){
		cachedreport = null;
	}
	
	
	 /**
	  * default period is 1 day
	  */
	 	private static final String defaultPeriod = "86400";
	 	private static final String defaultDays = "30";
	 @RequestMapping("/session/concurrent")
	    public ConcurrentConnectionsReport getConcurrentConnectionsReport(HttpSession session, 
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days,
	    		@RequestParam(value="period", required=false, defaultValue=defaultPeriod) String period,
	    		@RequestParam(value="pool",required=false,defaultValue="") String poolName) {
		 
		   
		 	log.info("Start to generate  ConcurrentConnectionsReport for "+days + " days");
		 	if(poolName.equals("")) 
		 		poolName = null;
		 	EventsAuditing auditing = SessionUtil.getSessionObj(session, EventsAuditing.class);
			
			int daysToShow = Integer.parseInt(days);
		    if (daysToShow<=0){
		    	daysToShow = Integer.parseInt(defaultDays);
		    }
		    long periodL = Long.parseLong(period);
			return auditing.getConcurrentConnectionsReport(poolName, daysToShow, periodL);

		}

}
