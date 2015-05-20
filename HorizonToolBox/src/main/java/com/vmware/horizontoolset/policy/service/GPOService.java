package com.vmware.horizontoolset.policy.service;

import java.util.List;
import java.util.Map;

public interface GPOService {
	
	public List<Map<String, String>> getGPO(String gpoName);
	
	public Map<String, String> getNameList();
	
	public boolean profileNameExist(String profileName);

	public List<Map<String, String>> removeGPO(String gpoName);
	
	public List<Map<String, String>> linkGPO(String profileName, String ouName);
	
	public List<Map<String, String>> setLinkGPO(String profileName, String ouName, String order);
	
	public List<Map<String, String>> removeLinkGPO(String profileName, String ouName);
	
	public List<Map<String, String>> removeBackup(String dirName);
	
	public boolean policyNewProcess(String profileName);
	
	public boolean policyEidtProcess(String profileName);
	
	//create directory 'c:\temp' if not exist
	public void checkDir();
}
