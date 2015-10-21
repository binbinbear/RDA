package com.vmware.horizontoolset.policy.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.policy.service.GPOService;
import com.vmware.horizontoolset.policy.service.ProfileService;
import com.vmware.horizontoolset.policy.service.impl.ProfileServiceImpl;


public class GpoCache implements ProfileService{
	public static enum GpoType{
		NOT_EXIT,
		IN_LDAP,
		IN_AD
	}
	
	private static Logger log = Logger.getLogger(GpoCache.class);
	
	private static final GpoCache gpoCacheInstance = new GpoCache();
	
	private Date updatedDate;
	private Map<String,String> cached_gpo;
	private ProfileService pServiceImpl;
	//private GPOService gpoService;
	
	private GpoCache(){
		log.debug("[DEBUG ] init GpoCache");
		updatedDate =new Date();
		cached_gpo = new HashMap<String,String>();
		pServiceImpl = new ProfileServiceImpl();	//ldap service
		log.debug("[DEBUG ] update GpoCache over");
	}

	public static GpoCache getInstance(){
		return gpoCacheInstance;
	} 
	
	public void updateCache(GPOService gpoService){
		Map<String, String> proItems = gpoService.getNameList();
		if(null==proItems){
			log.debug("[DEBUG ] proItems==null");
			return;
		}
		
		cached_gpo.clear();
		for(Map.Entry<String, String> mapEntry : proItems.entrySet()){
			String name = mapEntry.getKey();
			String desc = mapEntry.getValue();
			cached_gpo.put(name, desc);
		}

		Map<String,String> ldapList = pServiceImpl.getNameList();
		log.debug("[DEBUG ] ldapList: " + ldapList.toString());
		for(Map.Entry<String, String> ldapEntry : ldapList.entrySet()){
			String k = ldapEntry.getKey();
			String v = ldapEntry.getValue();
			if(cached_gpo.containsKey(k)){
				cached_gpo.put(k, v);
			}else{

				log.debug("[ERROR ] k="+k+", v="+v);
			}
		}
		updatedDate = new Date();
	}
	
	private boolean addGpo2Cache(String pName, String desc){
		if( cached_gpo.containsKey(pName) ){
			return false;
		}
		cached_gpo.put(pName, desc);
		return true;
	}
	
	private boolean modifyCache(String pName, String desc){
		if( !cached_gpo.containsKey(pName) ){
			return false;
		}
		cached_gpo.put(pName, desc);
		return true;
	}
	
	private boolean deleteFromCache(String pName){
		if( !cached_gpo.containsKey(pName) ){
			return false;
		}
		cached_gpo.remove(pName);
		return true;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	//@Override
	public Map<String, String> getNameList() {

		if(null==cached_gpo){
			log.debug("[DEBUG ] [getNameList()] cache_gpo==null");
			//updateCache();
		}
		
		return cached_gpo;
	}

	@Override
	public boolean profileNameExist(String profileName) {

		if(cached_gpo.containsKey(profileName)){
			return true; 
		}
		return false; 
	}
	
	public GpoType getGpoType(String profileName){
		if(profileNameExist(profileName)){

			if(pServiceImpl.profileNameExist(profileName)){
				log.debug("[DEBUG ] " + profileName + "'s type = IN_LDAP");
				return GpoType.IN_LDAP;
			}	
			else{
				log.debug("[DEBUG ] " + profileName + "'s type = IN_AD");
				return GpoType.IN_AD;
			}
		}else{
			log.debug("[DEBUG ] " + profileName + "'s type = NOT_EXIT");
			return GpoType.NOT_EXIT;
		}
	}

	@Override
	public boolean add2NameList(String profileName, String description) {
		if(!addGpo2Cache(profileName,description)){
			log.debug("[addGpo2Cache ERROR]");
			return false;
		}
		return pServiceImpl.add2NameList(profileName, description);
	}

	@Override
	public boolean modifyNameList(String profileName, String description) {
		if(!modifyCache(profileName,description)){
			log.debug("[modifyCache ERROR]");
			return false;
		}
		return pServiceImpl.modifyNameList(profileName, description);
	}

	@Override
	public void deleteFromNameList(String profileName) {

		if(!deleteFromCache(profileName)){
			log.debug("[deleteFromCache ERROR]");
		}
		pServiceImpl.deleteFromNameList(profileName);
	}

	
	@Override
	public boolean saveProfile2Ldap(String profileName, String description,
			String policiesStr) {
		return pServiceImpl.saveProfile2Ldap(profileName, description, policiesStr);
	}

	@Override
	public String getProfileFromLdap(String profileName) {
		
		if( !profileNameExist(profileName) ){
			return null;
		}
		String proStr = pServiceImpl.getProfileFromLdap(profileName);
		log.debug("[DEBUG ] [getProfileFromLdap] "+proStr);
		return proStr;
	}
	 
}
