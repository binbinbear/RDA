package com.vmware.horizontoolset.console;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.StringUtil;
import com.vmware.horizontoolset.viewapi.operator.Machine;


@RestController
public class ConsoleRestController {
	 @RequestMapping( "/console/list")
	public List<Machine> getvms(HttpSession session,
			@RequestParam(value = "pool", required = false, defaultValue = "") String pool) {

		if (StringUtil.isEmpty(pool)){
			return SessionUtil.getAllVMs(session);
		}
		
		return SessionUtil.getVMs(session, pool);
	

	}
	 
	 
}
