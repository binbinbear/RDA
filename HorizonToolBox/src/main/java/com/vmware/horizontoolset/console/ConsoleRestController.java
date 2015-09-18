package com.vmware.horizontoolset.console;

import java.util.ArrayList;
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
			@RequestParam(value = "pool", required = false, defaultValue = "") String pool,
			@RequestParam(value = "key", required = false, defaultValue = "") String key) {

		 List<Machine>  allvms = null;
		if (StringUtil.isEmpty(pool)){
			allvms = SessionUtil.getAllVMs(session);
		}else{
			allvms = SessionUtil.getVMs(session, pool);
		}
		
		return filter(allvms,key);
	

	}
	 
	 private static final int MAX_VMS = 100;
	 
	 private boolean isMatched(Machine m, String key){
		 if (!StringUtil.isEmpty(m.getName()) && m.getName().toLowerCase().contains(key)){
			 return true;
		 }
		 if (!StringUtil.isEmpty(m.getDnsname()) && m.getDnsname().toLowerCase().contains(key)){
			 return true;
		 }
		 return false;
	 }
	 
	private List<Machine> filter(List<Machine> allvms,String key){
		if (StringUtil.isEmpty(key)){
			if (allvms.size()>MAX_VMS){
				return allvms.subList(0, MAX_VMS);
			}else{
				return allvms;
			}
			
		}
		key = key.toLowerCase();
		List<Machine> filtered = new ArrayList<Machine>();
		int count = 0;
		for (Machine m: allvms){
			if (isMatched(m,key)){
				filtered.add(m);
				count++;
				if (count>= MAX_VMS){
					break;
				}
			}
		}
		
		return filtered;
	}
	 
}
