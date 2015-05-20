package com.vmware.horizontoolset.policy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;

public class SharedStorageUtil {
	private final static String SharedStorageIndex_Name = "SharedStorageIndex"; //subItemMap'index 所有通过SharedStorageUtil存储的文件信息
	private final static String SubItemsCountTable_Name = "SubItemsCount"; //subMap-1, subMap-2, subMap-3
	private final static String subItemSeparator = "-SUB-";
	private final static int SUBLENGTH = 512; //512*2
	
	private static Logger log = Logger.getLogger(SharedStorageUtil.class);
	
	public SharedStorageUtil(){
		sharedStorageInit();
		log.debug("[SS Util] [SharedStorageIndex_Name] " + get_(SharedStorageIndex_Name));
		log.debug("[SS Util] [SubItemsCountTable_Name] " + get_(SubItemsCountTable_Name));
	}

	private boolean checkSaveSuccess(String keyName, String value){
		String res = SharedStorageAccess.get(keyName);
		if( res.equals(value) )
			return true;
		return false;
	}
	
	//****************************************************************************
	
	private void sharedStorageInit(){
		String indexRes = get_(SharedStorageIndex_Name);
		if( null==indexRes ){
			List<String> indexList = new ArrayList<String>();
			String indexListStr = JsonUtil.javaToJson(indexList);
			SharedStorageAccess.set(SharedStorageIndex_Name, indexListStr);
		}
		String countMapRes = get_(SubItemsCountTable_Name);
		if( null==countMapRes ){
			Map<String, String> countMap = new HashMap<String, String>();
			String countMapStr = JsonUtil.javaToJson(countMap);
			SharedStorageAccess.set(SubItemsCountTable_Name, countMapStr);
		}
	}
	
	private List<String> getIndexList(){
		String indexListStr = SharedStorageAccess.get(SharedStorageIndex_Name);
		List<String> indexList = JsonUtil.jsonToJava(indexListStr, List.class);
		return indexList;
	}
	
	private void setIndexList(List<String> indexlist){
		String indexListStr = JsonUtil.javaToJson(indexlist);
		SharedStorageAccess.set(SharedStorageIndex_Name,indexListStr);
	}
	
	private void add2Index(String key){
		List<String> indexList = getIndexList();
		if( null==indexList ){
			indexList = new ArrayList<String>();
		}
		if( !indexList.contains(key) ){ //save no-duplicate values
			indexList.add(key);
			setIndexList(indexList);
		}  
	}
	
	private void deleteFromIndex(String key){
		List<String> indexList = getIndexList();
		if( null==indexList ){
			return;
		}
		indexList.remove(key);
		setIndexList(indexList);
	}
	
	//****************************************************************************
	
	private boolean set_(String key, String value){
		SharedStorageAccess.set(key, value);
		boolean res = checkSaveSuccess(key,value);
		if(res){
			add2Index(key);
		}
		return res;
	}
	
	private String get_(String key){
		return SharedStorageAccess.get(key);
	}
	
	private void delete_(String key){
		SharedStorageAccess.delete(key);
		deleteFromIndex(key);
	}
	
	//****************************************************************************
	//overwrite
	public boolean set(String key, String val){
		
		log.debug("[SS Util] [save] key=" + key + ", val=" + val); 
		
		boolean saveResult = true;
		ArrayList<String> sublist = (ArrayList<String>) splitStr(val);
		delete(key);
		add2CountMap(key, String.valueOf( sublist.size() ) );
		for(int i=0; i<sublist.size(); i++){
			saveResult = set_( key + subItemSeparator + i, sublist.get(i) );
			//if( !set( key + subItemSeparator + i, sublist.get(i) ) ){ saveResult=false; }
			log.debug("[SS Util] [save] " +  key + subItemSeparator + i + "\n ::" + sublist.get(i));
		}
		
		log.debug("[SS Util] [after save] [Index] " + get_(SharedStorageIndex_Name)); // ["testSSUtil01-SUB-0","testSSUtil01-SUB-1"]
		log.debug("[SS Util] [after save] [CountTable] " + get_(SubItemsCountTable_Name)); // {}
		
		return saveResult;
	}
	
