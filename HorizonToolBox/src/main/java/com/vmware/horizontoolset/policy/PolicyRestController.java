package com.vmware.horizontoolset.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.policy.model.PoolItem;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.policy.service.AssignmentService;
import com.vmware.horizontoolset.policy.service.GPOService;
import com.vmware.horizontoolset.policy.service.PolFileService;
import com.vmware.horizontoolset.policy.service.impl.AssignmentServiceImpl;
import com.vmware.horizontoolset.policy.service.impl.GPOServiceImpl;
import com.vmware.horizontoolset.policy.service.impl.PolFileServiceImpl;
import com.vmware.horizontoolset.policy.util.GpoCache;
import com.vmware.horizontoolset.policy.util.GpoCache.GpoType;
import com.vmware.horizontoolset.policy.util.ViewPoolCache;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.viewapi.Container;
import com.vmware.horizontoolset.viewapi.LinkedClonePool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewType;
import com.vmware.horizontoolset.viewapi.impl.ViewAPIServiceImpl;

@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	private static ViewPoolCache vpCache = null;  //static， no need singleton instance
	private static GpoCache gpoCache = null;
	private static final int refershInterValSeconds = 300;
	private static final String DefaultDomainPolicy = "Default Domain Policy";
	
	private AssignmentService ldapAssignmentService;
	private PolFileService polFileService;
	
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
		gpoCache = GpoCache.getInstance();
		ldapAssignmentService = new AssignmentServiceImpl();
		polFileService = new PolFileServiceImpl();
		log.debug("Policy Rest Controller has initialized");
		debugFunc();
	}
	
	private void debugFunc(){
		log.debug("[DEBUG ] [debugFunc] profileMap:: " + SharedStorageAccess.get("profileMap"));
		log.debug("[DEBUG ] [debugFunc] subLengthTable:: " + SharedStorageAccess.get("subLengthTable"));
		log.debug("[DEBUG ] [debugFunc] ppMapping:: " + SharedStorageAccess.get("ppMapping"));
	}
	
	@RequestMapping("/policy/login/check")
	public boolean policyLogin(HttpSession session){
		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
		log.debug("[DEBUG ] sessionName:"+session.getId() + "; ");
		if(null==gpoService)
			return false;
		return true;	
	}
	
	@RequestMapping("/policy/login")
	public boolean policyLogin(@RequestParam(value="user", required=true)String user, 
			   @RequestParam(value="pass", required=true)String pass, 
			   @RequestParam(value="computerName", required=true)String computerName, 
			   @RequestParam(value="domainfullName", required=true)String domainfullName,
			   HttpSession session){
		
		ViewAPIServiceImpl service = (ViewAPIServiceImpl) SessionUtil.getViewAPIService(session);
		String currentDomain = service.get_domain();
		GPOService gpoService = new GPOServiceImpl(user,pass,computerName,currentDomain,domainfullName );
		List<Map<String, String>> psRes = gpoService.getGPO(DefaultDomainPolicy);
		log.debug("[DEBUG ] [policyLogin] " + psRes.toString());
		if(0==psRes.size()){
			log.debug("[DEBUG ] new GPOServiceImpl error");
			return false;
		}
		gpoService.checkDir();
		SessionUtil.setSessionObj(session, gpoService);
		gpoCache.updateCache(gpoService);
		
		return true;
	}
	
	@RequestMapping("/policy/profile/getnamelist")
	public JTableData returnNameList(HttpSession session){
		// ----------------------------------
		// gpoCache==null 		update cache
		// newGPO				update cache
		// delete				update cache
		// edit					update cache
		// time interval 		xxxxxxx
		// ----------------------------------
		
		if(gpoCache==null){
			log.debug("[DEBUG ] gpoCache == null !!!");
			gpoCache = GpoCache.getInstance();
		}
		Long currenttime = new Date().getTime();
    	if (currenttime-gpoCache.getUpdatedDate().getTime() > 1000*refershInterValSeconds ){
    		log.debug("[DEBUG ] refresh gpoCache; updateDate=" + gpoCache.getUpdatedDate().getTime() + "; currenttime=" + currenttime);
    		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
    		gpoCache.updateCache(gpoService);
    	}
		
		JTableData ret = new JTableData();
		List<ProfileItem> profiles = new ArrayList<ProfileItem>();
		Map<String, String> proItems = gpoCache.getNameList();
		if(null==proItems){
			ret.Records=profiles.toArray();
			ret.TotalRecordCount=profiles.size();
			return ret;
		}

		for(Map.Entry<String, String> mapEntry : proItems.entrySet()){
			String name = mapEntry.getKey();
			String desc = mapEntry.getValue();
			profiles.add(new ProfileItem(name, desc, gpoCache.getGpoType(name)));
		}
		Collections.sort(profiles);
		ret.Records=profiles.toArray();
		ret.TotalRecordCount=profiles.size();
		log.debug("[DEBUG ] ret: " + ret.toString());
		//add by wx
		if(ret.Records.length > 0) {
			log.debug("[ret] " + ret.Records[0].toString());
		}
		//log.debug("[ret] " + ret.Records[0].toString());
		return ret;
	}
	
	@RequestMapping("/policy/profile/getprofile")
	public ProfileModel getProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		// ----------------------------------
		// check GPOTYPE，if	NOT_EXIT	return null，
		//						IN_LDAP		return editPro
		//						IN_AD		return null， uneditable
		// ----------------------------------
		GpoType gType = gpoCache.getGpoType(profileName);
		switch(gType){
			case NOT_EXIT:
				log.debug("[GpoType] NOT_EXIT");
				return null;
			case IN_AD:
				log.debug("[GpoType] IN_AD");
				return null;
			case IN_LDAP:
				log.debug("[GpoType] IN_LDAP");
		}
		String editProStr = gpoCache.getProfileFromLdap(profileName);
		if(null==editProStr){
			log.error("editProStr==null !!!!!!");
		}
		log.debug("[DEBUG ] editProStr=" + editProStr);
		ProfileModel editPro = JsonUtil.jsonToJava(editProStr, ProfileModel.class);
		
		return editPro;
	}
	
	@RequestMapping("/policy/profile/checkProfileName")
	public boolean checkProfileName(@RequestParam(value="profileName", required=true)String profileName){
		return !gpoCache.profileNameExist(profileName);
	}
	
	@RequestMapping("/policy/profile/updateProfile")
	public boolean updateProfile( @RequestParam(value="profileName", required=true)String profileName, 
								  @RequestParam(value="description", required=false,defaultValue="")String description, 
								  @RequestParam(value="policiesStr", required=false)String policiesStr, 
								  HttpSession session){
		log.debug("[DEBUG ] [updateProfile] GPO_NAME=" + profileName + ",desc: " + description + " \n content: " + policiesStr);
		
		if( gpoCache.profileNameExist(profileName) ){
			return false;
		}	//TODO delete
		
		if( !gpoCache.add2NameList(profileName,description) ){	
			return false;
		}
		
		if ( !gpoCache.saveProfile2Ldap(profileName, description, policiesStr) ){
			gpoCache.deleteFromNameList(profileName);
			return false;
		}
		
		if( !polFileService.createPolFile(profileName) ){
			gpoCache.deleteFromNameList(profileName);
			return false;
		}
		
		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
		
		if( gpoService.policyNewProcess(profileName) ){	//policyProcess(profileName, description)
			
			log.debug("[DEBUG ] policyNewProcess success");	
			
			//delete pol file
			polFileService.deletePolFile(profileName);
			
			return true;
		}else{
			gpoCache.deleteFromNameList(profileName);
		}

		return false;
	}
	
	@RequestMapping("/policy/profile/editProfile")
	public synchronized boolean editProfile(@RequestParam(value="profileName", required=true)String profileName, String description, String policiesStr, HttpSession session){
		log.debug("[DEBUG ] [edit] " + profileName);
		
		if( !gpoCache.modifyNameList(profileName,description) ){
			log.debug("[DEBUG ] profileName not exist");
			return false;	// profileName not exist
		}
		
		if( !gpoCache.saveProfile2Ldap(profileName, description, policiesStr) ){
			log.debug("[DEBUG ] saveProfile2Ldap error");
			return false;
		}
		
		// edit GPO
		if( !polFileService.createPolFile(profileName) ){
			log.debug("[DEBUG ] createPolFile error");
			return false;
		}
		
		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
		
		if( !gpoService.policyEidtProcess(profileName) ){
			log.debug("[DEBUG ] editProfile error");
			gpoCache.deleteFromNameList(profileName);
			return false;
		}
		return true;
	}
	
	@RequestMapping("/policy/profile/delete")
	public synchronized boolean deleteProfile(@RequestParam(value="profileName", required=true)String profileName, HttpSession session){
		GpoType gType = gpoCache.getGpoType(profileName);
		switch(gType){
			case NOT_EXIT:
				log.debug("[DEBUG ] NOT_EXIT " + profileName);
				return false;
			case IN_AD:
				log.debug("[DEBUG ] IN_AD " + profileName);
				return false;
			default:
				log.debug("[DEBUG ] [deleteProfile] delete " + profileName);
				GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
				gpoService.removeGPO(profileName);
				gpoCache.deleteFromNameList(profileName);
				ldapAssignmentService.deleteAssignedProfiles(profileName);
		}
		return true;
	}
	
	// =======================================================================================
	//	assignment
	// =======================================================================================
	
	@RequestMapping("/policy/profile/assignprofiles")
	public synchronized boolean assignProfiles( @RequestParam(value="profileNames", required=true)String profileNames,
												@RequestParam(value="poolNames", required=true)String poolNames, 
												HttpSession session){

		String[] _profileNames = JsonUtil.jsonToJava(profileNames, String[].class);
		String[] _poolNames = JsonUtil.jsonToJava(poolNames, String[].class);
		for(int i=0; i<_profileNames.length; i++){
			for(int j=0; j<_poolNames.length; j++){
				//link
				String ouName = vpCache.getCached_ou().get(_poolNames[j]);
				String pattern = "(OU=[A-Za-z0-9_]*)(,OU=[A-Za-z0-9_]*)*";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(ouName);
				if (m.matches()) {
					log.debug("[DEBUG ] ouName:"+ouName);
					GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
					gpoService.linkGPO(_profileNames[i], ouName);	
				}else{
					log.debug("[DEBUG ] ouName==null");
					return false;
				}
			}
		}
		if( ldapAssignmentService.add2ppMap(poolNames, profileNames) ){
			return true;
		}
		return false;
	}
	
	@RequestMapping("/policy/profile/deletepoolprofile")
	public synchronized void deletePoolProfile( @RequestParam(value="poolName", required=true)String poolName, 
											    @RequestParam(value="profileName", required=true)String profileName, 
											    HttpSession session){
		ldapAssignmentService.deleteFromPPMap(poolName,profileName);
		log.debug("[DEBUG ] deleteFromPPMap Over");
		String ouName = vpCache.getCached_ou().get(poolName);
		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
		gpoService.removeLinkGPO(profileName, ouName);
	}

	@RequestMapping("/policy/profile/getpoolprofiles")
	public JTableData getPoolProfiles(@RequestParam(value="poolName", required=true)String poolName, HttpSession session){
		log.debug("[DEBUG ] poolName=" + poolName);
		return ldapAssignmentService.getPorfilesOfPool(poolName);
	}
	
	@RequestMapping("/pool/viewpools/getviewpools")	
    public synchronized JTableData getViewPools(HttpSession session) {
		
		
		updateCache(session);
		if(vpCache!=null){
			log.debug("[DEBUG ] [getViewPools()] "+vpCache.getCached_ou().toString());
		}else{
			log.debug("[DEBUG ] [vpCache!=null]");
		}
		
    	JTableData ret = new JTableData();
    	List<PoolItem> poolArray = new ArrayList<PoolItem>();
    	if( vpCache.getCached_ou().size()==0 ){ //TODO
    		log.debug("[vpCache.getCached_ou().size()==0]");
    		updateCache(session);
    	}
    	for(Map.Entry<String, String> entry : vpCache.getCached_ou().entrySet()){
    		poolArray.add( new PoolItem(entry.getKey(), entry.getValue()) );
    	}
    	log.debug("[DEBUG ] [pools] " + poolArray.size());
    	ret.Records=poolArray.toArray();
		ret.TotalRecordCount=poolArray.size();
    	return ret;
	}
	
	@RequestMapping("/policy/assignment/priority")
	public synchronized boolean changePriority( @RequestParam(value="poolName", required=true)String poolName, 
												@RequestParam(value="profilesStr", required=true)String profilesStr, 
												HttpSession session){
		GPOService gpoService = SessionUtil.getSessionObj(session, GPOService.class);
		
		ViewAPIServiceImpl service = (ViewAPIServiceImpl) SessionUtil.getViewAPIService(session);
		String currentDomain = service.get_domain();
		//currentDomain = ",dc=" + currentDomain + ",dc=com";
		currentDomain = ",dc=" + currentDomain + ",dc=fvt";
		log.debug("[DEBUG ] currentDomain: "+currentDomain);
		
		String[] profiles = JsonUtil.jsonToJava(profilesStr, String[].class);
		List<String> profileList = Arrays.asList(profiles);
		String ouName = vpCache.getCached_ou().get(poolName);
		
		if(profiles.length==0 || ouName==null){
			return false;
		}

		log.debug("[DEBUG ] poolName=" + poolName + ", ouName=" + ouName);
		log.debug("[DEBUG ] [cached_ou]" + vpCache.getCached_ou().toString());

		ouName += currentDomain;
		
		//run powershell in priority of GPO
		String order = null;
		log.debug("[priority] profileList: "+profileList.toString());
		for(int i=1; i<=profileList.size(); ++i){
			order = String.valueOf(i);
			gpoService.setLinkGPO(profileList.get(i-1), ouName, order);
		}
		ldapAssignmentService.setPorfilesOfPool(poolName, profileList);
		return true;
	}
	
	private void updateCache(HttpSession session){
		log.debug("[DEBUG ] [update cache] !!!");
		
		Long currenttime = new Date().getTime();
    	if (vpCache !=null && currenttime - vpCache.getUpdatedDate().getTime() < 1000 *refershInterValSeconds ){
    		 log.debug("[DEBUG ] No need to update vpCache");
    		 if(vpCache != null){
    			 Long timeGap = (currenttime - vpCache.getUpdatedDate().getTime());
    			 log.debug("[DEBUG ] [vpCache != null] timegap=" + timeGap.toString());
    		 }
    	}else{
    		log.debug("[DEBUG ] update vpCache");
    		/*
    		Map<String,String> cached_ou = new HashMap<String,String>();
    		List<ViewPool> pools = new ArrayList<ViewPool>();
        	try{
                log.debug("[DEBUG ] Receive get request for pools, farms");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                
                if (service!=null){
                	pools = service.getAllDesktopPools();	//bug
                	for(ViewPool pool : pools){
                		log.debug("[DEBUG ] poolName:"+pool.getName());
                	}
                	Map<String,String> ouMap = getOU(service);
                	if(null==ouMap){
                		log.debug("[ERROR ] ouMap==null !!!");
                	}
                	for(ViewPool vp : pools){
                	 	String poolName = vp.getName();
                		cached_ou.put( poolName, ouMap.get(poolName) );
                	}
                }else{
                	log.debug("[DEBUG ] [service == null]");
                }
                
        	}catch(Exception ex){
        		log.error("Exception, return empty array",ex);
        	}
        	*/
    		ViewAPIService service = SessionUtil.getViewAPIService(session);
    		Map<String,String> cached_ou = getOU(service);
    		if(cached_ou.size() != 0){
    			log.debug("[DEBUG ] [new ViewPoolCache]");
        		vpCache = new ViewPoolCache(cached_ou);
        	}else{
        		log.debug("[DEBUG ] [cached_ou.size() == 0] !!!");
        	}
    	}
	}
	
	private Map<String,String> getOU(ViewAPIService service){
		log.debug("[DEBUG ] [getOU]");
		Map<String,String> ouMap = new HashMap<String,String>();
    	List<SnapShotViewPool> ssvpList = service.getDetailedAutoPools(); 
    	if(null==ssvpList){
    		log.debug("[ERROR ] ssvpList==null !!!");
    	}else{
    		for(SnapShotViewPool ssvp : ssvpList){
    			log.debug("[DEBUG ] SnapShotViewPool.name=" + ssvp.getName());
    		}
    	}
		for(SnapShotViewPool ssvp : ssvpList){
			if( ssvp.getViewType().equals(ViewType.LinkedClone) ){
				log.debug("[DEBUG ] [LinkedClone] " + ssvp.getName());
				LinkedClonePool linkedClonePool = (LinkedClonePool)ssvp;	
				try {
					Container container = linkedClonePool.getADContainer();
					if(container != null){
						String ouName = container.getRDN();
						if(ouName.contains("OU=")){
							ouMap.put(linkedClonePool.getName(), ouName);
						}
						
					}else{
						log.debug("[DEBUG ] [container == null] ");
					}
				} catch (Exception ex) {
					log.error("[DEBUG ] ",ex);
				}
			}
			else{
				log.debug("[DEBUG ] [else] " + ssvp.getViewType().toString());
			}
		}
		return ouMap;
	}
	
}
