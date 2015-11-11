package com.vmware.horizontoolset.power;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class PowerController {
	private static final String view = "power";

    @RequestMapping(value="/power", method=RequestMethod.GET)
    public String get(Model model, HttpSession session) {
        model.addAttribute("view", view); 
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
}
