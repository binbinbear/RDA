package com.vmware.horizontoolset.ra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.ToolboxStorage;
import com.google.gson.reflect.TypeToken; 

public class UnsolicitedRAHistoryMag {
	private static Logger log = Logger.getLogger(UnsolicitedRAHistoryMag.class);
	private static final String STORED_RAHIST_ATTR_KEY = "RAHIST";
	private static final int MAX_HISTITEM_COUNT = 200;
	private static boolean histLoadedFromLdap = false;
	
	private static List<HraHistoryItem> raHistItems = new ArrayList<HraHistoryItem>(); 
	
	private static void LoadFromStorage() {
		raHistItems.clear();
		histLoadedFromLdap = true;
		String jsonHist = ToolboxStorage.getStorage().get(STORED_RAHIST_ATTR_KEY);
		if(jsonHist != null) {
			HraHistoryItem[] histArray = JsonUtil.jsonToJava(jsonHist, HraHistoryItem[].class);
			if((histArray != null) && (histArray.length > 0))
				Collections.addAll(raHistItems, histArray);
		}
	}
	
	private static void SaveHistItem2Storage() {
		String jsonHist = JsonUtil.javaToJson(raHistItems);
		ToolboxStorage.getStorage().set(STORED_RAHIST_ATTR_KEY, jsonHist);
	}
	
	private static void FlushHistItemsFromMemory2Storage() {
		
	}
	
	public synchronized static List<HraHistoryItem> list() {
		try{
			if(histLoadedFromLdap == false) {
				LoadFromStorage();
			}
			
			return new ArrayList<HraHistoryItem>(raHistItems);
		} catch(Exception ex) {
			log.info("caught exception in list function, exception: " + ex.toString());
			
			return null;
		}

	}
	
	public synchronized static void add(HraHistoryItem histItem) {
		try{
			if(histLoadedFromLdap == false) {
				LoadFromStorage();
			}
			raHistItems.add(histItem);
			while(raHistItems.size() > MAX_HISTITEM_COUNT) {
				raHistItems.remove(0);
			}
			SaveHistItem2Storage();
		} catch(Exception ex) {
			log.info("caught exception in add function, exception: " + ex.toString());
		}
	}
	
	public static String Transfer2Json() {
		String jsonHist = JsonUtil.javaToJson(raHistItems);
		return jsonHist;
	}
}
