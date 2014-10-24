package com.vmware.horizontoolset.device.data.ssa;

/**
 * A shared storage, with late consistency.
 * 
 * Shared: the storage will be shared across multiple View Connection servers.
 * Late consistency: Changes to the storage is only visible to other readers 
 * after certain time period.
 * 
 * The SharedStorageAccess maintains a cache for read operation.
 *
 */
public class SharedStorageAccess {

	private final String key;
	
	protected SharedStorageAccess(String key) {
		this.key = key;
	}
	
	
}
