package com.vmware.horizontoolset;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class ClientController {

	private static Logger log = Logger.getLogger(ClientController.class);
	private static final String view = "client";
	private static final String ceipEnabledKey = "ceipEnabled";
	
	public ClientController(){
		log.debug("ClientController is created");
	}
	
    @RequestMapping(value="/client", method=RequestMethod.GET)
    public String getclientsAuditing(Model model, HttpSession session) {
    	LDAP ldap = SessionUtil.getLDAP(session);
    	if (!ldap.isCEIPEnabled()){
    		model.addAttribute(ceipEnabledKey, false);
    	}else{
    		model.addAttribute(ceipEnabledKey, true);
    	}

        log.debug("Receive get request for clients");
        model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
    
    
    @RequestMapping(value="/refreshClient", method=RequestMethod.GET)
    public String refresh(Model model, HttpSession session) {
    	ClientRestController.cleanReport();
    	return "redirect:/client"; 
    }
    

    @RequestMapping(value="/enableCEIP", method=RequestMethod.GET)
    public String enableCEIP(Model model, HttpSession session) {
    	LDAP ldap = SessionUtil.getLDAP(session);
    	if (!ldap.isCEIPEnabled()){
    		ldap.enableCEIP();
    	}
    	return "redirect:/client";
    }
    
    
    @RequestMapping(value="/disableCEIP", method=RequestMethod.GET)
    public String disableCEIP(Model model, HttpSession session) {
    	LDAP ldap = SessionUtil.getLDAP(session);
    	if (ldap.isCEIPEnabled()){
    		ldap.disableCEIP();
    	}
    	return "redirect:/client";
    }
}
