package com.vmware.horizontoolset.policy.model;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


public class Grouppolicycmdlets {

	private String aduser;
	private String adpass;
	private String ad;
	String csvFile;
	String psCsvFile;

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

	public List<Map<String, String>> copyItem(Map<String, String> map){
		//copy-item -path c:\temp\xxx.pol -destination \\eucsolutionad.eucsolution.com\c$\temp\xxx.pol
		return runPowerShellDirect("Copy-Item", map);
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
	      return getPSOutput(pSCredential + "Invoke-Command -scriptblock {import-module grouppolicy;" + cmd + "} -computerName " + this.ad);
	   }

	public List<Map<String, String>> getPSOutput(String psGetCmd) {
		String cmd =
	            psGetCmd + " | Export-CSV " + psCsvFile + " -NoTypeInformation;$?";
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

	public static void main(String[] args) throws IOException {
		//import-module grouppolicy
		Grouppolicycmdlets grouppolicycmdlets = new Grouppolicycmdlets("eucsolutionad.eucsolution.com",
				"eucsolution\\administrator",
				"VMware123");
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "test667");
		List<Map<String, String>> aaa = grouppolicycmdlets.newGPO(map);
		for(int i = 0; i < aaa.size(); i++){
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iter = aaa.get(i).entrySet().iterator();
			while (iter.hasNext()) {
			    Entry<String, String> entry = iter.next();
			    sb.append(entry.getKey());
			    sb.append('=').append('"');
			    sb.append(entry.getValue());
			    sb.append('"');
			    if (iter.hasNext()) {
			        sb.append(',').append(' ');
			    }
			}
			System.out.println(sb);
		}
	}
}
