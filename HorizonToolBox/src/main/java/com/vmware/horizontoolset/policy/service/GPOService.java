package com.vmware.horizontoolset.policy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.horizontoolset.policy.gpo.Grouppolicycmdlets;
	
public class GPOService {
	private Grouppolicycmdlets grouppolicycmdlets;
	
	public GPOService(){
		grouppolicycmdlets = new Grouppolicycmdlets("eucsolutionad.eucsolution.com",
				"eucsolution\\administrator",
				"VMware123");
	}
	
	private List<Map<String, String>> getGPO(String gpoName){
		Map<String,String> getGpoParam = new HashMap<String,String>();
		getGpoParam.put("Name", "'" + gpoName + "'");
		return grouppolicycmdlets.getGPO(getGpoParam);
	}
	
	private List<Map<String, String>> getAllGPO(String gpoName){
		Map<String,String> getAllGpoParam = new HashMap<String,String>();
		getAllGpoParam.put("All", "");
		getAllGpoParam.put("Domain", "eucsolution.com");
		return grouppolicycmdlets.getGPO(getAllGpoParam);
	}
	
	private List<Map<String, String>> copyGPO(String gpoName){
		Map<String,String> copyGpoParam = new HashMap<String,String>();
		copyGpoParam.put("sourceName", "'Default Domain Policy'");	//Default Domain Policy
		copyGpoParam.put("targetName", "'" + gpoName + "'");
		
		if(grouppolicycmdlets.getGPO(copyGpoParam).size()==0){
			return grouppolicycmdlets.copyGPO(copyGpoParam);
		}
		return null;
	}
	
	private List<Map<String, String>> newGPO(String gpoName){
		Map<String,String> newGpoParam = new HashMap<String,String>();
		newGpoParam.put("Name", "'" + gpoName + "'");
		if(grouppolicycmdlets.getGPO(newGpoParam).size()==0){
			return grouppolicycmdlets.newGPO(newGpoParam);
		}
		return null;
	}
	
	private List<Map<String, String>> backupGPO(String gpoName){
		Map<String,String>  backupGpoParam = new HashMap<String, String>();
		backupGpoParam.put("Name", "'" + gpoName + "'");
		backupGpoParam.put("Path", "c:\\temp\\");
		return grouppolicycmdlets.backupGPO(backupGpoParam);
	}
	
	
	//Get-WmiObject -Class Win32_ComputerSystem
	private String getComputerName(){
		Map<String, String> nameParam = new HashMap<String, String>();
		nameParam.put("Class", "Win32_ComputerSystem");
		List<Map<String, String>>  nameRes = grouppolicycmdlets.getComputerName(nameParam);
		String computerName = nameRes.get(0).get("Name");
		return computerName;
	}
	
	private List<Map<String, String>> copyItem(String profileName, String dirName){
		String computerName = getComputerName();
		Map<String, String> copyItemParam = new HashMap<String, String>();
		copyItemParam.put("path", "'\\\\" + computerName + "\\c$\\temp\\"+ profileName +".pol'");
		
		dirName = dirName.toUpperCase();
		String destinationPath = "'\\\\eucsolutionad.eucsolution.com"
				+ "\\c$\\temp\\{"+dirName+"}\\DomainSysvol\\GPO\\Machine\\registry.pol'";
		copyItemParam.put("destination", destinationPath);
		return grouppolicycmdlets.copyItem(copyItemParam);
	}
	
	private List<Map<String, String>> importGPO(String gpoName){
		Map<String, String> importGpoParam = new HashMap<String, String>();
		importGpoParam.put("BackupGpoName", "'" + gpoName + "'");
		importGpoParam.put("TargetName", "'" + gpoName + "'");
		importGpoParam.put("Path", "c:\\temp\\");
		return grouppolicycmdlets.importGPO(importGpoParam);
	}
	
	public List<Map<String, String>> linkGPO(String profileName, String ouName){
		Map<String, String> linkGpoParam = new HashMap<String, String>();
		linkGpoParam.put("Name", "'" + profileName + "'");
		ouName = "'" + ouName + ",dc=eucsolution,dc=com'";
		linkGpoParam.put("Target", ouName);
		return grouppolicycmdlets.newGPLink(linkGpoParam);
	}
	
	public List<Map<String, String>> setLinkGPO(String profileName, String ouName, String order){
		Map<String, String> setLinkGpoParam = new HashMap<String, String>();
		setLinkGpoParam.put("Name", "'" + profileName + "'");
		setLinkGpoParam.put("Target", "'" + ouName + "'");
		setLinkGpoParam.put("Order", order);
		return grouppolicycmdlets.setGPLink(setLinkGpoParam);
	}
	
	public boolean policyProcess(String profileName){
		//copy GPO	
		//TODO error
		
		List<Map<String, String>> copyRes = copyGPO(profileName);
		if( copyRes==null ){
			return false;
		}
		
		//get GOP
		List<Map<String, String>> getRes = getGPO(profileName);
		if( getRes==null ){
			return false;
		}
		
		//backup
		List<Map<String, String>> backupRes = backupGPO(profileName);
		if( backupRes==null ){
			return false;
		}
		String dirName = backupRes.get(0).get("Id");
		
		//copy
		List<Map<String, String>> copyItem_Res = copyItem(profileName, dirName);
		if( copyItem_Res==null ){
			return false;
		}
		
		//gpoService.importGPO();
		List<Map<String, String>> importRes = importGPO(profileName);
		if( importRes==null ){
			return false;
		}
		
		return true;
	}
	
}
