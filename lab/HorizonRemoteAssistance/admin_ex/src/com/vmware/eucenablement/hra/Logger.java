package com.vmware.eucenablement.hra;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class Logger {

	private static final String fileName;
	
	static {
		String path = System.getProperty("user.home", null);
		
		if (path == null || !new File(path).exists()) {
			
		}
		
		path = System.getProperty("HOMEPATH", null);
		//if the path does not exist, try with default user temp
		if (path == null || !new File(path).exists()) {
			String user = System.getProperty("username", null);
			if (user != null) {
				path = "/Users/" + user + "/AppData/Local/Temp";
			}
		}
		
		if (path != null && new File(path).exists()) {
			fileName = path + "/hra.log";
			File logFile = new File(fileName);
			if (logFile.exists())
				logFile.delete();
		} else {
			fileName = null;
		}
	}
	
	public static void log(String msg) {
		
		if (fileName == null)
			return;
		
		try (FileWriter fw = new FileWriter(fileName, true);
				PrintWriter pw = new PrintWriter(fw, true)) {
			pw.println(new Date().toString() + " - " + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();
		log(sw.toString());
	}
}
