package com.vmware.horizontoolset.ceip;

public class ClientOS {
	private static String[] osnames = {"OS X","Windows 7", "Windows 8", "Windows XP", "iPhone", "Android", "Windows 10"};

	public static String getOS(String os){
		os = os.toLowerCase();
		for (String osName: osnames){
			if (os.startsWith(osName.toLowerCase())){
				return osName;
			}
		}
		return os;
	}
}
