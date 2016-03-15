package com.vmware.horizontoolset.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtil {
	private static final String webapprootkey = "horizontoolset.root";

	public static String getWebAppRoot(){
		return  System.getProperty(webapprootkey);
	}
	public static String getTempFolder(){
		String temp = System.getProperty(webapprootkey)+ "/../../temp/";
		File f = new File(temp);
		if (!f.isDirectory()){
			return null;
		}
		return temp;
	}
	private static Logger _log = Logger.getLogger(FileUtil.class);

	public static String getResourceContent(String path) throws FileNotFoundException{


		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		StringBuffer buffer = new StringBuffer("");
        try {
        	while((line = reader.readLine())!=null){
        		buffer.append(line + "\n");
        	}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_log.error(e.getMessage(),e);
		}

		return new String(buffer);
	}


}
