package com.vmware.horizontoolset.devicefilter;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.SessionUtil;
@Controller
public class DeviceFilterController {
	private static final String view = "devicefilter";

    @RequestMapping(value="/devicefilter", method=RequestMethod.GET)
    public String get(Model model, HttpSession session) {
        model.addAttribute("view", view);
        model.addAttribute("user", SessionUtil.getuser(session));
        model.addAttribute("translatedJsonURL", SessionUtil.getTranslatedJsonURL(session));

    	return Application.MAINPAGE;
    }
}
