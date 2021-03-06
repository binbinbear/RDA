package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.horizon.auditing.db.EventDBUtil;
import com.vmware.horizon.auditing.db.StorageException;

/**
 *
 * This storage use DB
 * Refer to EventDBUtil.
 */
public class DBStorage extends ToolboxStorage {

	private EventDBUtil dbUtil;
	public DBStorage() throws StorageException{
		this.dbUtil = EventDBUtil.createDefault();
		//DB is not ready, so disable for a short time
//		if (!this.dbUtil.isToolboxTableAvaiable()){
			throw new StorageException("Event DB can't be used, try LDAP or some other way!");
//		}
	}

	@Override
	public void delete(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getList(String key) {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public void setList(String key, List<String> values) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getMap(String key) {
		// TODO Auto-generated method stub
		return new HashMap<String, String>();
	}

	@Override
	public void setMap(String key, Map<String, String> map) {
		// TODO Auto-generated method stub

	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String get(String namekey, String attrkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(String namekey, String attrkey, String value) {
		// TODO Auto-generated method stub

	}
}
