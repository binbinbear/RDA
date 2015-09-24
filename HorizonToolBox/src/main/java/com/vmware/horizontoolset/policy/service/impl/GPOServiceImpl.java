package com.vmware.horizontoolset.policy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.policy.gpo.Grouppolicycmdlets;
import com.vmware.horizontoolset.policy.service.GPOService;
	
public class GPOServiceImpl implements GPOService{
	private Grouppolicycmdlets grouppolicycmdlets;
	
	private static Logger log = Logger.getLogger(GPOServiceImpl.class);
	private String domain;
	private String domainFullName;
	public GPOServiceImpl(String ad_user, String ad_pass, String ad_computerName, String domain, String domainfullname){
		ad_user = domain+"\\"+ad_user;
		this.domain = domain;
		
		/*		
		grouppolicycmdlets = new Grouppolicycmdlets("eucsolutionad.eucsolution.com",
													"eucsolution\\administrator",
													"VMware123");
		*/
		this.domainFullName = domainfullname;
		grouppolicycmdlets = new Grouppolicycmdlets(ad_computerName,ad_user,ad_pass);
		log.debug("[DEBUG ] init GPOServiceImpl over");
	}
	
	public void checkDir(){
		//testPath();
		makeDir();
	}
	
	public List<Map<String, String>> getGPO(String gpoName){
		Map<String,String> getGpoParam = new HashMap<String,String>();
		getGpoParam.put("Name", "'" + gpoName + "'");
		return grouppolicycmdlets.getGPO(getGpoParam);
	}
	
	private List<Map<String, String>> getAllGPO(){
		Map<String,String> getAllGpoParam = new HashMap<String,String>();
		getAllGpoParam.put("All", "");
		
		//getAllGpoParam.put("Domain", "eucsolution.com");	//get domain
		//TODO FIXME: domainFullName should not require user to input
		getAllGpoParam.put("Domain", this.domainFullName);
		
		return grouppolicycmdlets.getAllGPO(getAllGpoParam);
	}
	
	public Map<String, String> getNameList(){		//cache in ldap
		log.debug("[POWERSHELL] getNameList");
		Map<String,String> nameList = new HashMap<String,String>();
		List<Map<String, String>> psRes = getAllGPO();
		log.debug("[POWERSHELL] psRes="+psRes.toString());
		for(int i=0; i<psRes.size(); ++i){
			Map<String,String> gpoItem = psRes.get(i);
			String name = gpoItem.get("DisplayName").toString();
			String desc = "";
			nameList.put(name,desc);
		}
		return nameList;
	} 
	
