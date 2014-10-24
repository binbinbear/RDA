package com.vmware.horizontoolset.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static String getHTML(String urlToRead) {

		HttpURLConnection conn;
		try {
			URL url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		try (InputStream in = conn.getInputStream();
				InputStreamReader ir = new InputStreamReader(in);
				BufferedReader rd = new BufferedReader(ir);
				) {
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
