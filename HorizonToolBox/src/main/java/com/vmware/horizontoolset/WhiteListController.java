package com.vmware.horizontoolset;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class WhiteListController {
	private static final String view = "whitelist";
    @RequestMapping(value="/whitelist", method=RequestMethod.GET)
    public synchronized String action( Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;

    }
    
}
