package com.vmware.horizontoolset.device.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vmware.horizontoolset.util.SharedStorageAccess;

public class WhitelistManager {

	private static final long READ_CACHE_EXPIRATION = 120 * 1000; 
	private static final String STORED_ATTR_KEY = "WHITELIST";
	
	private static List<WhitelistRecord> data = new LinkedList<>();
	private static volatile long lastLoadTimestamp; 
	
	
	public static List<WhitelistRecord> list() {
		
		synchronized(data) {
			ensureData();
			return new ArrayList<WhitelistRecord>(data);
		}
	}
	
	public static boolean delete(long recordId) {
		synchronized(data) {
			ensureData();
			
			for (Iterator<WhitelistRecord> i = data.iterator(); i.hasNext(); ) {
				WhitelistRecord r = i.next();
				if (r.recordId == recordId) {
					i.remove();
					saveData();
					return true;
				}
			}
		}
		return false;
	}
	
	public static void add(WhitelistRecord record) {
		synchronized(data) {
			ensureData();
			
			if (data.contains(record))
				throw new IllegalArgumentException("Record already exists: " + record);
			data.add(0, record);
			saveData();
		}
	}
	
	public static boolean isAllowed(DeviceInfo di) {
		
		if (di == null || di.ViewClient_Client_ID == null || di.ViewClient_Client_ID.isEmpty())
			return false;
		
		synchronized(data) {
			ensureData();
			
			for (WhitelistRecord i : data) {
				if (i.deviceInfo.isSameDevice(di)) {
					
					i.deviceInfo = di;	//refresh device info
					i.lastAccessTime = System.currentTimeMillis();
					return true;
				}
			}
		}
		return false;
	}
	
	private static void ensureData() {
		long now = System.currentTimeMillis();
		if (now - lastLoadTimestamp > READ_CACHE_EXPIRATION) {
			if (loadData())
				lastLoadTimestamp = now;
		}
	}
	
	///////////////////////////////////////////////////////////////////
	private static boolean loadData() {
		
		synchronized(data) {
			
			String val = SharedStorageAccess.get(STORED_ATTR_KEY);
			
			if (val == null)
				return true;
			
			String[] tmp = val.split(";;");
			
			Set<String> ids = new HashSet<>(Arrays.asList(tmp));
			
			for (Iterator<WhitelistRecord> i = data.iterator(); i.hasNext();) {

				String clientId = i.next().deviceInfo.ViewClient_Client_ID;
				
				if (ids.contains(clientId)) {
					//the ID still exists in the storage. 
					//keep it in the data so other associated information are kept,
					//e.g. login time.
					//Remove from ids, so that eventually ids contains all new IDs.
					ids.remove(clientId);
				} else {
					//the id disappears. So remove it from data.
					i.remove();
				}
			}
			
			//handle new IDs
			for (String s : ids) {
				data.add(new WhitelistRecord(new DeviceInfo(s)));
			}
		}
		
		return true;
	}
	
	private static void saveData() {
		//no need to sync due to all caller synchronized
		
		StringBuilder sb = new StringBuilder();
		for (WhitelistRecord i : data) {
			sb.append(i.deviceInfo.ViewClient_Client_ID).append(";;");
		}
		
		SharedStorageAccess.set(STORED_ATTR_KEY, sb.toString());
	}
}
