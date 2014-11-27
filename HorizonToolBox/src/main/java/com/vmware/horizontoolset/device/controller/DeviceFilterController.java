package com.vmware.horizontoolset.device.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.device.data.AccessLogManager;
import com.vmware.horizontoolset.device.data.AccessRecord;
import com.vmware.horizontoolset.device.data.WhitelistManager;
import com.vmware.horizontoolset.device.data.WhitelistRecord;
import com.vmware.horizontoolset.util.SessionMsg;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class DeviceFilterController {
	private static final String VIEW_ID = "deviceFilter";

    @RequestMapping(value="/deviceFilter", method=RequestMethod.GET)
    public String get( Model model, HttpSession session) {
        model.addAttribute("view", VIEW_ID);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
    
    @RequestMapping("/deviceFilter/addToWhitelist")
	public String addToWhitelist(HttpSession session,
			@RequestParam(value="recordId", required=true) String recordIdString) {
    	
    	try {
    		long recordId = Long.parseLong(recordIdString);
    		AccessRecord rec = AccessLogManager.get(recordId);
    		
    		if (rec == null)
    			SessionMsg.addSevere(session, "No such record");
    		
    		else {
    			rec.result = AccessRecord.AccessResult.ADDED_TO_WHITELIST;
    			
    			WhitelistManager.add(new WhitelistRecord(rec.deviceInfo));
    			SessionMsg.addSuccess(session, "No such record");
    		}
    		
    	} catch (Exception e) {
    		SessionMsg.addSevere(session, "Exception: " + e);
    	}

    	return "redirect:/deviceFilter";
    }
}

