package com.vmware.horizontoolset;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class LogoutController {
    
    @RequestMapping(value="/Logout", method=RequestMethod.GET)
    public String logout(Credential credential, BindingResult bindingResult, Model model, HttpSession session) {
    	if(session != null){
    		SessionUtil.releaseSession(session);
    	}
    	return "redirect:/Login";
    }


}
