package com.vmware.horizontoolset.power;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.policy.util.GpoCache;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;


@RestController
public class PowerRestController {
	private static Logger log = Logger.getLogger(PowerRestController.class);
	private static List<PowerOnJob> poJobs = new ArrayList<PowerOnJob>();
	
	@RequestMapping("/pool/list")
	public List<String> getPools(HttpSession session) {
		List<String> pools = SessionUtil.getAllDesktopPools(session);
		poJobs.clear();
		for (int i = 0; i < pools.size(); i++) {
			poJobs.add(new PowerOnJob(pools.get(i), null));			
		}
		return pools;
	}
	
	@RequestMapping(value = "/power/myajax")
	public String postPolicy(@RequestParam(value="content", required=true)String content ) {
	    //return "hello";
		//SharedStorageAccess.set("powerpolicy", content);
		content = content.substring(1, content.length()-1);
		String[] policys = content.split(",");
		for (int i = 0; i < policys.length; i++) {
			String[] tmp = policys[i].split(":");
			for (int j = 0; j < poJobs.size(); j++) {
				if (poJobs.get(j).getPoolName().compareTo(tmp[0].substring(1, tmp[0].length()-1)) == 0) {
					poJobs.get(j).setCron(tmp[1].substring(1, tmp[1].length()-1));
					break;
				}
			}
		}
		
		for (int i = 0; i < poJobs.size(); i++) {
			log.debug("poJobs: poolNam : " + poJobs.get(i).getPoolName() + ", cron : " + poJobs.get(i).getCron());
		}
		return content;
	}
	
	
//	@RequestMapping(value = "/power/getpolicy")
//	public String getPowerPolicy(@RequestParam(value="filename", required=true)String filename ) {
//		String content = SharedStorageAccess.get(filename);
//		return content;
//	}
	
	@RequestMapping(value = "/power/getpolicy")
	public String getPowerPolicy() {
		String content = SharedStorageAccess.get("powerpolicy");
		return content;
	}
	
}
