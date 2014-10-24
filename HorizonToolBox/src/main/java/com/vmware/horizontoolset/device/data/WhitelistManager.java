package com.vmware.horizontoolset.device.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WhitelistManager {

	private static List<WhitelistRecord> data = new LinkedList<>();
	
	public synchronized static List<WhitelistRecord> list() {
		return new ArrayList<WhitelistRecord>(data);
	}
	
	public synchronized static boolean delete(long recordId) {
		for (Iterator<WhitelistRecord> i = data.iterator(); i.hasNext(); ) {
			WhitelistRecord r = i.next();
			if (r.recordId == recordId) {
				i.remove();
				return true;
			}
		}
		return false;
	}
	
	public synchronized static void add(WhitelistRecord record) {
		if (data.contains(record))
			throw new IllegalArgumentException("Record already exists: " + record);
		data.add(0, record);
	}
	
	public synchronized static boolean isAllowed(DeviceInfo di) {
		
		if (di == null || di.ViewClient_Client_ID == null || di.ViewClient_Client_ID.isEmpty())
			return false;
		
		for (WhitelistRecord i : data) {
			if (i.deviceInfo.isSameDevice(di)) {
				
				i.deviceInfo = di;	//refresh device info
				i.lastAccessTime = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}
	
}
