package com.vmware.horizontoolset.check;

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.viewapi.ConnectionServer;
import com.vmware.horizontoolset.viewapi.ViewAPIService;

public class VersionChecker {
	private static Logger log = Logger.getLogger(VersionChecker.class);

	private static String[] matchedVersioins;
	public static void setMatchedVersions(String[] versions){
		VersionChecker.matchedVersioins = versions;
	}
	
	public static boolean isServerMatched(ViewAPIService api) throws VersionInCompatiableException{
		List<ConnectionServer> servers=api.getConnectionServers();
		String version;
		for (ConnectionServer server: servers){
			version = server.getVersion();
			//TODO: setup a map/set for version check
			log.info("Server version:" + version);
			boolean matched = false;
			for (int i=0;i<matchedVersioins.length;i++){
				if (version.contains(matchedVersioins[i])){
					matched = true;
					break;
				}
			}
			if (!matched){
				throw new VersionInCompatiableException(version);
			}
		}
		return true;
	}
}
