package com.vmware.horizontoolset;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.SessionUtil;
@Controller
public class SessionController {
	
	private static final String view = "session";

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index() {
    	return "redirect:/session";
    }
    
    @RequestMapping(value="/session", method=RequestMethod.GET)
    public synchronized String getSessionsAuditing( Model model, HttpSession session) {
        model.addAttribute("translatedJsonURL", SessionUtil.getTranslatedJsonURL(session));
    	model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;

    }
    
    @RequestMapping(value="/refreshSession", method=RequestMethod.GET)
    public String refresh(Model model, HttpSession session) {
    	SessionRestController.cleanReport();
    	return "redirect:/session"; 
    }

}