	public boolean profileNameExist(String profileName){
		Map<String, String> proNames = getNameList();
		if( null==proNames ){
			return false;
		}
		if( proNames.containsKey(profileName) ){
			return true;	// name exist
		}
		return false;
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
	
	private List<Map<String, String>> newGPO(String gpoName, String desc){
		Map<String,String> newGpoParam = new HashMap<String,String>();
		newGpoParam.put("Name", "'" + gpoName + "'");	
		newGpoParam.put("comment", "'" + desc + "'");
		if(grouppolicycmdlets.getGPO(newGpoParam).size()==0){
			return grouppolicycmdlets.newGPO(newGpoParam);
		}
		return null;
	}
	
	private List<Map<String, String>> backupGPO(String gpoName){
		Map<String,String>  backupGpoParam = new HashMap<String, String>();
		backupGpoParam.put("Name", "'" + gpoName + "'");
		
		//add by wx 9-15
	    String sysDriver = System.getProperty("user.home");
	    sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		backupGpoParam.put("Path", sysDriver + ":\\temp\\");
		
		//backupGpoParam.put("Path", "c:\\temp\\");
		return grouppolicycmdlets.backupGPO(backupGpoParam);
	}
	
	public List<Map<String, String>> removeGPO(String gpoName){
		Map<String,String> newGpoParam = new HashMap<String,String>();
		newGpoParam.put("Name", "'" + gpoName + "'");
		if(grouppolicycmdlets.getGPO(newGpoParam).size()!=0){
			return grouppolicycmdlets.removeGPO(newGpoParam);
		}
		return null;
	}
	
	//Get-WmiObject -Class Win32_ComputerSystem
	private String getComputerName(){
		Map<String, String> nameParam = new HashMap<String, String>();
		nameParam.put("Class", "Win32_ComputerSystem");
		List<Map<String, String>>  nameRes = grouppolicycmdlets.getComputerName(nameParam);
		String computerName = nameRes.get(0).get("Name");
		return computerName;
	}
	
	private String getPolPath(String profileName){
		String computerName = getComputerName();
		String sourcePath = PolFileServiceImpl.polPath;
		sourcePath = sourcePath.replaceFirst("C:", "");
		sourcePath = "'//"+ computerName + "/C$" + sourcePath +profileName + ".pol'";
		return sourcePath;
	}
	
	private List<Map<String, String>> copyItem(String profileName, String dirName){
		Map<String, String> copyItemParam = new HashMap<String, String>();
		copyItemParam.put("path", getPolPath(profileName));
		dirName = dirName.toUpperCase();
/*		String destinationPath = "'\\\\eucsolutionad.eucsolution.com"
				+ "\\c$\\temp\\{"+dirName+"}\\DomainSysvol\\GPO\\Machine\\registry.pol'";*/
		String destinationPath = "'\\\\"+grouppolicycmdlets.getAd()
				+ "\\c$\\temp\\{"+dirName+"}\\DomainSysvol\\GPO\\Machine\\registry.pol'";
		copyItemParam.put("destination", destinationPath);
		return grouppolicycmdlets.copyItem(copyItemParam);
	}
	
	public List<Map<String, String>> testPath(){
		Map<String, String> testPathParam = new HashMap<String, String>();
		//testPathParam.put("Path", "'\\\\eucsolutionad.eucsolution.com\\c$\\temp'");	
		testPathParam.put("Path", "'\\\\"+ grouppolicycmdlets.getAd() +"\\c$\\temp'");	
		List<Map<String, String>> res = grouppolicycmdlets.testPath(testPathParam);
		log.debug("[DEBUG ] [testPath]"+res.toString());
		for(Map<String, String> item : res){
			for(Map.Entry<String, String> mapEntry : item.entrySet()){
				log.debug("[DEBUG ] [res] " + mapEntry.getKey() + ", " + mapEntry.getValue());
			}
		}
		return res;
	}
	
	public List<Map<String, String>> makeDir(){
		Map<String, String> makeDirParam = new HashMap<String, String>();	
		makeDirParam.put("Path", "'\\\\"+ grouppolicycmdlets.getAd() +"\\c$\\temp'");
		makeDirParam.put("type", "directory");
		//TODO: FIXME  DO NOT USE absolute path C:\TEMP  
		//TODO: FIXME: THE FOLDER MAY BE ALREAD there
		try{
			List<Map<String, String>> res = grouppolicycmdlets.makeDir(makeDirParam);
			log.debug("[DEBUG ] [makeDir]"+res.toString());
			return res;
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
			return null;
		}


	}
	
	private List<Map<String, String>> importGPO(String gpoName){
		Map<String, String> importGpoParam = new HashMap<String, String>();
		importGpoParam.put("BackupGpoName", "'" + gpoName + "'");
		importGpoParam.put("TargetName", "'" + gpoName + "'");
		//add by wx 9-15
	    String sysDriver = System.getProperty("user.home");
	    sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		importGpoParam.put("Path", sysDriver + ":\\temp\\");
		
		//importGpoParam.put("Path", "c:\\temp\\");
		return grouppolicycmdlets.importGPO(importGpoParam);
	}
	
	public List<Map<String, String>> linkGPO(String profileName, String ouName){
		Map<String, String> linkGpoParam = new HashMap<String, String>();
		linkGpoParam.put("Name", "'" + profileName + "'");
		//ouName = "'" + ouName + ",dc=" + this.domain + ",dc=com'";
		ouName = "'" + ouName + ",dc=" + this.domain + ",dc=fvt'";
		linkGpoParam.put("Target", ouName);
		return grouppolicycmdlets.newGPLink(linkGpoParam);
	}
	
	public List<Map<String, String>> setLinkGPO(String profileName, String ouName, String order){
		Map<String, String> setLinkGpoParam = new HashMap<String, String>();
		setLinkGpoParam.put("Name", "'" + profileName + "'");
		String domainStr = this.domainFullName;
		setLinkGpoParam.put("Domain", "'" + domainStr + "'");
		setLinkGpoParam.put("Target", "'" + ouName + "'");
		setLinkGpoParam.put("Order", order);
		return grouppolicycmdlets.setGPLink(setLinkGpoParam);
	}
	
	public List<Map<String, String>> removeLinkGPO(String profileName, String ouName){
		Map<String, String> removeLinkGpoParam = new HashMap<String, String>();
		removeLinkGpoParam.put("Name", "'" + profileName + "'");
		//ouName = "'" + ouName + ",dc=" + this.domain + ",dc=com'";
		ouName = "'" + ouName + ",dc=" + this.domain + ",dc=fvt'";
		removeLinkGpoParam.put("Target", ouName);
		return grouppolicycmdlets.removeGPLink(removeLinkGpoParam);
	}
	
	public List<Map<String, String>> removeBackup(String dirName){
		Map<String, String> removeBackupParam = new HashMap<String, String>();
		
		
		dirName = dirName.toUpperCase();
		String destinationPath = "'\\\\"+grouppolicycmdlets.getAd()
						+ "\\c$\\temp\\{"+dirName+"}'";
		removeBackupParam.put("Path", destinationPath);
		removeBackupParam.put("recurse", "");
		removeBackupParam.put("force", "");
		return grouppolicycmdlets.removeItem(removeBackupParam);
	}
	
	private boolean policyCommonProcess(String profileName){		
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
			
 		removeBackup(dirName);
//		PolFileService.deletePolFile(profileName);
		
		return true;
	}
	
	public boolean policyNewProcess(String profileName){
		//copy GPO	
		List<Map<String, String>> copyRes = copyGPO(profileName);
		if( copyRes==null ){
			return false;
		}
		
		return policyCommonProcess(profileName);
	}
	
	public boolean policyEidtProcess(String profileName){
		return policyCommonProcess(profileName);
	}
	
}
