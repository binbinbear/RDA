package com.vmware.horizontoolset.email;

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
public class EmailController {
	private static final String view = "email";
	private static Logger log = Logger.getLogger(EmailUtil.class);
	
    
    @RequestMapping(value="/email", method=RequestMethod.GET)
    public synchronized String getEmailHome( Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("server", EmailUtil.loadServerProps());
        model.addAttribute("user", SessionUtil.getuser(session));
        
    	return Application.MAINPAGE;

    }
    
    @RequestMapping(value="/email/server", method=RequestMethod.GET)
    public synchronized String setEmailServer(EmailServerProps props, Model model, HttpSession session) {
    	 EmailUtil.init(props);
    	 try{
    		 EmailUtil.sendMail("mail config is changed", "mailconfig is changed");
    	 }catch(Exception ex){
    		 log.warn("Eexception when sending email",ex);
    	 }
    	 
    	 return "redirect:/email";
    }
    
    
}
