package com.vmware.eucenablement.util;

import java.io.File;

public class WindowsDirUtil {

	public String getUserHome() {
		String path = System.getProperty("user.home", null);
		
		if (path != null && !new File(path).exists())
			return path;
		
		path = System.getProperty("HOMEPATH", null);
		
		if (path != null && !new File(path).exists())
			return path;

		String user = System.getProperty("username", null);
		if (user != null) {
			return "/Users/" + user;
		}
		
		return null;
	}

	public String getLocalAppDataPath() {
		return System.getProperty("LOCALAPPDATA", null);
	}
}
