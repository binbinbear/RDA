package com.vmware.horizontoolset.policy;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.vmware.horizontoolset.policy.model.PoolItem;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.policy.service.GPOService;
import com.vmware.horizontoolset.policy.service.LdapAssignmentService;
import com.vmware.horizontoolset.policy.service.LdapProfileService;
import com.vmware.horizontoolset.policy.service.PolFileService;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.viewapi.Container;
import com.vmware.horizontoolset.viewapi.LinkedClonePool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewPool;
import com.vmware.horizontoolset.viewapi.ViewType;

@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	
	private static ViewPoolCache vpCache =null;
	
	private static final int refershInterValSeconds = 300;
	
	private LdapProfileService ldapProfileService;
	private LdapAssignmentService ldapAssignmentService;
	private PolFileService polFileService;
	private GPOService gpoService;
	
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
		ldapProfileService = new LdapProfileService();
		ldapAssignmentService = new LdapAssignmentService();
		polFileService = new PolFileService();
		gpoService = new GPOService();
		
	}
	
	@RequestMapping("/policy/profile/getnamelist")	//profile
	public JTableData returnNameList(){	
		JTableData ret = new JTableData();
		List<ProfileItem> profiles = new ArrayList<ProfileItem>();
		Map<String, String> proItems = ldapProfileService.getNameList();
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
	
	@RequestMapping("/policy/profile/getprofile")	//profile
	public ProfileModel getProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
  		Map<String, String> proItems = ldapProfileService.getNameList();
		if(null == proItems.get(profileName)){	// check List
			return null;
		}
		String editProStr = SharedStorageAccess.get(profileName);
		if(null == editProStr)
			return null;	//TODO
		ProfileModel editPro = JsonUtil.jsonToJava(editProStr, ProfileModel.class);
		return editPro;
	}
	
	@RequestMapping("/policy/profile/updateProfile")	//profile
	public boolean updateProfile(String profileName, String description, String policiesStr){
		ldapProfileService.saveProfile2Ldap(profileName, description, policiesStr);
		polFileService.createPolFile(profileName);
		if( gpoService.policyProcess(profileName) ){
			if( ldapProfileService.add2NameList(profileName,description) ){
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping("/policy/profile/editProfile")		//profile
	public boolean editProfile(String profileName, String description, String policiesStr){
		if(!ldapProfileService.modifyNameList(profileName,description)){
			return false;	// profileName不存在
		}
		ldapProfileService.saveProfile2Ldap(profileName, description, policiesStr);
		//TODO edit GPO
		return true;
	}
	
	@RequestMapping("/policy/profile/delete")	//profile
	public boolean deleteProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		ldapProfileService.deleteFromNameList(profileName);
		//TODO 删除 Pool-Profile MAP中的项?
		return true;
	}
	
	/*=======================================================================================*/
	
	@RequestMapping("/policy/profile/assignprofiles")	//assignment
	public boolean assignProfiles(String profileNames, String poolNames, HttpSession session){
		// 创建PolFile对象,读取ldap中profile内容，对每个policy的每项设置，去set PolFile
		String[] _profileNames = JsonUtil.jsonToJava(profileNames, String[].class);
		String[] _poolNames = JsonUtil.jsonToJava(poolNames, String[].class);
		for(int i=0; i<_profileNames.length; i++){
			for(int j=0; j<_poolNames.length; j++){
				//link
				String ouName = vpCache.getCached_ou().get(_poolNames[j]);

				if( ouName != null ){
					log.debug("[DEBUG lxy] ouName:"+ouName);
					gpoService.linkGPO(_profileNames[i], ouName);	
				}else{
					log.debug("[DEBUG lxy] ouName==null");
					return false;
				}
			}
		}
		if( ldapAssignmentService.add2ppMap(poolNames, profileNames) ){
			return true;
		}
		return false;
	}
	
	@RequestMapping("/policy/profile/deletepoolprofile")	//assignment
	public void deletePoolProfile(@RequestParam(value="poolName", required=true)String poolName, @RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		ldapAssignmentService.deleteFromPPMap(poolName,profileName);
	}

	@RequestMapping("/policy/profile/getpoolprofiles")	//assignment
	public JTableData getPoolProfiles(@RequestParam(value="poolName", required=true)String poolName, HttpSession session){
		return ldapAssignmentService.getPorfilesOfPool(poolName);
	}
	
	private void updateCache(HttpSession session){
		Long currenttime = new Date().getTime();
    	if (vpCache !=null && currenttime - vpCache.getUpdatedDate().getTime() < 1000 *refershInterValSeconds ){
    		 log.debug("No need to update report");
    		 log.debug("[DEBUG lxy] [vpCache] "+vpCache.getCached_ou().toString());
    		 if(vpCache != null){
    			 Long timeGap = (currenttime - vpCache.getUpdatedDate().getTime());
    			 log.debug("[DEBUG lxy] [vpCache != null] timegap=" + timeGap.toString());
    		 }else{
    			 log.debug("[DEBUG lxy] [vpCache == null]");
    		 }
    	}else{
    		Map<String,String> cached_ou = new HashMap<String,String>();
    		List<ViewPool> pools = new ArrayList<ViewPool>();
        	try{
                log.debug("Receive get request for pools, farms");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                if (service!=null){
                	pools = service.getAllDesktopPools();	//bug
                	
                	Map<String,String> ouMap = getOU(service);
                	for(ViewPool vp : pools){
                		String poolName = vp.getName();
                		cached_ou.put( poolName, ouMap.get(poolName) );
                	}
                }else{
                	log.debug("[service == null]");
                }
        	}catch(Exception ex){
        		log.error("Exception, return empty array",ex);
        	}
        	if(cached_ou.size()!=0){	//临时处理
        		vpCache = new ViewPoolCache(cached_ou);
        	}
        	
    	}
	}
	
	private Map<String,String> getOU(ViewAPIService service){
		Map<String,String> ouMap = new HashMap<String,String>();
		
    	List<SnapShotViewPool> ssvpList = service.getDetailedAutoPools();
		for(SnapShotViewPool ssvp : ssvpList){
			if( ssvp.getViewType().equals(ViewType.LinkedClone) ){
				LinkedClonePool linkedClonePool = (LinkedClonePool)ssvp;	
				try {
					Container container = linkedClonePool.getADContainer();
					if(container != null){
						String ouName = container.getRDN();
						ouMap.put(linkedClonePool.getName(), ouName);
					}else{
						log.info("[container == null] ");
					}
				} catch (Exception ex) {
					log.error("[DEBUG lxy] ",ex);
				}
			}
			else{
				log.info("[else] " + ssvp.getViewType().toString());
			}
		}
		return ouMap;
	}
	
	@RequestMapping("/pool/viewpools/getviewpools")		//assignment
    public synchronized JTableData getViewPools(HttpSession session) {
		
		updateCache(session);
		if(vpCache!=null){
			log.debug("[DEBUG lxy] [getViewPools()] "+vpCache.getCached_ou().toString());
		}else{
			log.debug("[DEBUG lxy] [vpCache!=null]");
		}
		
    	JTableData ret = new JTableData();
    	List<PoolItem> poolArray = new ArrayList<PoolItem>();
    	for(Map.Entry<String, String> entry : vpCache.getCached_ou().entrySet()){
    		poolArray.add( new PoolItem(entry.getKey(), entry.getValue()) );
    	}

    	ret.Records=poolArray.toArray();
		ret.TotalRecordCount=poolArray.size();
    	return ret;
	}

	
	@RequestMapping("/policy/assignment/priority")	//assignment
	public boolean changePriority(String poolName, String profilesStr){
		String[] profiles = JsonUtil.jsonToJava(profilesStr, String[].class);
		List<String> profileList = Arrays.asList(profiles);
		//按优先级调用ps
		String ouName = vpCache.getCached_ou().get(poolName);
		String order = null;
		for(int i=1; i<=profileList.size(); ++i){
			order = String.valueOf(i);
			gpoService.setLinkGPO(poolName, ouName, order);
		}
		ldapAssignmentService.setPorfilesOfPool(poolName, profileList);
		return true;
	}

}
