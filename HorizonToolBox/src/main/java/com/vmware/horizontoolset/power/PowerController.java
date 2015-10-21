package com.vmware.horizontoolset.power;


import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.horizontoolset.console.VMServiceImplVCenter;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.operator.Machine;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter.VirtualCenterInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineInfo;



@Controller
public class PowerController {
	private static Logger log = Logger.getLogger(PowerController.class);


    @RequestMapping(value="/vmAction", method=RequestMethod.POST)
    @ResponseBody
    public  String action(HttpSession session,
    		@RequestParam(value="action", required=true) String action, 
    		@RequestParam(value="vmid", required=true) String vmid){
    	log.debug("VM Action for: " + vmid + " action:" + action);
    	
    	try {
    		//check vmid first
    		Machine vm = SessionUtil.getMachine(session, vmid);
    		if (vm==null){
    			return "error-VM is invalid!";
    		}


    		action = action.toLowerCase().trim();

	    	MachineInfo minfo = SessionUtil.getViewAPIService(session).getMachineInfo(vm.getVmid());
	    	if (minfo == null){
	    		return "error-VM is invalid!";
	    	}

	    	
	    		VirtualCenterInfo vcinfo =  SessionUtil.getViewAPIService(session).getVCInfo(vm.getVcenterId());
	    		VMServiceImplVCenter vmservice = new VMServiceImplVCenter(SessionUtil.getLDAP(session).getVDIContext(),vcinfo.serverSpec.serverName,minfo.managedMachineData.getVirtualCenterData().path);
	    		
	    		

			
		
    		if (action.equals("poweron")){
    			vmservice.poweron();
    		}else if (action.equals("poweroff")){
    			vmservice.poweroff();
    		}else if (action.equals("suspend")){
    			vmservice.suspend();
    		}else if (action.equals("reset")){
    			vmservice.reset();
    		}else{
    			log.info("error-Action is invalid");
    			return "error-Action is invalid";
    		}
    	}catch(Exception ex){
    		log.warn("Exception", ex);
    		return "error-"+ex.getMessage();
    	}
		return "Success";
		
		
    	
    }
}
