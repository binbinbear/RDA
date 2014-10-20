package com.vmware.eucenablement.util;

import com.google.gson.Gson;

public class JsonReturn {
		
		String err;
		String content;
		
		public static String createError(String s) {
			JsonReturn r = new JsonReturn();
			r.err = s;
			return r.toString();
		}
		
		public static String createContent(String s) {
			JsonReturn r = new JsonReturn();
			r.content = s;
			return r.toString();
		}
		
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
	}