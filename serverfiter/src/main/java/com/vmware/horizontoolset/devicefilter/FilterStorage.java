package com.vmware.horizontoolset.devicefilter;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.CachedObjs;


public class FilterStorage {
	private static Logger log = Logger.getLogger(FilterStorage.class);
	public final CachedObjs<DeviceFilterPolicy> policies = new CachedObjs<DeviceFilterPolicy>(60000) {

		@Override
		protected void populateCache(List<DeviceFilterPolicy> objects) {

			objects.clear();

		}
	};
}
