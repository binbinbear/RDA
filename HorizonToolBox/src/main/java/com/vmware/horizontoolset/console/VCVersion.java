package com.vmware.horizontoolset.console;

import org.apache.log4j.Logger;

import com.vmware.vim25.AboutInfo;

public class VCVersion {
	private static Logger log = Logger.getLogger(VCVersion.class);
	
	int major;
	int minor;
	long build;
	String versionString;
	String osType;
	public VCVersion(AboutInfo aboutInfo){
		try {
			versionString = aboutInfo.version;
			osType = aboutInfo.osType;
			build = Long.parseLong(aboutInfo.getBuild());
			
			String[] tmp = versionString.split("\\.");
			
			major = Integer.valueOf(tmp[0]);
			minor = Integer.valueOf(tmp[1]);
			
		} catch (NumberFormatException e) {
			log.error("Error parsing version info. New version string? version=" + aboutInfo.version 
					+ ", build=" + aboutInfo.build
					+ ", osType=" + aboutInfo.osType, e);
			major = 0;
			minor = 0;
			build = 0;
		
		}
	}
	
	public boolean isWssSupported() {
		//return build >=  2001466;	//v5.5 update 2
		//return build >= 2183111;	//v5.5 update 2b
		
		//Wss is supported from v5.5 update 2.
		//So any build prior to this version will not have WSS supported.
		if (build < 2001466)
			return false;
		
		//also, patch build for earlier versions could have a higher build
		//number. So check version, too.
				//Also check version. 		
		return major == 5 && minor >= 5 || major > 5;
	}
	
	public boolean isNewerThanVC600() {
		return major >= 6;
	}
	
}
