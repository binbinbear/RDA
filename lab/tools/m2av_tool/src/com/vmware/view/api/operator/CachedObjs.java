package com.vmware.view.api.operator;

import java.util.ArrayList;
import java.util.List;

public abstract class CachedObjs<T> {

	private final List<T> objects = new ArrayList<>();
	private boolean cacheLoaded;
	
	CachedObjs() {
	}
	
	public List<T> get() {
		synchronized (objects) {
			
			if (!cacheLoaded) {
				populateCache(objects);
				cacheLoaded = true;
			}
			
			return new ArrayList<T>(objects);
		}
	}
	
	public void clearCache() {
		synchronized (objects) {
			
			objects.clear();
			cacheLoaded = false;
		}
	}
	
	protected abstract void populateCache(List<T> objects);
}
