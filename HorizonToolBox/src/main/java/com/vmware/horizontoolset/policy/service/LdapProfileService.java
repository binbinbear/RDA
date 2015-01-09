package com.vmware.horizontoolset.policy.service;

import java.util.HashMap;
import java.util.Map;

import com.vmware.horizontoolset.policy.model.PolicyModel;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;

public class LdapProfileService {
	private final static String indexTableName = "profileMap";			// Map<String,String>
	
	public Map<String, String> getNameList(){
		Map<String, String> proItems;
		String nameListStr = SharedStorageAccess.get(indexTableName);
		if(null==nameListStr){
			return null;
		}
		proItems = JsonUtil.jsonToJava(nameListStr, Map.class);
		return proItems;
	}
	
	public boolean profileNameExist(String profileName){
		Map<String, String> proNames = getNameList();
		if( null==proNames ){
			return false;	// nameList 不存在
		}
		if( proNames.containsKey(profileName) ){
			return true;
		}
		return false;	// 文件名不存在
	}
	
	public void setNameList(Map<String, String> nMap){
		String nMapStr = JsonUtil.javaToJson(nMap);
		SharedStorageAccess.set(indexTableName, nMapStr);
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
	
	public void deleteFromNameList(String profileName){
		Map<String, String> proItems = getNameList();
		if(null != proItems){
			proItems.remove(profileName);
			setNameList(proItems);
			SharedStorageAccess.delete(profileName);
		}
	}
	
	public void saveProfile2Ldap(String profileName, String description, String policiesStr){
		// 1. 从前端接收数据，得到policies数组对象
		PolicyModel[] policies = JsonUtil.jsonToJava(policiesStr, PolicyModel[].class);
		ProfileModel newPro = new ProfileModel(profileName, description, policies);

		// 2. 持久化到ldap中
		String newProStr = JsonUtil.javaToJson(newPro);
		SharedStorageAccess.set(profileName, newProStr);	//存储空profile
	}
	
}
