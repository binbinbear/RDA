package com.vmware.horizontoolset.limit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.common.jtable.JTableResponse;
import com.vmware.horizontoolset.util.LDAP;
import com.vmware.horizontoolset.util.SessionUtil;

@RestController
public class LimitRestController {

	private static Logger log = Logger.getLogger(LimitRestController.class);
	
    @RequestMapping("/limit/list")
	public JTableData list(HttpSession session) {
    	JTableData ret = new JTableData();

    	List<AppLimitInfo> records = LimitManager.list(session);
    	ret.Records = records.toArray();
    	ret.TotalRecordCount = records.size();
    	
    	return ret;
    }
    
    @RequestMapping("/limit/update")
	public String update(HttpSession session,
			@RequestParam(value="appId", required=true) String appId,
			@RequestParam(value="limit", required=true) String numStr) {
    	
    	try {
    		int n = Integer.parseInt(numStr);
    		LimitManager.update(session, appId, n);
    		
    		return JTableResponse.RESULT_OK.toString();
    	} catch (Exception e) {
    		log.warn("Error updating app limit.", e);
    		return JTableResponse.error(e.toString()).toString();
    	}
    }
        
    @RequestMapping("/limit/test")
	public String test(HttpSession session) {
    	
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
    
    @RequestMapping("/limit/refresh")
    public void refresh() {
    	LimitManager.updateAppConcurrency(null);
    }
}

