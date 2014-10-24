package com.vmware.horizontoolset.ra;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;

@Controller
public class RemoteAssistController {
	private static final String VIEW_ID = "remoteassist";

    @RequestMapping(value="/remoteassist", method=RequestMethod.GET)
    public String get( Model model, HttpSession session) {
        model.addAttribute("view", VIEW_ID);
        model.addAttribute("user", SessionUtil.getuser(session));
    	return Application.MAINPAGE;
    }
}
