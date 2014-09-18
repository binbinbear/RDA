package com.vmware.horizontoolset;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class UserController {
	private static final String view = "user";
	 @RequestMapping(value="/user", method=RequestMethod.GET)
	    public synchronized String getEvents( Model model, HttpSession session) {
		    
	        model.addAttribute("view", view);
	        model.addAttribute("user", SessionUtil.getuser(session));
	    	return Application.MAINPAGE;

	    }

	
}
