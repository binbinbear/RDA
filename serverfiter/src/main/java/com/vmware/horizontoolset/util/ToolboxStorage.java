package com.vmware.horizontoolset.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.db.StorageException;

public abstract class ToolboxStorage {

	private static Logger log = Logger.getLogger(ToolboxStorage.class);
	public static ToolboxStorage getStorage(){
		try {
			ToolboxStorage _instance = new DBStorage();
			return _instance;
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage(),e);
			return new SharedStorageAccess();
		}

	}

	public abstract void delete(String key);

	public abstract List<String> getList(String key);
	public abstract void  setList(String key, List<String> values);

	public abstract Map<String,String> getMap(String key);

	public abstract void  setMap(String key, Map<String, String> map);

	public abstract String get(String key);

	public abstract void set(String key, String value) ;

}
