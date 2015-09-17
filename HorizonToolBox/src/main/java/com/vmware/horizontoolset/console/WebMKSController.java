package com.vmware.horizontoolset.console;


import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.horizontoolset.util.LRUCache;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.operator.Machine;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter.VirtualCenterInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineInfo;

/**
 * scope=session
 * 
 *
 */

@Controller
public class WebMKSController {
	
	private static final String view = "webmks";
	private static Logger log = Logger.getLogger(WebMKSController.class);

	//TODO: setup this value 1000 in xml
	private static LRUCache<String, ConsoleAccessInfo> _infoCache = new LRUCache<String, ConsoleAccessInfo>(1000);
	

	public static ConsoleAccessInfo getAccessInfo(String uuid){
		return _infoCache.get(uuid);
	}


	//vcenter:
	//https://10.117.160.99:7343/console/?vmId=vm-124&vmName=stengdomain&host=nanw-04.eng.vmware.com:443&sessionTicket=cst-VCT-52ad1c7d-eb12-6626-ab0b-427290cbf504--tp-58-C2-CC-26-03-F3-E9-C4-50-A3-88-FF-AB-C2-DF-47-73-D8-3B-94&thumbprint=58:C2:CC:26:03:F3:E9:C4:50:A3:88:FF:AB:C2:DF:47:73:D8:3B:94
    @RequestMapping(value="/webmks", method=RequestMethod.GET)
    public synchronized String getWebMKS(Model model, HttpSession session,
    		@RequestParam(value="vmid", required=true ) String vmid 
    		) {

    	log.debug("Requesting console for: " + vmid);
    	Machine m = SessionUtil.getMachine(session, vmid);
    	if (m==null){
    		//open an empty window
    		return view;
    	}
    	MachineInfo minfo = SessionUtil.getViewAPIService(session).getMachineInfo(m.getVmid());
    	if (minfo == null){
    		return view;
    	}
    	try {
    		VirtualCenterInfo vcinfo =  SessionUtil.getViewAPIService(session).getVCInfo(m.getVcenterId());
    		VMServiceImplVCenter vmservice = new VMServiceImplVCenter(SessionUtil.getLDAP(session).getVDIContext(),vcinfo.serverSpec.serverName,minfo.managedMachineData.getVirtualCenterData().path);
        	
    		ConsoleAccessInfo info = vmservice.requestConsoleAccessInfo();
    			
    		String key = UUID.randomUUID().toString();
    		_infoCache.put(key, info);
    		model.addAttribute("vmurl", "/toolbox/wsproxy?uuid="+key);
    			model.addAttribute("vmname", m.getDnsname());
    			

    			log.debug("Start connection for vmurl:"+info.getUri());
    		
    		

    	} catch (Exception e) {
    		log.warn("Fail opening console", e);
    		model.addAttribute("vmname", "Internal Error");
		} 
        model.addAttribute("user", SessionUtil.getuser(session));
       
        
    	return view;
    }


	
	
	private static final String SUCCESS ="successful";
	
	//TODO: check vmid
	@RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
	@ResponseBody
	public String heartbeat(HttpSession session,
    		@RequestParam(value="vmid", required=true) String vmid) {
		return SUCCESS;
	}
}
