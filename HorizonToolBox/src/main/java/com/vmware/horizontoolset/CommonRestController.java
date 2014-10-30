package com.vmware.horizontoolset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.report.ViewPoolReport;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewPool;

@RestController
public class CommonRestController {
	private static Logger log = Logger.getLogger(CommonRestController.class);
	private ViewPoolReport cachedreport = null;
	private static final int refershInterValSeconds = 300;
	/**
	 * 
	 * @param session
	 * @return all pools, including application pool and rds pool
	 */
	@RequestMapping("/common/viewpools")
    public synchronized List<ViewPool> getViewPools(HttpSession session) {
		long currenttime = new Date().getTime();
    	if (cachedreport !=null && currenttime - cachedreport.getUpdatedDate().getTime() < 1000 *refershInterValSeconds ){
    		 log.debug("No need to update report");
    		
    	}else{
    		List<ViewPool> result = new ArrayList<ViewPool>();
        	try{
                log.debug("Receive get request for clients");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                if (service!=null){
                	result = service.getAllPools();
                }
        	}catch(Exception ex){
        		log.error("Exception, return empty array",ex);
        	}
        	cachedreport = new ViewPoolReport(result);
    	}
		
    	return cachedreport.getPools();
	}
}
