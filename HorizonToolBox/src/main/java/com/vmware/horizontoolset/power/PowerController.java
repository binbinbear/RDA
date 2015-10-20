package com.vmware.horizontoolset.power;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;

public class PowerController {
	private static final String VIEW_ID = "power";
	private static Logger log = Logger.getLogger(PowerController.class);
	
    @RequestMapping(value="/power", method=RequestMethod.GET)
    public String get( Model model, HttpSession session) {
    	model.addAttribute("translatedJsonURL", SessionUtil.getTranslatedJsonURL(session));
        model.addAttribute("view", VIEW_ID);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
    

//    @RequestMapping("/limit/forceRefresh")
//    public String forceRefresh(HttpSession session) {
//    	PowerManager.updateAppConcurrency(session, true);
//    	return "redirect:/limit";
//    }
}
