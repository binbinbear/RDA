package com.vmware.eucenablement.jTable;

import com.google.gson.Gson;


public class JTableResponse {

	public static final JTableResponse RESULT_OK = new JTableResponse();

	public String Result = "OK";
	public String Message;

	JTableResponse() {
		
	}
	
	public static JTableResponse error(String msg) {
		JTableResponse ret = new JTableResponse();
		ret.Result = "ERROR";
		ret.Message = msg;
		return ret;
	}
	
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
