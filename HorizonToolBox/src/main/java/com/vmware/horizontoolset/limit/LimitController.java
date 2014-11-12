package com.vmware.horizontoolset.limit;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class LimitController {
	private static final String VIEW_ID = "limit";

    @RequestMapping(value="/limit", method=RequestMethod.GET)
    public String get( Model model, HttpSession session) {
        model.addAttribute("view", VIEW_ID);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
    

    @RequestMapping("/limit/forceRefresh")
    public String forceRefresh(HttpSession session) {
    	LimitManager.updateAppConcurrency(session);
    	return "redirect:/limit";
    }
}

