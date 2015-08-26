package com.vmware.horizontoolset.util;

import java.io.File;

public class FileUtil {
	private static final String webapprootkey = "horizontoolset.root";
	public static String getTempFolder(){
		String temp = System.getProperty(webapprootkey)+ "/../../temp/";
		File f = new File(temp);
		if (!f.isDirectory()){
			return null;
		}
		return temp;
	}

}
