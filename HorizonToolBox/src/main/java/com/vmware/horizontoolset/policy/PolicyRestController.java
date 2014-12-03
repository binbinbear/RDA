package com.vmware.horizontoolset.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.policy.model.CommonCategory;
import com.vmware.horizontoolset.policy.model.PCoIPCategory;
import com.vmware.horizontoolset.policy.model.Profile;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.policy.model.USBCategory;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;


@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	private final static String indexTableName = "profileMap";
	
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
	}
	
	private Map<String, String> getNameList(){
		Map<String, String> proItems;
		String nameListStr = SharedStorageAccess.get(indexTableName);
		if(null==nameListStr){
			return null;
		}
		proItems = JsonUtil.jsonToJava(nameListStr, Map.class);
		return proItems;
	}
	
	private void setNameList(Map<String, String> nMap){
		String nMapStr = JsonUtil.javaToJson(nMap);
		SharedStorageAccess.set(indexTableName, nMapStr);
	}
	
	private boolean add2NameList(String profileName, String description){
		Map<String, String> proItems = getNameList();
		if(null == proItems){	
			proItems = new HashMap<String,String>();
		}
		if( null != proItems.get(profileName) )	
			return false;
/*		if( null == description)
			description = "";*/
		proItems.put(profileName, description);
		setNameList(proItems);
		return true;
	}
	
	private void deleteFromNameList(String profileName){
		Map<String, String> proItems = getNameList();
		if(null != proItems){
			proItems.remove(profileName);
		}
		setNameList(proItems);
		SharedStorageAccess.delete(profileName);
	}
	
	@RequestMapping("/policy/profile/getnamelist")	
	public JTableData returnNameList(){	//policies table中展示profiles
		JTableData ret = new JTableData();
		List<ProfileItem> profiles = new ArrayList<ProfileItem>();
		Map<String, String> proItems = getNameList();
		if(null==proItems){
			ret.Records=profiles.toArray();
			ret.TotalRecordCount=profiles.size();
			return ret;
		}

		for(Map.Entry<String, String> mapEntry : proItems.entrySet()){
			String name = mapEntry.getKey();
			String desc = mapEntry.getValue();
			profiles.add(new ProfileItem(name,desc));
		}
		
		ret.Records=profiles.toArray();
		ret.TotalRecordCount=profiles.size();
		return ret;
	}
	
	@RequestMapping("/policy/profile/getprofile")
	public Profile getProfile(String profileName, HttpSession session){
		Map<String, String> proItems = getNameList();
		if(null == proItems.get(profileName)){	// check List
			return null;
		}
		String editProStr = SharedStorageAccess.get(profileName);
		if(null == editProStr)
			return null;	//TODO
		Profile editPro = JsonUtil.jsonToJava(editProStr, Profile.class);
		return editPro;
	}
	
	@RequestMapping("/policy/profile/create")
	public boolean createEmptyProfile(@RequestParam(value="proname", required=true)String profileName, @RequestParam(value="description",required=false,defaultValue="")String description, HttpSession session){
		Profile newPro = new Profile(profileName);
		if (!add2NameList(profileName,description)){
			return false;
		}
		String newProStr = JsonUtil.javaToJson(newPro);
		SharedStorageAccess.set(profileName, newProStr);	//存储空profile
		return true;
	}
	
	@RequestMapping("/policy/profile/delete")
	public boolean deleteProfile(String profileName, HttpSession session){
		deleteFromNameList(profileName);
		return true;
	}
	
	@RequestMapping(value="/policy/profile/updatecommon")	//click [common->save]
	public Profile updateCommon(@RequestParam(value="profile", required=true)String profileName, CommonCategory common, HttpSession session){			
		String curProStr = SharedStorageAccess.get(profileName);
		if( null == curProStr)
			return null;
		Profile curProfile = JsonUtil.jsonToJava(curProStr, Profile.class);
		curProfile.setCommonCategory(common);
		curProStr = JsonUtil.javaToJson(curProfile);
		SharedStorageAccess.set(profileName, curProStr);
		return curProfile;	//return ?
	}
	
	@RequestMapping("/policy/profile/updatepcoip")	
	public Profile updatePCoIP(@RequestParam(value="profile", required=true)String profileName, PCoIPCategory pcoip, HttpSession session){
		String curProStr = SharedStorageAccess.get(profileName);
		if( null == curProStr)
			return null;
		Profile curProfile = JsonUtil.jsonToJava(curProStr, Profile.class);
		curProfile.setPcoipCategory(pcoip);
		curProStr = JsonUtil.javaToJson(curProfile);
		SharedStorageAccess.set(profileName, curProStr);
		return curProfile;
	}
	
	@RequestMapping("/policy/profile/updateusb")	
	public Profile updateUSB(@RequestParam(value="profile", required=true)String profileName, USBCategory usb, HttpSession session){
		String curProStr = SharedStorageAccess.get(profileName);
		if( null == curProStr)
			return null;
		Profile curProfile = JsonUtil.jsonToJava(curProStr, Profile.class);
		curProfile.setUsbCategory(usb);
		curProStr = JsonUtil.javaToJson(curProfile);
		SharedStorageAccess.set(profileName, curProStr);
		return curProfile;
	}

}
