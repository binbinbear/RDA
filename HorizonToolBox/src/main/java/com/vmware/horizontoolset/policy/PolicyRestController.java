package com.vmware.horizontoolset.policy;

import java.util.ArrayList;
import java.util.Date;
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
import com.vmware.horizontoolset.policy.model.PoolItem;
import com.vmware.horizontoolset.policy.model.Profile;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.policy.model.USBCategory;
import com.vmware.horizontoolset.report.ReportUtil;
import com.vmware.horizontoolset.report.SessionReport;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewPool;

@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	private final static String indexTableName = "profileMap";			// Map<String,String>
	private final static String poolProfileMappingName = "ppMapping";	// Map< String, List<String> >
	
	private static SessionReport cachedreport = null;
	private static long timestamp;
	private static final int refershInterValSeconds = 300;
	
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
		proItems.put(profileName, description);
		setNameList(proItems);
		return true;
	}
	
	private void deleteFromNameList(String profileName){
		Map<String, String> proItems = getNameList();
		if(null != proItems){
			proItems.remove(profileName);
			setNameList(proItems);
			SharedStorageAccess.delete(profileName);
		}
	}
	
	/*
	 * 	Pool-Profile Map
	 * */
	private Map<String, List<String>> getPPMap(){
		Map<String, List<String>> ppMap;
		String ppMapStr = SharedStorageAccess.get(poolProfileMappingName);
		if( null==ppMapStr ){
			ppMap = new HashMap<String,List<String>>();
			setPPMap(ppMap);
			return ppMap;	// return new Map<String,List<String>>();
		}
		ppMap = JsonUtil.jsonToJava(ppMapStr, Map.class);
		return ppMap;
	}
	
	private void setPPMap(Map<String,List<String>> ppMap){
		String ppMapStr = JsonUtil.javaToJson(ppMap);
		SharedStorageAccess.set(poolProfileMappingName, ppMapStr);
	}
	
	private boolean add2ppMap(String poolNames, String profileNames){
		String poolArray[] = JsonUtil.jsonToJava(poolNames, String[].class);
		String profileArray[] = JsonUtil.jsonToJava(profileNames, String[].class);
		int poolArrayLen = poolArray.length - 1;
		int profileArrayLen = profileArray.length - 1;
		if( poolArrayLen<=0 || profileArrayLen<=0 ){	
			return false;
		}
		
		HashMap<String, List<String>> ppMap = (HashMap<String, List<String>>) getPPMap();

		for(int i=0; i<poolArrayLen; i++){
			ArrayList<String> profileItems = (ArrayList<String>) ppMap.get(poolArray[i]);
			if( null==profileItems )
				profileItems = new ArrayList<String>();
			
			for(int j=0; j<profileArrayLen; j++){
				if(profileItems.contains( profileArray[j]) ){
					continue;
				}else{
					profileItems.add( profileArray[j] );
				}
			}
			ppMap.put(poolArray[i], profileItems);
		}
		setPPMap(ppMap);
		return true;
	}
	
	private void deleteFromPPMap(String poolName, String profileName){
		HashMap<String, List<String>> ppMap = (HashMap<String, List<String>>) getPPMap();
		ArrayList<String> profilesOfPool = (ArrayList<String>) ppMap.get(poolName);
		if( null==profilesOfPool ){
			return;
		}
		profilesOfPool.remove(profileName);
		ppMap.put(poolName, profilesOfPool);
		setPPMap(ppMap);
	}
	
	private JTableData getPorfilesOfPool(String poolName){
		JTableData ret = new JTableData();
		ArrayList<ProfileItem> profileItems = new ArrayList<ProfileItem>();
		HashMap<String, List<String>> ppMap = (HashMap<String, List<String>>) getPPMap();
		ArrayList<String> profileArray = (ArrayList<String>) ppMap.get(poolName);
		if( null==profileArray ){
			return ret;
		}
		for(String proName : profileArray){
			profileItems.add(new ProfileItem(proName));
		}
		
		ret.Records = profileItems.toArray();
		ret.TotalRecordCount = profileItems.size();
		return ret;
	}
	
	@RequestMapping("/policy/profile/getnamelist")	
	public JTableData returnNameList(){	//policies table中列出profiles
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
	public Profile getProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
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
	public boolean deleteProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		deleteFromNameList(profileName);
		//TODO 删除 Pool-Profile MAP中的项?
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
	
	@RequestMapping("/policy/profile/assignprofiles")
	public boolean assignProfiles(String poolNames, String profileNames, HttpSession session){
		return add2ppMap(poolNames, profileNames);
	}
	
	@RequestMapping("/policy/profile/deletepoolprofile")
	public void deletePoolProfile(@RequestParam(value="poolName", required=true)String poolName, @RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		deleteFromPPMap(poolName,profileName);
	}

	@RequestMapping("/policy/profile/getpoolprofiles")
	public JTableData getPoolProfiles(@RequestParam(value="poolName", required=true)String poolName, HttpSession session){
		return getPorfilesOfPool(poolName);
	}
	
	@RequestMapping("/pool/viewpools/getviewpools")
    public synchronized JTableData getViewPools(HttpSession session) {
		long currenttime = new Date().getTime();
    	if (cachedreport !=null && currenttime - timestamp < 1000 *refershInterValSeconds ){
    		 log.debug("Receive get request for clients, and reuse previous report");
    	}else{
    		timestamp = currenttime;
        	try{
                log.debug("Receive get request for clients");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                int allCount = service.getSessionCount();
                if (allCount<500){
                	List<Session> sessions = service.getAllSessions();
                	cachedreport = ReportUtil.generateSessionReport(sessions);
                }else{
                    List<SessionPool> pools = service.getSessionPools();
                    List<SessionFarm> farms = service.getSessionFarms();
                    cachedreport = ReportUtil.generateSessionReport(pools, farms);
                }
        	}catch(Exception ex){
        		log.error("Exception, return to login",ex);
        	}
    	}

    	JTableData ret = new JTableData();
    	List<PoolItem> poolArray = new ArrayList<PoolItem>();
    	for(ViewPool viewPoolItem : cachedreport.getPools()){
    		poolArray.add(new PoolItem(viewPoolItem.getName()));
    	}
    	ret.Records=poolArray.toArray();
		ret.TotalRecordCount=poolArray.size();
    	return ret;
	}
}
