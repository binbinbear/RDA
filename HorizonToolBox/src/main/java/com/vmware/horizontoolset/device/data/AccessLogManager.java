package com.vmware.horizontoolset.device.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

public class AccessLogManager {

	public static final int CAPACITY = 1000;	//num of unique devices
	
	private static List<AccessRecord> data = new LinkedList<>();
	
	public synchronized static List<AccessRecord> list() {
		return new ArrayList<AccessRecord>(data);
	}

	public synchronized static AccessRecord get(long id) {
		for (AccessRecord r : data)
			if (r.recordId == id)
				return r;
		return null;
	}

	public static boolean log(HttpSession session, DeviceInfo di) {
		
		boolean isAllowed = WhitelistManager.isAllowed(di);
		
		record(di, isAllowed);
		
		return isAllowed;
	}

	private synchronized static void record(DeviceInfo di, boolean isAllowed) {

		while (data.size() >= CAPACITY)
			data.remove(0);
		
		AccessRecord r = new AccessRecord(di, isAllowed);
		
		for (Iterator<AccessRecord> i = data.iterator(); i.hasNext();) {
			AccessRecord ar = i.next();
			if (ar.deviceInfo.isSameDevice(di)) {
				i.remove();
				break;
			}
		}
		data.add(0, r);
	}
}
