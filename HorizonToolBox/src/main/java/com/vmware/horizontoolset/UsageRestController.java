package com.vmware.horizontoolset;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.vmware.horizontoolset.util.ExportFileUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;


@RestController
public class UsageRestController {
	 private static final String defaultDays = "30";
		private static Logger log = Logger.getLogger(UsageRestController.class);
		
		static List<Event> getEvents(HttpSession session, String userName, String days, String poolName){
			try{
				 EventDBUtil db = SessionUtil.getDB(session);
				    if (db!=null){
				    	int daysToShow = Integer.parseInt(days);
				    	if (daysToShow<=0){
				    		daysToShow = Integer.parseInt(defaultDays);
				    	}
				    	return  db.getEvents(userName, daysToShow,poolName);
				    }
			}catch(Exception ex){
				log.error("Can't get events from DB",ex);
			}

			 return null;
			    
		}
		
	 @RequestMapping("/usage/connection")
	    public List<Connection> getConnections(HttpSession session, 
	    		@RequestParam(value="user", required=false, defaultValue="") String userName,
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 ViewAPIService service = SessionUtil.getViewAPIService(session);
		 log.info("Start to query connections for "+userName+new Date());
		   List<Event> events = UsageRestController.getEvents(session, userName, days,null);
		 log.debug("Get Events: "+new Date());
		   if (events!=null){
			   return ReportUtil.getConnections(events, userName);
		   }
		 log.debug("Complish Connnections: "+new Date());
			return new ArrayList<Connection>();
		}
	 
	 
	 @RequestMapping("/usage/accumulated")
	    public AccumulatedUsageReport getUsageReport(HttpSession session, 
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) {
		 AccumulatedUsageReport report=null;
		    try{
		    	log.info("Start to query accumlated usage for "+days + " days");
			 	List<Connection>  connections = this.getConnections(session, "", days);
			 	
			 	report= ReportUtil.generateUsageReport(connections);
		    }catch(Exception ex){
		    	log.error(ex);
		    }
		    return report;
		 	
		}
	 
	 @RequestMapping(value = "/usage/connection/export")  
	  public void getConcurrentConnectionsExport(HttpSession session, 
			  HttpServletRequest request, HttpServletResponse response,
			  @RequestParam(value="user", required=false, defaultValue="") String userName,
	    		@RequestParam(value="days", required=false, defaultValue=defaultDays) String days) throws IOException {
		 	log.debug("Start to generate  usageConnection excel for "+days + " days");
		 	List<Connection> list = this.getConnections(session, userName, days);
	       
		 	response.setContentType("text/csv");  
	        response.setHeader("Content-disposition", "attachment;filename=usageConnection.csv");  
	        Writer writer = response.getWriter();  
	        
		    ExportFileUtil.exportConnections(list, writer);
	   }  

}
