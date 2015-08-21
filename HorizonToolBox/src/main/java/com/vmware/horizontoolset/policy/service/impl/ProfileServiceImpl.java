package com.vmware.horizontoolset.policy.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.policy.model.PolicyModel;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.policy.service.ProfileService;
import com.vmware.horizontoolset.policy.util.SharedStorageUtil;
import com.vmware.horizontoolset.util.JsonUtil;


public class ProfileServiceImpl implements ProfileService{
	private final static String indexTableName = "profileMap";		// 存储profile名称和description的列表， Map<String,String>
//	private final static String profileSubCountTable = "subLengthTable";
//	private final static String SUB = "-SUB";  // -SUB-
//	private final static int SUBLENGTH = 512; 
	private static SharedStorageUtil SharedStorageAccess = new SharedStorageUtil();
	
	
	private static Logger log = Logger.getLogger(ProfileServiceImpl.class);
	public ProfileServiceImpl(){
		log.debug("[DEBUG ] init ProfileServiceImpl");
	}
	
	public Map<String, String> getNameList(){
		Map<String, String> proItems;
		String nameListStr = SharedStorageAccess.get(indexTableName);
		if(null==nameListStr){
			log.debug("[DEBUG ] nameList not exist !!!");
			Map<String, String> proNames = new HashMap<String, String>();
			setNameList(proNames);
			//SharedStorageAccess.set(indexTableName,"");
			//return null;
		}
		proItems = JsonUtil.jsonToJava(nameListStr, Map.class);
		log.debug("[DEBUG ] [proItems]" + proItems.toString());
		return proItems;
	}
	
	public boolean profileNameExist(String profileName){
		Map<String, String> proNames = getNameList();
		if( null==proNames ){
			log.debug("[DEBUG ] proName==null");
			return false;	// nameList doesn't exist
		}
		if( proNames.containsKey(profileName) ){
			log.debug("[DEBUG ] containsKey");
			return true;	// nameList exist
		} 
		return false;	// nameList exist
	}
	
	private void setNameList(Map<String, String> nMap){
		//写入完全重复的value，会 throw exception
		log.debug("[DEBUG ] [previous:] " + SharedStorageAccess.get(indexTableName));
		String nMapStr = JsonUtil.javaToJson(nMap);
		SharedStorageAccess.set(indexTableName, nMapStr);
		log.debug("[DEBUG ] [present:] " + SharedStorageAccess.get(indexTableName));
	}
	
	//新添加一个profileName到索引中，如果已经存在返回false
	public boolean add2NameList(String profileName, String description){
		Map<String, String> proItems = getNameList();
		if(null == proItems){	
			proItems = new HashMap<String,String>();
		}
		if( null != proItems.get(profileName) )
			return false;	//文件名已经存在
		proItems.put(profileName, description);
		setNameList(proItems);
		return true;
	}
	
	public boolean modifyNameList(String profileName, String description){
		Map<String, String> proItems = getNameList();
		if(null == proItems){	
			proItems = new HashMap<String,String>();
		}
		if( !proItems.containsKey(profileName) )
			return false;	//文件名不存在
		proItems.put(profileName, description);
		setNameList(proItems);
		return true;
	}
	
	//删除profile: 从list删除,并删除数据
	public void deleteFromNameList(String profileName){
		//从NameList中删除，并且将profileName对应的数据删除
		Map<String, String> proItems = getNameList();
		if(null != proItems){
			proItems.remove(profileName);
			setNameList(proItems);	
			//先将profileName对应的数据删除,并删除count数据
			//deleteSubList(profileName);
			SharedStorageAccess.delete(profileName);
		}
	}
	
	//******************************************************************
	
	public boolean saveProfile2Ldap(String profileName, String description, String policiesStr){
		// 1. 从前端接收数据，得到policies数组对象
		PolicyModel[] policies = JsonUtil.jsonToJava(policiesStr, PolicyModel[].class);
		ProfileModel newPro = new ProfileModel(profileName, description, policies);  //String -> json
		
		// 2. 持久化到ldap中
		String newProStr = JsonUtil.javaToJson(newPro);
		return SharedStorageAccess.set(profileName, newProStr);
	}
	
	public String  getProfileFromLdap(String profileName){
		//String proStr = loadSubList(profileName);
		String proStr = SharedStorageAccess.get(profileName);
		
		//log.debug("[DEBUG ] [ProfileServiceImpl] [getProfileFromLdap] "+proStr);
		return proStr;
	}
}
