package com.vmware.horizontoolset.devicefilter;

import java.util.List;



public interface DeviceFilterManager {

	public List<DeviceFilterPolicy> getAllPolicies(List<String> pools) ;

	public void updateFilterPolicy(DeviceFilterPolicy policy) ;

	public void removeFilterPolicy(String pool) ;

	 public boolean isEnabled();

	 public void enable();

	 public void disable();



}
