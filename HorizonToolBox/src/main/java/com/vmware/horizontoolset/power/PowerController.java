package com.vmware.horizontoolset.power;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.EmailUtil;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class PowerController {
	private static final String view = "power";
	private static Logger log = Logger.getLogger(PowerController.class);
    
    @RequestMapping(value="/power", method=RequestMethod.GET)
    public String get(Model model, HttpSession session) {
    	log.warn("Receive power control request");
    	//model.addAttribute("translatedJsonURL", SessionUtil.getTranslatedJsonURL(session));
        model.addAttribute("view", view);
        //model.addAttribute("user", SessionUtil.getuser(session));
        
    	return Application.MAINPAGE;

    }
}
