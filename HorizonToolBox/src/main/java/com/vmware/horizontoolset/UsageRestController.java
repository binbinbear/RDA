package com.vmware.horizontoolset;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.report.AccumulatedUsageReport;
import com.vmware.horizontoolset.report.ReportUtil;
import com.vmware.horizontoolset.usage.Connection;
import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.util.EventDBUtil;
import com.vmware.horizontoolset.util.SessionUtil;


@RestController
public class UsageRestController {
	 private static final String defaultDays = "30";
		private static Logger log = Logger.getLogger(UsageRestController.class);
		
		static List<Event> getEvents(HttpSession session, String userName, String days, String poolName){
			 EventDBUtil db = SessionUtil.getDB(session);
			    if (db!=null){
			    	int daysToShow = Integer.parseInt(days);
			    	if (daysToShow<=0){
			    		daysToShow = Integer.parseInt(defaultDays);
			    	}
			    	return  db.getEvents(userName, daysToShow,poolName);
			    }
			 return null;
			    
		}
		
	 @RequestMapping("/usage/connection")
	    public List<Connection> getConnections(HttpSession session, 
	    		@RequestParam(value="user", required=false, defaultValue="") String userName,
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 log.debug("Start to query connections for "+userName);
		   List<Event> events = UsageRestController.getEvents(session, userName, days,null);
		   if (events!=null){
			   return ReportUtil.getConnections(events, userName);
		   }
			return new ArrayList<Connection>();
		}
	 
	 
	 @RequestMapping("/usage/accumulated")
	    public AccumulatedUsageReport getUsageReport(HttpSession session, 
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 	log.debug("Start to query accumlated usage for "+days + " days");
		 	List<Connection>  connections = this.getConnections(session, "", days);
		 	
		 	return ReportUtil.generateUsageReport(connections);
		}
	 

}