	public String get(String key){
		int cNum = getCountNum(key);
		if( cNum < 0 )
			return null;
		String subs = "";
		for(int i=0; i<cNum; i++){
			String subStr = get_(key + subItemSeparator + i);
			log.debug("[SS Util] [load] " +  key + subItemSeparator + i + "\n ::" + subStr);
			if( null == subStr ){
				return null;
			}
			subs += subStr;
		}
		return subs;
	}
	
	public boolean delete(String key){
		
		log.debug("[SS Util] [del] key=" + key); 
		
		int cNum = getCountNum(key);
		if( cNum < 0 ){
			log.debug("[SS Util] cNum=" + cNum);
			return false;
		}
		for(int i=0; i<cNum; i++){
			if( null != get_(key + subItemSeparator + i) ){
				log.debug("[SS Util] [delete] " + key + subItemSeparator + i);
				delete_(key + subItemSeparator + i);
			}else{
				log.debug("[SS Util] [ERROR] " + key + subItemSeparator + i + " not found");
			}
			
		}
		deleteFromCountMap(key);
		
		log.debug("[SS Util] [after del] [Index] " + get_(SharedStorageIndex_Name));
		log.debug("[SS Util] [after del] [CountTable] " + get_(SubItemsCountTable_Name));
		
		return true;
	}
	
	//****************************************************************************
	
	private int countStrSubNum(String policiesStr){
		if( 0 == policiesStr.length() % SUBLENGTH ){
			return policiesStr.length() / SUBLENGTH;
		}
		return policiesStr.length() / SUBLENGTH + 1;
	}
	
	private List<String> splitStr(String value){
		int subNum = countStrSubNum(value);
		List<String> subList = new ArrayList<String>();
		for(int i=0; i<subNum; i++){
			log.debug("[SS Util] i="+i+" ** "+value.substring(i*SUBLENGTH, Math.min(i*SUBLENGTH+SUBLENGTH, value.length())));
			subList.add(value.substring(i*SUBLENGTH, Math.min(i*SUBLENGTH+SUBLENGTH, value.length())));
		}
		return subList;
	}

	//******************************************************************
	
	private Map<String, String> getCountMap(){
		Map<String, String> countMap;
		String countMapStr = get_(SubItemsCountTable_Name);
		if(null==countMapStr){
			countMap = new HashMap<String, String>();
			setCountMap(countMap);
			set_(SubItemsCountTable_Name, "");
			return null;
		}
		countMap = JsonUtil.jsonToJava(countMapStr, Map.class);
		return countMap;
	}
	
	private void setCountMap(Map<String, String> countMap){
		String countMapStr = JsonUtil.javaToJson(countMap);
		set_(SubItemsCountTable_Name, countMapStr);
	}
	
	/*
	 * Before add2CountMap, you must delete the data which has the same keyname
	 * 
	 * */
	private boolean add2CountMap(String profileName, String count){
		Map<String, String> proCounts = getCountMap();
		log.debug("[SS Util] [add2CountMap] previous: " + proCounts.toString());
		if( null != proCounts.get(profileName) ){
			return false;
		}
		if( proCounts.get(profileName)==count ){
			return true;
		}
		proCounts.put(profileName, count);
		setCountMap(proCounts);
		log.debug("[SS Util] [add2CountMap] present: " + proCounts.toString());
		return true;
	}
	
	private int getCountNum(String profileName){
		Map<String, String> countMap = getCountMap();
		if(null == countMap){
			log.debug("[SS Util] CountMap not exist !!!");
			return -2;
		}
		String countMapStr = countMap.get(profileName);
		if(null == countMapStr ){
			log.debug("[SS Util] Can not find " + profileName);
			return -1;
		}
		log.debug("[SS Util] countstr=" + countMapStr);
		return Integer.valueOf(countMapStr);
	}
	
	public void deleteFromCountMap(String key){
		Map<String, String> countMap = getCountMap();
		log.debug("[SS Util] [deleteFromCountMap] start\n" + getCountMap().toString());
		if( null != countMap ){
			if(null!=countMap.remove(key)){
				setCountMap(countMap);
			}
		}
		log.debug("[SS Util] [deleteFromCountMap] end\n" + getCountMap().toString());
	}
	
}
