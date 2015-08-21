package com.vmware.horizontoolset.policy.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.policy.service.AssignmentService;
import com.vmware.horizontoolset.policy.util.SharedStorageUtil;
import com.vmware.horizontoolset.util.JsonUtil;


public class AssignmentServiceImpl implements AssignmentService{
	private final static String poolProfileMappingName = "ppMapping";	// Map< String, List<String> >
	
	private static SharedStorageUtil SharedStorageAccess = new SharedStorageUtil();
	
	private static Logger log = Logger.getLogger(AssignmentServiceImpl.class);
	public AssignmentServiceImpl(){
		log.debug("[DEBUG ] init AssignmentServiceImpl");
	}
	
	
	public Map<String, List<String>> getPPMap(){ //Pool-Profile Map
		
		Map<String, List<String>> ppMap;
		String ppMapStr = SharedStorageAccess.get(poolProfileMappingName);
		log.debug("[DEBUG ] ppMapStr::\n" + ppMapStr);
		
		if( null==ppMapStr ){
			log.debug("[DEBUG ] null==ppMapStr");
			ppMap = new HashMap<String,List<String>>();
			setPPMap(ppMap);
			return ppMap;	// return new Map<String,List<String>>();
		}
		ppMap = JsonUtil.jsonToJava(ppMapStr, Map.class); // Map.class
		log.debug("ppMap = " + ppMap.toString());
		return ppMap;
	}
	
	public void setPPMap(Map<String,List<String>> ppMap){
		String ppMapStr = JsonUtil.javaToJson(ppMap);
		SharedStorageAccess.set(poolProfileMappingName, ppMapStr);
	}
	
	public boolean add2ppMap(String poolNames, String profileNames){
		String poolArray[] = JsonUtil.jsonToJava(poolNames, String[].class);
		String profileArray[] = JsonUtil.jsonToJava(profileNames, String[].class);
		int poolArrayLen = poolArray.length;
		int profileArrayLen = profileArray.length;

		if( poolArrayLen<=0 || profileArrayLen<=0 ){	
			return false;
		}
		
		Map<String, List<String>> ppMap = getPPMap();
		for(int i=0; i<poolArrayLen; i++){
			ArrayList<String> profileItems = (ArrayList<String>) ppMap.get(poolArray[i]);
			if( null==profileItems ){
				profileItems = new ArrayList<String>();
			}
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
	
	public void deleteFromPPMap(String poolName, String profileName){
		Map<String, List<String>> ppMap = getPPMap();
		
		ArrayList<String> profilesOfPool = (ArrayList<String>) ppMap.get(poolName);
		if( null==profilesOfPool ){
			return;
		}
		
		profilesOfPool.remove(profileName);
		ppMap.put(poolName, profilesOfPool);
		setPPMap(ppMap);
	}
	
	public void deleteAssignedProfiles(String profileName){
		log.debug("[DEBUG ] delete " + profileName);
		Map<String, List<String>> ppMap = getPPMap();
		
		for(Map.Entry<String, List<String>> entry : ppMap.entrySet()){
			List<String> plist = entry.getValue();
			if(plist.contains(profileName)){
				String poolName = entry.getKey();
				deleteFromPPMap(poolName, profileName);
			}else{
				log.debug("[DEBUG ] int " + entry.getKey() + "can't find " + profileName);
			}
		}
	}
	
	public JTableData getPorfilesOfPool(String poolName){
		JTableData ret = new JTableData();
		ArrayList<ProfileItem> profileItems = new ArrayList<ProfileItem>();
		Map<String, List<String>> ppMap = getPPMap();
		//log.debug( "[DEBUG ] [getPorfilesOfPool] ppMap::\n" + ppMap.toString() );
		
		ArrayList<String> profileArray = (ArrayList<String>) ppMap.get(poolName);
		if( null==profileArray ){
			log.debug("null==profileArray");
			return ret;
		}
		
		//log.debug("[DEBUG ] [getPorfilesOfPool] " + profileArray.toString());
		for(String proName : profileArray){
			profileItems.add(new ProfileItem(proName));
		}
		
		ret.Records = profileItems.toArray();
		ret.TotalRecordCount = profileItems.size();
		//log.debug("ret::\n" + ret.toString());
		return ret;
	}
	
	public void setPorfilesOfPool(String poolName, List<String> profileNames){
		Map<String, List<String>> ppMap = getPPMap();
		ppMap.put(poolName, profileNames);
		setPPMap(ppMap);
	}
}
