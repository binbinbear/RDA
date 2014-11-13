package com.vmware.horizontoolset.device.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.common.jtable.JTableResponse;
import com.vmware.horizontoolset.device.data.AccessLogManager;
import com.vmware.horizontoolset.device.data.AccessRecord;
import com.vmware.horizontoolset.device.data.DeviceInfo;
import com.vmware.horizontoolset.device.data.WhitelistManager;
import com.vmware.horizontoolset.device.data.WhitelistRecord;
import com.vmware.horizontoolset.device.guidata.RowData_AccessLog;
import com.vmware.horizontoolset.device.guidata.RowData_Whitelist;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;

@RestController
public class DeviceFilterRestController {

	private static Logger log = Logger.getLogger(DeviceFilterRestController.class);
	
    @RequestMapping("/deviceFilter/accessLog")
	public JTableData getAccessLog(HttpSession session) {
    	JTableData ret = new JTableData();

    	List<AccessRecord> records = AccessLogManager.list();
    	
    	ret.Records = new Object[records.size()];
    	
    	for (int i = 0; i < records.size(); i++) {
    		
    		AccessRecord r = records.get(i);
    		RowData_AccessLog d = new RowData_AccessLog(r.recordId, 
    				r.deviceInfo.ViewClient_Client_ID, 
    				r.deviceInfo.ViewClient_Type,
    				r.deviceInfo.UserName,
    				r.deviceInfo.UserDomain,
    				r.time,
    				r.result.toString());
    		
    		ret.Records[i] = d;
    	}
    	ret.TotalRecordCount = records.size();
    	
    	return ret;
    }
    
    @RequestMapping("/deviceFilter/whitelist")
	public JTableData whitelist(HttpSession session) {
    	JTableData ret = new JTableData();

    	List<WhitelistRecord> records = WhitelistManager.list();
    	
    	ret.Records = new Object[records.size()];
    	
    	for (int i = 0; i < records.size(); i++) {
    		
    		WhitelistRecord r = records.get(i);
    		RowData_Whitelist d = new RowData_Whitelist(r.recordId, 
    				r.deviceInfo.ViewClient_Client_ID, 
    				r.deviceInfo.ViewClient_Type,
    				r.deviceInfo.UserName,
    				r.deviceInfo.UserDomain,
    				r.lastAccessTime);
    		
    		ret.Records[i] = d;
    	}
    	ret.TotalRecordCount = records.size();
    	
    	return ret;
    }
    
    @RequestMapping("/deviceFilter/delete")
	public JTableResponse delete(HttpSession session,
			@RequestParam(value="recordId", required=true) String recordIdString) {
    	
    	JTableResponse resp;
    	
    	try {
    		long recordId = Long.parseLong(recordIdString);
    		
    		if (WhitelistManager.delete(recordId))
    			resp = JTableResponse.RESULT_OK;
    		else
    			resp = JTableResponse.error("No such record");
    	} catch (Exception e) {
    		resp = JTableResponse.error("Exception: " + e);
    	}

    	return resp;
    }
    /*
    @RequestMapping("/deviceFilter/addToWhitelist")
	public JTableResponse addToWhitelist(HttpSession session,
			@RequestParam(value="recordId", required=true) String recordIdString) {
    	
    	JTableResponse resp;
    	
    	try {
    		long recordId = Long.parseLong(recordIdString);
    		AccessRecord rec = AccessLogManager.get(recordId);
    		
    		if (rec == null)
    			resp = JTableResponse.error("No such record");
    		
    		else {
    			rec.result = AccessRecord.AccessResult.ADDED_TO_WHITELIST;
    			WhitelistManager.add(new DeviceRecord(rec.deviceInfo, rec.time));
    			resp = JTableResponse.RESULT_OK;
    		}
    		
    	} catch (Exception e) {
    		resp = JTableResponse.error("Exception: " + e);
    	}

    	return resp;
    }
    */
    @RequestMapping("/deviceFilter/check")
	public String check(HttpSession session,
			@RequestParam(value="di", required=true) String diJson) {
    	
    	log.debug("deviceFilter/check: " + diJson);
    	
    	Gson gson = new Gson();
    	
    	String resp;
    	
    	try {
    		DeviceInfo di = gson.fromJson(diJson, DeviceInfo.class);

    		if (di.isValid()) {
	    		boolean isAllowed = AccessLogManager.log(session, di);
	    		
	    		if (isAllowed)
	    			resp = "ACCESS_ALLOWED";
	    		else
	    			resp = "ACCESS_DENIED";
    		} else {
    			resp = "ERROR: Invalid device info.";
    		}
    		
    	} catch (Exception e) {
    		resp = "ERROR: " + e.toString();
    		log.warn("Error checking device", e);
    	}
    	return resp;
    }
        
    @RequestMapping("/deviceFilter/test2")
	public String test2(HttpSession session) {
    	
    	try {
	       	LDAP ldap = SessionUtil.getLDAP(session);
	       	String key = "toolbox";
	       	int n = ldap.getInt(key, 0) + 1;
	       	ldap.setAttribute(key, String.valueOf(n));
	       	
	       	return String.valueOf(n);
    	} catch (Exception e) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		e.printStackTrace(pw);
    		pw.close();
    		return sw.toString();
    	}
    }
}

