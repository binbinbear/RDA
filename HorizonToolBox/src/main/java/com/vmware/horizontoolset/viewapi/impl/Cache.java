package com.vmware.horizontoolset.viewapi.impl;

import java.util.HashMap;
import java.util.List;

import com.vmware.horizontoolset.viewapi.RDS;
import com.vmware.horizontoolset.viewapi.SnapShot;
import com.vmware.horizontoolset.viewapi.VM;

public class Cache {
	private static HashMap<String, SnapShot> _snapShotCache = new HashMap<String, SnapShot>();
	 static SnapShot getSnapShot(String vmid, String fullpath){
		return _snapShotCache.get(vmid+fullpath);
	}
	
	 static void addOrUpdateSnapShot(String vmid, String fullpath, SnapShot snapshot){
		_snapShotCache.put(vmid+fullpath, snapshot);
	}
	
	private static HashMap<String, VM> _vmcache = new HashMap<String, VM>();
	 static VM getVM(String id){
		return _vmcache.get(id);
	}
	
	 static void addOrUpdateVM(String id, VM vm){
		_vmcache.put(id, vm);
	}
	
	private static HashMap<String, TemplateImpl> _tempCache = new HashMap<String, TemplateImpl> ();
	 static TemplateImpl getTemplate(String id){
		return _tempCache.get(id);
	}
	
	 static void addOrUpdateTemplate(String id, TemplateImpl template){
		_tempCache.put(id, template);
	}
	
	static void  emptyCache(){
		_snapShotCache.clear();
		_vmcache.clear();
		_tempCache.clear();
	}
	
}
