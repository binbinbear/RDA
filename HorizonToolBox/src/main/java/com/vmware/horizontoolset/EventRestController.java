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
import com.vmware.horizontoolset.util.EventDBUtil;
import com.vmware.horizontoolset.util.SessionUtil;


@RestController
public class EventRestController {
	 private static final String defaultDays = "30";
		private static Logger log = Logger.getLogger(EventRestController.class);
	 @RequestMapping("/userevent")
	    public List<Connection> getConnections(HttpSession session, 
	    		@RequestParam(value="user", required=false, defaultValue="") String userName,
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 log.debug("Start to query connections for "+userName);
		    EventDBUtil db = SessionUtil.getDB(session);
		    if (db!=null){
		    	return db.getConnections(userName, days);
		    }
			return new ArrayList<Connection>();
		}
	 
	 
	 @RequestMapping("/usagereport")
	    public AccumulatedUsageReport getUsageReport(HttpSession session, 
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 	log.debug("Start to query accumlated usage for "+days + " days");
		 	List<Connection>  connections = this.getConnections(session, "", days);
		 	
		 	return ReportUtil.generateUsageReport(connections);
		}
}
