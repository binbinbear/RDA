package com.vmware.eucenablement.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class IoUtil {

	public static String readTextFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		
		File f = new File(fileName);
		if (!f.exists())
			return null;
		
		try (FileReader fr = new FileReader(fileName);
				BufferedReader br = new BufferedReader(fr)) {
			while (true) {
				String s = br.readLine();
				
				if (s == null)
					break;
				
				sb.append(s).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return sb.toString();
	}
	
	/**
	 * The input reader will be closed.
	 * 
	 * @param rdr
	 * @param file
	 * @return
	 */
	public static boolean transfer(Reader rdr, String file) {
		BufferedReader br;
		
		if (rdr instanceof BufferedReader) {
			br = (BufferedReader) rdr;
		} else {
			br = new BufferedReader(rdr);
		}

		try (FileWriter fw = new FileWriter(file);
				PrintWriter pw = new PrintWriter(fw)){
			String line;
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}
}
