package com.vmware.vdi.broker.toolboxfilter.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author nanw
 *
 * @param <T>
 */
public abstract class CachedObjs<T> {

	private final List<T> objects = new ArrayList<>();
	private long lastLoadTimestamp = 0;
	protected long timeoutMillis = 0;
	
	public CachedObjs() {
		
	}

	public CachedObjs(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public List<T> get(boolean forceReload) {
		synchronized (objects) {
			
			if (forceReload || needReload()) {
				populateCache(objects);
				lastLoadTimestamp = System.currentTimeMillis();
			}
			
			return new ArrayList<T>(objects);
		}
	}

	public List<T> get() {
		return get(false);
	}

	public void clear() {
		synchronized (objects) {
			if (lastLoadTimestamp != 0) {
				objects.clear();
				lastLoadTimestamp = 0;
			}
		}
	}
	
	private boolean needReload() {
		//if not loaded yet, reload
		if (lastLoadTimestamp == 0)
			return true;
		
		//if timeout is set, and timeout is reached, reload
		if (timeoutMillis != 0 && System.currentTimeMillis() - lastLoadTimestamp > timeoutMillis)
			return true;
		
		return false;
	}
	
	protected abstract void populateCache(List<T> objects);

}
