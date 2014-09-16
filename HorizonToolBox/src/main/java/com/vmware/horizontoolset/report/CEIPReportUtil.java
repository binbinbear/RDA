package com.vmware.horizontoolset.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.ceip.ClientOS;
import com.vmware.horizontoolset.ceip.ClientVersion;
public class CEIPReportUtil {
	private static Logger log = Logger.getLogger(CEIPReportUtil.class);
	
	private static final String versionKey1 = "\"version\":\"";
	private static final String versionKey2 = "client-version\":\"";
	private static final String hostOSKey = "host-os-name\":\"";
	private static HashSet<String> cachedFiles = new HashSet<String>();
	private static ClientReport baseReport = new ClientReport();
	private static final int largestFileNum = 10000;
	
	 private static void readFromFile(File currentFile ) {
		 if (cachedFiles.contains(currentFile.getName())){
			 return;
		 }
		 cachedFiles.add(currentFile.getName());
	      FileReader fr = null;
	      BufferedReader br ;
	      try {
	         fr = new FileReader(currentFile);
	        br  = new BufferedReader(fr);
	         while (br.ready()) {
	            String line = br.readLine();
	            if (line == null || line.length() == 0){
	            	break;
	            }
	            if (line.contains(hostOSKey)){
	            	
	            	 ArrayList<String> osarray =  CEIPReportUtil.getOSs(line);
					if (osarray != null){
						for (String os: osarray){
							baseReport.addOS(ClientOS.getOS(os).toString());
						}
					}
					
					
					ArrayList<String> versionarray =  CEIPReportUtil.getVersions(line);
					if (versionarray!=null){
						for (String version: versionarray){
							baseReport.addVersion("" + ClientVersion.getVersion(version));
						}
					}
					
				}
	         }
	         log.debug("file:"+ currentFile.getName()+" versions:" + baseReport.getVersionMap().size() + " OSs:" + baseReport.getOsMap().size());
	         br.close();
	      } catch (Exception e) {
	         log.warn("Exception reading file",e);
	      }

	   }
	 
	private static void searchFolder(File folder){
		 File[] files = folder.listFiles();
         if (files==null || files.length == 0) {
        	 log.info("Folder is null:"+ folder.getAbsolutePath()+" Files:"+ files);
            return;
         }
         for (int i=0;i< files.length;i++){
        	 readFromFile(files[i]);
         }
         
	}
	
	public static void resetCount(){
		baseReport = new ClientReport();
		cachedFiles.clear();
	}
	public static ClientReport generateReport(String spoolFolder){
		if (cachedFiles.size()> largestFileNum){
			resetCount();
		}
		File cur = new File(spoolFolder + File.separator + "cur");
		File tmp = new File(spoolFolder + File.separator + "tmp");
		
		
		searchFolder(cur);
		searchFolder(tmp);
		
		baseReport.updateDate();
		return baseReport;
	}
	
	private static ArrayList<String> getAttr(String line, String key){
		ArrayList<String> results = new ArrayList<String>();
		int sindex = 0;
		while(true){
			sindex = line.indexOf(key, sindex);
			if (sindex<0){
			    break;
			}
			sindex = sindex + key.length() ;
			int eindex = line.indexOf('\"', sindex+1);
			if (eindex<0){
				break;
			}
			
			results.add( line.substring(sindex, eindex));
			sindex = eindex+1;
		}
		return results;
	}
	
	private static  ArrayList<String> getVersions(String line){
		 ArrayList<String> list1 = getAttr(line, versionKey1);
		 ArrayList<String> list2 = getAttr(line, versionKey2);
		 list1.addAll(list2);
		 return list1;
	}

	private static  ArrayList<String> getOSs(String line){
		return getAttr(line, hostOSKey);
	}
	

	
}
