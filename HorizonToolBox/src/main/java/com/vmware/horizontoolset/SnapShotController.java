package com.vmware.horizontoolset;


import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;


@Controller
public class SnapShotController  {
    
	public static final String mainPage = "toolset";
	private static Logger log = Logger.getLogger(SnapShotController.class);
	public SnapShotController(){
		log.debug("Create SnapShotController");
	}
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index() {
    	return "redirect:/snapshot";
    }
    
    /**
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value="/snapshot", method=RequestMethod.GET)
    public String snapshot(Model model, HttpSession session) {
    	
        log.debug("Receive get request for snapshot report");
        ViewAPIService service = SessionUtil.getViewAPIService(session);
        
    	if (service != null){
    		log.debug("this is a logged on user, getting report");
        	model.addAttribute("view", "snapshot");
        	model.addAttribute("user", SessionUtil.getuser(session));
        	return Application.MAINPAGE;
    	}
    	
    	log.debug("this is not a logged on user, redirect to login page");
    	return "redirect:/Login";
    }
    
    /**
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value="/refreshSnapShot", method=RequestMethod.GET)
    public String refresh(Model model, HttpSession session) {
    	
        log.debug("Receive refresh request for snapshot report");
        SnapShotRestController.cleanReport();
        return "redirect:/snapshot";
    }
    
    
}