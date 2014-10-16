package com.vmware.horizontoolset;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.report.ReportUtil;
import com.vmware.horizontoolset.report.SnapShotReport;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;

@RestController
public class SnapShotRestController {
	private static Logger log = Logger.getLogger(SnapShotRestController.class);
	private static SnapShotReport lastReport = null;
	public SnapShotRestController(){
		log.debug("Create SnapShotRestController");
	}
	
	@RequestMapping("/snapshot/report")
	 public SnapShotReport getReport(HttpSession session){
		if (lastReport ==null){
			refreshLastReport(session);
		}
		return lastReport;
	  }
	
	 static void cleanReport(){
			lastReport = null;
	    }

	 private synchronized void refreshLastReport(HttpSession session){
		 try{
			 if (lastReport == null){
				 ViewAPIService service = SessionUtil.getViewAPIService(session);
					log.info("Start to get all pools");
		     	List<SnapShotViewPool> list = service.getDetailedAutoPools();
		     	lastReport= ReportUtil.generateSnapShotReport(list);
			 }
		 }catch(Throwable ex){
			 log.error("Can't refresh snapshot report", ex);
		 }

	 }
}
