package com.vmware.horizontoolset.report;

import java.util.HashMap;

import com.vmware.horizon.auditing.report.AbstractReport;

public class ClientReport extends AbstractReport{
	
	public ClientReport(){
		
	}
	private HashMap<String, Integer> osMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> versionMap = new HashMap<String, Integer>();
	public HashMap<String, Integer> getVersionMap() {
		return versionMap;
	}
	public HashMap<String, Integer> getOsMap() {
		return osMap;
	}
	
	private void addtomap(HashMap<String, Integer> map, String key, int count){
		if (map.containsKey(key)){
			int current = map.get(key);
			map.put(key, current+count);
		}else{
			map.put(key, count);
		}
	}
	
	public void addOS(String os){
		this.addtomap(osMap, os, 1);
		
	}
	
	
	public void addVersion(String version){
		this.addtomap(versionMap, version, 1);
	}
	
	public void merge(ClientReport parentReport){
		HashMap<String, Integer> parentOSMap = parentReport.getOsMap();
		for(String key: parentOSMap.keySet()){
			this.addtomap(this.osMap, key, parentOSMap.get(key));
		}
		
		
		HashMap<String, Integer> parentVersionMap = parentReport.getVersionMap();
		for(String key: parentVersionMap.keySet()){
			this.addtomap(this.versionMap, key, parentVersionMap.get(key));
		}
		
		
	}

}
