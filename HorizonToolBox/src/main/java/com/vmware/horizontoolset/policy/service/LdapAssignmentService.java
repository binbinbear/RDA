package com.vmware.horizontoolset.policy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.policy.model.ProfileItem;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;

public class LdapAssignmentService {
	private final static String poolProfileMappingName = "ppMapping";	// Map< String, List<String> >
	
	public Map<String, List<String>> getPPMap(){
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
	
	public void deleteFromPPMap(String poolName, String profileName){
		HashMap<String, List<String>> ppMap = (HashMap<String, List<String>>) getPPMap();
		ArrayList<String> profilesOfPool = (ArrayList<String>) ppMap.get(poolName);
		if( null==profilesOfPool ){
			return;
		}
		profilesOfPool.remove(profileName);
		ppMap.put(poolName, profilesOfPool);
		setPPMap(ppMap);
	}
	
	public JTableData getPorfilesOfPool(String poolName){
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
	
	public void setPorfilesOfPool(String poolName, List<String> profileNames){
		Map<String, List<String>> ppMap = getPPMap();
		ppMap.put(poolName, profileNames);
		setPPMap(ppMap);
	}
}
