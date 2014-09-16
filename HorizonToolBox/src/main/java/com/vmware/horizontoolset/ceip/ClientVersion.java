package com.vmware.horizontoolset.ceip;

public class ClientVersion {
	
	public static double getVersion(String version){
		double subversion =0;
		try{
			subversion = Double.parseDouble( version.substring(0, 3));
		}catch(Exception ex){
			subversion = 0;
		}
		return subversion;
	}
	

	
}
