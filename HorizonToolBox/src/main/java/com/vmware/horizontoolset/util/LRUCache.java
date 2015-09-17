package com.vmware.horizontoolset.util;



import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K,V> extends LinkedHashMap<K,V> {
	private static final long serialVersionUID = -5477999416234576638L;
	private int MAX_CACHE_SIZE;
	public LRUCache(int cacheSize){
		super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
		MAX_CACHE_SIZE= cacheSize;
	}
	 @Override
	 protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		return size() > MAX_CACHE_SIZE;
	 }
}
