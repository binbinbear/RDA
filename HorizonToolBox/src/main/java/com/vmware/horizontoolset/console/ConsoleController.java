package com.vmware.horizontoolset.console;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.SessionUtil;
@Controller
public class ConsoleController {
	private static final String view = "console";
	@RequestMapping(value = "/console", method = RequestMethod.GET)
	public String getVMList(Model model, HttpSession session) {

		model.addAttribute("pools", SessionUtil.getAllDesktopPools(session));
		model.addAttribute("view", view);
		model.addAttribute("user", SessionUtil.getuser(session));
		

		return "toolset";

	}
}
