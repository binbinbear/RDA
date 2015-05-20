package com.vmware.horizontoolset.policy.service;

import java.util.Map;

public interface ProfileService {
	
	public Map<String, String> getNameList();
	
	public boolean profileNameExist(String profileName);
	
	public boolean add2NameList(String profileName, String description);
	
	public boolean modifyNameList(String profileName, String description);
	
	public void deleteFromNameList(String profileName);
	
	public boolean saveProfile2Ldap(String profileName, String description, String policiesStr);
	
	public String  getProfileFromLdap(String profileName);
}
