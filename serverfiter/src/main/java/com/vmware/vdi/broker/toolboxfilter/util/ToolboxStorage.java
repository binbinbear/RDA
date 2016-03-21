package com.vmware.vdi.broker.toolboxfilter.util;

import java.util.List;
import java.util.Map;


public abstract class ToolboxStorage {

	public static ToolboxStorage getStorage(){
		//TODO: enable DB Storage
		return new SharedStorageAccess();

	}

	public abstract void delete(String key);


	public abstract List<String> getList(String key);
	public abstract void  setList(String key, List<String> values);

	public abstract Map<String,String> getMap(String key);

	public abstract void  setMap(String key, Map<String, String> map);

	/**
	 * * Use the default "description" as the attribute key
	 * @param key
	 * @return
	 */
	public abstract String get(String key);


	public abstract String get(String namekey, String attrkey);

	/**
	 * * Use the default "description" as the attribute key
	 * @param key
	 * @param value
	 */
	public abstract void set(String key, String value) ;


	public abstract void set(String namekey, String attrkey, String value) ;

}
