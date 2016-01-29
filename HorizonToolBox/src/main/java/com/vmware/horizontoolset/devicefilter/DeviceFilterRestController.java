package com.vmware.horizontoolset.devicefilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;

@RestController
public class DeviceFilterRestController {

	private static FilterStorage storage = new FilterStorage();
	private static Logger log = Logger.getLogger(DeviceFilterRestController.class);
	  @RequestMapping("/devicefilter/all")
	    public List<DeviceFilterPolicy> getAllPolicies(HttpSession session) {
		  List<DeviceFilterPolicy> policies = storage.policies.get();
		  log.info("Policies found:"+ policies.size());
		  
		  // XU YUE MODIFIED ON 20160129
		  /* ORIGIN CODE
		  List<String> pools = SessionUtil.getAllDesktopPools(session);
			for (String pool:pools){
				DeviceFilterPolicy emptypolicy = new DeviceFilterPolicy(pool);
				if (!policies.contains(emptypolicy)){
					policies.add(emptypolicy);
				}
			}
		  */
		  
		  List<String> desktoppools = SessionUtil.getAllDesktopPools(session);
		  List<String> applicationpools = SessionUtil.getAllAppPools(session);
		  for( String pool:desktoppools ){
			  DeviceFilterPolicy emptypolicy = new DeviceFilterPolicy( pool );
			  if( !policies.contains(emptypolicy)){
				  policies.add( emptypolicy );
			  }
		  }
		  for( String pool:applicationpools ){
			  DeviceFilterPolicy emptypolicy = new DeviceFilterPolicy( pool );
			  if( !policies.contains(emptypolicy)){
				  policies.add( emptypolicy );
			  }
		  }
			
		  // MODIFICATION END
		  return policies;
	  }

	  @RequestMapping("/devicefilter/update")
		public String updateFilterPolicy(HttpSession session,
				@RequestParam(value="policyStr", required=true) String policyStr) {

	    	try {

	    		DeviceFilterPolicy policy = JsonUtil.jsonToJava(policyStr, DeviceFilterPolicy.class);


	    		storage.addOrUpdate(policy);

	    		return "successful ";
	    	} catch (Exception e) {
	    		log.warn("Error updating access policy for  "+ policyStr,e);
	    		return "failed";
	    	}
	    }


	  @RequestMapping("/devicefilter/result")
	    public List<DeviceFilterResult> getAllResults(HttpSession session) {
		  //TODO: read from DB
		  List<DeviceFilterResult> all = new ArrayList<DeviceFilterResult>();
		  DeviceFilterResult r1 = new DeviceFilterResult();
		  r1.setIp("192.168.1.2");
		  r1.setMac("a000-a000-a000-a000");
		  r1.setPoolName("p1");
		  r1.setOs("Windows 7");
		  r1.setTime(new Date());
		  r1.setVersion("3.2");
		  all.add(r1);

		  	return all;
		}


}
