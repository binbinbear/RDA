package com.vmware.horizontoolset.devicefilter;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;
@Controller
public class DeviceFilterController {
	private static final String view = "devicefilter";

	private DeviceFilterManager devicemanager = new DeviceFilterManagerLDAP();
    @RequestMapping(value="/devicefilter", method=RequestMethod.GET)
    public String get(Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
        model.addAttribute("translatedJsonURL", SessionUtil.getTranslatedJsonURL(session));
        model.addAttribute("filterEnabled", devicemanager.isEnabled());
    	return Application.MAINPAGE;
    }


    private static Logger log = Logger.getLogger(DeviceFilterController.class);
    @RequestMapping(value="/enablefilter", method=RequestMethod.GET)
    public String enableFilter(Model model, HttpSession session) {

    	if (!devicemanager.isEnabled()){
    		log.info("Starting Filter...");
    		devicemanager.enable();
    		log.info("Filter has been started...");
    	}

    	return "redirect:/Logout";
    }


    @RequestMapping(value="/disablefilter", method=RequestMethod.GET)
    public String disableFilter(Model model, HttpSession session) {

    	if (devicemanager.isEnabled()){
    		log.info("Stopping Filter...");
    		devicemanager.disable();
    		log.info("Filter has been stopped...");
    	}
    	return "redirect:/Logout";
    }



}
