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
	
	@RequestMapping("/pool/list")
	public List<String> getPools(HttpSession session) {
		List<String> pools = SessionUtil.getAllDesktopPools(session);
		return pools;
	}
	
}
