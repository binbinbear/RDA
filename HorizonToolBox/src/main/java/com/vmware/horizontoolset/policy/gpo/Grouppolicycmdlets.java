package com.vmware.horizontoolset.policy.gpo;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;


public class Grouppolicycmdlets {
	private static Logger log = Logger.getLogger(Grouppolicycmdlets.class);
	
	private String aduser;
	private String adpass;
	private String ad;
	String csvFile;
	String psCsvFile;
	private ReadCsvFunc readAll;
	private ReadCsvFunc readAllGpo;

	public Grouppolicycmdlets (String ad, String aduser, String adpass) {
		this.ad=ad;
		this.aduser = aduser;
		this.adpass = adpass;
		
		mkdirTemp();
		
		Random random = new Random();
		StringBuffer csvName = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
			csvName.append((char) (choice + random.nextInt(26)));
		}
		csvFile = csvName.toString() + ".csv";
		psCsvFile = "c:\\\\temp\\\\" + csvFile;
		readAll = new CsvFuncReadall();
		readAllGpo = new CsvFuncReadallgpo();
	}
	
	private void mkdirTemp(){
		File file =new File("c:\\\\temp");    
		if  (!file .exists()  && !file .isDirectory())      
		{         
			log.debug("[DEBUG lxy] create c:\\temp");
		    file .mkdir();    
		} else   
		{  
		    log.debug("[DEBUG lxy] c:\\temp already exists");
		}  
	}
	
	public String getAd(){
		return ad;
	}
	
	public String getAdUser(){
		return aduser;
	}
	
	public List<Map<String, String>> getGPO(Map<String, String> map){
		return runPowerShell_("Get-GPO", map, readAll);
	}
	
	public List<Map<String, String>> getAllGPO(Map<String, String> map){
		return runPowerShell_("Get-GPO", map, readAllGpo);
	}

	public List<Map<String, String>> backupGPO(Map<String, String> map){
		return runPowerShell_("Backup-GPO", map, readAll);
	}

	public List<Map<String, String>> importGPO(Map<String, String> map){
		return runPowerShell_("Import-GPO", map, readAll);
	}
	
	public List<Map<String, String>> removeGPO(Map<String, String> map){
		return runPowerShell_("Remove-GPO", map, readAll);
	}

	public List<Map<String, String>> newGPO(Map<String, String> map){
		return runPowerShell_("New-GPO", map, readAll);
	}

	public List<Map<String, String>> newGPLink(Map<String, String> map){
		return runPowerShell_("New-GPLink", map, readAll);
	}

	public List<Map<String, String>> setGPLink(Map<String, String> map){
		return runPowerShell_("Set-GPLink", map, readAll);
	}

	public List<Map<String, String>> removeGPLink(Map<String, String> map){
		return runPowerShell_("Remove-GPLink", map, readAll);
	}
	
	public List<Map<String, String>> getTest(Map<String, String> map){
		return runPowerShell_("Get-Host", map, readAll);
	}
	
	public List<Map<String, String>> getComputerName(Map<String, String> map){
		return runPowerShellDirect("Get-WmiObject", map);
	}

	public List<Map<String, String>> copyItem(Map<String, String> map){
		return runPowerShell_("Copy-Item", map, readAll);
	}
	
	public List<Map<String, String>> removeItem(Map<String, String> map){
		return runPowerShell_("Remove-Item", map, readAll);
	}
	
	public List<Map<String, String>> testPath(Map<String, String> map){
		return runPowerShell_("Test-Path", map, readAll);
	}
	
	public List<Map<String, String>> makeDir(Map<String, String> map){
		return runPowerShell_("New-Item", map, readAll);
	}
	
	public List<Map<String, String>> copyGPO(Map<String, String> map){
		return runPowerShell_("Copy-GPO", map, readAll);
	}
	
	private List<Map<String, String>> runPowerShellDirect(String cmd,
														  Map<String, String> map) {
		
		Iterator<String> idata = map.keySet().iterator();
		while (idata.hasNext()) {
			String name = idata.next();
			String value = map.get(name);
			cmd += " -" + name + " " + value;
	    }
	    return getPSOutput_(cmd, readAll);
	}

	private List<Map<String, String>> runPowerShell_(String cmd,
	         										 Map<String, String> map,
	         										 ReadCsvFunc func) {
		Iterator<String> idata = map.keySet().iterator();
		while (idata.hasNext()) {
			String name = idata.next();
			String value = map.get(name);
			cmd += " -" + name + " " + value;
		}
		String pSCredential = "$Username = \'"
	    		  + this.aduser
	    		  + "\';$Password = \'"
	    		  + this.adpass
	    		  + "\';$pass = ConvertTo-SecureString -AsPlainText $Password -Force;"
	    		  + "$Cred = New-Object System.Management.Automation.PSCredential -ArgumentList $Username,$pass;";
	    return getPSOutput_(pSCredential + "Invoke-Command -Credential $Cred -scriptblock {import-module grouppolicy;" + cmd + "} -computerName " + this.ad, func);
	}
	
	private List<Map<String, String>> getPSOutput_(String psGetCmd,  ReadCsvFunc func) {
		String cmd =
	            psGetCmd + " | Export-CSV " + psCsvFile + " -NoTypeInformation;$?";
		//log.debug("[DEBUG lxy] [cmd] " + cmd);
		Output o = PowerConsole2.execute(cmd);
	    String ret = o.toString();
	    log.debug("[ret] " + ret);
	    if (ret.matches(".*True.*"))
	    	;
	    List<Map<String, String>> psRes = func.operate(psCsvFile);
	    return psRes;
	}
	
}
