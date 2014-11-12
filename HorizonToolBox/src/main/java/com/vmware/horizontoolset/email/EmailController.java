package com.vmware.horizontoolset.email;

import javax.servlet.http.HttpSession;

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

    
    @RequestMapping(value="/email", method=RequestMethod.GET)
    public synchronized String getEmailHome( Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("server", EmailUtil.getEmailServerProps());
        model.addAttribute("content", EmailUtil.getEmailContentProps());
        model.addAttribute("user", SessionUtil.getuser(session));
        
    	return Application.MAINPAGE;

    }
    
    @RequestMapping(value="/email/server", method=RequestMethod.GET)
    public synchronized String getEmailServer(EmailServerProps props, Model model, HttpSession session) {
    	 EmailUtil.init(props);
    	 return "redirect:/email";
    }
    
    
    @RequestMapping(value="/email/content", method=RequestMethod.GET)
    public synchronized String getEmailContent(EmailContentProps props, Model model, HttpSession session) {
    	 EmailUtil.init(props);
         return "redirect:/email";

    }
    
}
