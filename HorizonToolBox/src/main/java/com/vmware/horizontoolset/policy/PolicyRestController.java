package com.vmware.horizontoolset.policy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.policy.model.CommonCategory;
import com.vmware.horizontoolset.policy.model.PCoIPCategory;
import com.vmware.horizontoolset.policy.model.Profile;
import com.vmware.horizontoolset.policy.model.USBCategory;


@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
	}
	
	@RequestMapping("/policy/profiles")
	public List<Profile> getProfiles(HttpSession session){
		List<Profile> profiles = new ArrayList<Profile>();
		Profile p = new Profile();
		p.setName("123");
		CommonCategory cc = new CommonCategory();
		cc.setBlockAll(false);
		cc.setDaysToKeepLogs(10);
		p.setCommonCategory(cc);
		
		profiles.add(p);
		p.setUsbCategory(new USBCategory());
		return profiles;
	}
	
	
	
	
	@RequestMapping("/policy/profile/create")
	public Profile createEmptyProfile(@RequestParam(value="profile", required=true)String profileName, HttpSession session){
		
		return new Profile(profileName);
	}
	
	@RequestMapping("/policy/profile/delete")
	public boolean deleteProfile(@RequestParam(value="profile", required=true)String profileName, HttpSession session){
		
		return true;
	}
	
	
	@RequestMapping("/policy/profile/updatecommon")
	public Profile updateCommon(@RequestParam(value="profile", required=true)String profileName, CommonCategory common, HttpSession session){
		log.info("update common for profile:" + profileName + ", block all:" + common.isBlockAll());
		Profile p =  new Profile(profileName);
		p.setCommonCategory(common);
		return p;
	}
	
	@RequestMapping("/policy/profile/updatepcoip")
	public Profile updatePCoIP(@RequestParam(value="profile", required=true)String profileName, PCoIPCategory pcoip, HttpSession session){
		
		Profile p =  new Profile(profileName);
		p.setPcoipCategory(pcoip);
		return p;
	}
	
	
	@RequestMapping("/policy/profile/updateusb")
	public Profile updateUSB(@RequestParam(value="profile", required=true)String profileName, USBCategory usb, HttpSession session){
		
		Profile p =  new Profile(profileName);
		p.setUsbCategory(usb);
		return p;
	}
	
}
