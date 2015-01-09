package com.vmware.horizontoolset.policy.gpo;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.policy.gpo.model.GroupPolicyObject;


public class Grouppolicycmdlets {
	private static Logger log = Logger.getLogger(Grouppolicycmdlets.class);
	
	private String aduser;
	private String adpass;
	private String ad;
	String csvFile;
	String psCsvFile;
	private GroupPolicyObject gpo;

	public Grouppolicycmdlets (String ad, String aduser, String adpass) {
		this.ad=ad;
		this.aduser = aduser;
		this.adpass = adpass;
		Random random = new Random();
		StringBuffer csvName = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
			csvName.append((char) (choice + random.nextInt(26)));
		}
		csvFile = csvName.toString() + ".csv";
		psCsvFile = "c:\\\\temp\\\\" + csvFile;
		gpo = new GroupPolicyObject();
		//String cmd = "$net = new-object -ComObject WScript.Network;";
		//cmd += "$net.MapNetworkDrive('u:', '\\\\eucsolutionad.eucsolution.com\\c$\\temp', $false, 'eucsolution\\administrator', 'VMware123')";
		
	}

	public List<Map<String, String>> getGPO(Map<String, String> map){
		return runPowerShell("Get-GPO", map);
	}

	public List<Map<String, String>> backupGPO(Map<String, String> map){
		return runPowerShell("Backup-GPO", map);
	}

	public List<Map<String, String>> importGPO(Map<String, String> map){
		return runPowerShell("Import-GPO", map);
	}

	public List<Map<String, String>> newGPO(Map<String, String> map){
		return runPowerShell("New-GPO", map);
	}

	public List<Map<String, String>> newGPLink(Map<String, String> map){
		return runPowerShell("New-GPLink", map);
	}

	public List<Map<String, String>> setGPLink(Map<String, String> map){
		return runPowerShell("Set-GPLink", map);
	}

	public List<Map<String, String>> getTest(Map<String, String> map){
		return runPowerShell("Get-Host", map);
	}
	
	public List<Map<String, String>> getComputerName(Map<String, String> map){
		return runPowerShellDirect("Get-WmiObject", map);
	}

	public List<Map<String, String>> copyItem(Map<String, String> map){
		//copy-item -path c:\temp\xxx.pol -destination u:\xxx.pol
		return runPowerShell("Copy-Item", map);
	}
	
	public List<Map<String, String>> copyGPO(Map<String, String> map){
		return runPowerShell("Copy-GPO", map);
	}
	
	private List<Map<String, String>> runPowerShellDirect(String cmd,
			Map<String, String> map) {
		Iterator<String> idata = map.keySet().iterator();

	      while (idata.hasNext()) {
	         String name = idata.next();
	         String value = map.get(name);
	         cmd += " -" + name + " " + value;
	      }
	      return getPSOutput(cmd);
	}

	private List<Map<String, String>> runPowerShell(String cmd,
	         Map<String, String> map) {
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
	    return getPSOutput(pSCredential + "Invoke-Command -Credential $Cred -scriptblock {import-module grouppolicy;" + cmd + "} -computerName " + this.ad);
	}

	public List<Map<String, String>> getPSOutput(String psGetCmd) {
		String cmd =
	            psGetCmd + " | Export-CSV " + psCsvFile + " -NoTypeInformation;$?";
		log.debug("[DEBUG lxy] [cmd] "+cmd);
		Output o = PowerConsole2.execute(cmd);
	    String ret = o.toString();
	    if (ret.matches(".*True.*"))
	    	;
	    File file = new File(psCsvFile);
	    
	    CSVReader csvr;
	    try {
	    	csvr = new CSVReader(new FileReader(file));
	    	return csvr.readAll();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
	
	public GroupPolicyObject listMapToGPO(List<Map<String, String>> aaa){
		if(aaa.size()!=1){
			return null;
		}
		this.gpo.setUser(aaa.get(0).get("User"));
		return this.gpo;
	}
	
}
