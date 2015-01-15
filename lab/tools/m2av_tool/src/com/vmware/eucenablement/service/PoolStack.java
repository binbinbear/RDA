package com.vmware.eucenablement.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.vmware.eucenablement.util.SharedStorageAccess;

public class PoolStack {
	private static Set<String> appStacks = null;

	public static void put(String poolName, String appStack) {
		SharedStorageAccess.write2File(poolName, appStack);
	}

	public static Set<String> get(String poolName) {
		appStacks = new HashSet<>();
		ArrayList<String> temp = SharedStorageAccess.readFile(poolName);
		if (temp == null || temp.isEmpty())
			return null;
		for (String value : temp) {
			appStacks.add(value);
		}
		return appStacks;
	}

	public static void delete(String poolName, String appStack) {
		SharedStorageAccess.deleteLine(poolName, appStack);
	}
}
