package com.vmware.horizon.auditing.db;

public class StringUtil {
	public static boolean isEmpty(String s){
		return s==null || s.trim().length() == 0;
	}
}
