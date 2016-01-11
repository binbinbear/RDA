package com.vmware.vdi.broker.toolboxfilter.util;

import java.util.List;
import java.util.Map;

public abstract class ToolboxStorage {
	public static ToolboxStorage getStorage(){

		return new SharedStorageAccess();


	}

	public abstract void delete(String key);

	public abstract List<String> getList(String key);
	public abstract void  setList(String key, List<String> values);

	public abstract Map<String,String> getMap(String key);

	public abstract void  setMap(String key, Map<String, String> map);

	public abstract String get(String key);

	public abstract void set(String key, String value) ;

}
