package com.vmware.horizontoolset.policy.service;

import java.util.List;
import java.util.Map;

import com.vmware.horizontoolset.common.jtable.JTableData;

public interface AssignmentService {
	public Map<String, List<String>> getPPMap();
	
	public void setPPMap(Map<String,List<String>> ppMap);
	
	public boolean add2ppMap(String poolNames, String profileNames);
	
	public void deleteFromPPMap(String poolName, String profileName);
	
	public void deleteAssignedProfiles(String profileName);
	
	public JTableData getPorfilesOfPool(String poolName);
	
	public void setPorfilesOfPool(String poolName, List<String> profileNames);
}
