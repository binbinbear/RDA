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


@RestController
public class PowerRestController {
	private static Logger log = Logger.getLogger(PowerRestController.class);
//	private static GpoCache gpoCache = null;
//	
//	public PowerRestController() {
//		gpoCache = GpoCache.getInstance();
//	}
	
	@RequestMapping("/pool/list")
	public List<String> getPools(HttpSession session) {
		List<String> pools = SessionUtil.getAllDesktopPools(session);
		for (int i = 0; i < pools.size(); i++) {
			log.debug("power:" + pools.get(i));
		}
		return pools;
	}
	
	@RequestMapping(value = "/power/myajax")
	public String addKeys(@RequestParam(value="content", required=true)String content ) {
	    //return "hello";
//		if (gpoCache.saveProfile2Ldap("powerpolicy", "", content)) {
//			content += "success";
//		}
		return content;
	}
	
	
//	@RequestMapping(value = "/power/getpolicy")
//	public String getPowerPolicy(@RequestParam(value="filename", required=true)String filename ) {
//		return gpoCache.getProfileFromLdap(filename);
//	}
//	
}
