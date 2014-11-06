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
import com.vmware.horizontoolset.viewapi.Farm;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewPool;

@RestController
public class CommonRestController {
	private static Logger log = Logger.getLogger(CommonRestController.class);
	private ViewPoolReport cachedreport = null;
	private static final int refershInterValSeconds = 300;
	
	private void updateCache(HttpSession session){
		long currenttime = new Date().getTime();
    	if (cachedreport !=null && currenttime - cachedreport.getUpdatedDate().getTime() < 1000 *refershInterValSeconds ){
    		 log.debug("No need to update report");
    		
    	}else{
    		List<ViewPool> pools = new ArrayList<ViewPool>();
    		List<Farm> farms = new ArrayList<Farm>();
        	try{
                log.debug("Receive get request for pools, farms");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                if (service!=null){
                	pools = service.getAllDesktopPools();
                	farms = service.getAllFarms();
                }
        	}catch(Exception ex){
        		log.error("Exception, return empty array",ex);
        	}
        	cachedreport = new ViewPoolReport(pools,farms);
    	}
		
	}
	/**
	 * 
	 * @param session
	 * @return all pools desktop pools, including rds pool, not including application pools
	 */
	@RequestMapping("/common/desktoppools")
    public synchronized List<ViewPool> getDesktopPools(HttpSession session) {
		updateCache(session);
    	return cachedreport.getPools();
	}
	
	/**
	 * 
	 * @param session
	 * @return a list of String contains pool names and farm names
	 */
	@RequestMapping("/common/poolfarms")
    public synchronized List<String> getPoolFarms(HttpSession session) {
		updateCache(session);
		List<String> poolfarms = new ArrayList<String>();
		List<ViewPool> pools = cachedreport.getPools();
		for (ViewPool pool: pools){
			poolfarms.add(pool.getName());
		}
		List<Farm> farms = cachedreport.getFarms();
		for (Farm farm: farms){
			poolfarms.add(farm.getName());
		}
    	return poolfarms;
	}
}
