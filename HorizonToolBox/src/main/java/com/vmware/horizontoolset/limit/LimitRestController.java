package com.vmware.horizontoolset.limit;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.common.jtable.JTableData;
import com.vmware.horizontoolset.common.jtable.JTableResponse;
import com.vmware.vdi.vlsi.binding.vdi.util.SecureString;
import com.vmware.vim.binding.impl.vmodl.BinaryImpl;
import com.vmware.vim.binding.vmodl.Binary;

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
    		/*
	    	AdamRoleManager arm = AdamRoleManager.getInstance();
	    	VDIContext ctx = VDIContextFactory.defaultVDIContextUnpooled();
	    	int n = arm.getDefinedRoles(ctx).size();
	    	ctx.close();
	    	
	    	return "getDefinedRoles=" + n;
	    	*/
    	/*
    	
    		EventDBUtil dbu = EventDBUtil.createDefault();
    		return "" + dbu.getEvents(1, true).size();
    		*/
    		
    		//return SharedStorageAccess.defaultContextGet("LIMIT_MGR");
    		return "OK";
    		
    	} catch (Throwable t) {
    		log.error("", t);
    		return t.toString();
    	}
    	
    	
    }
    
    @RequestMapping("/limit/refresh")
    public void refresh() {
    	LimitManager.updateAppConcurrency(null, false);
    }
    
    public static void main(String[] args) {
		try {
			byte[] bytes = "asdf".getBytes("UTF-8");
	        Binary binary = new BinaryImpl(bytes);
	        SecureString ss = new SecureString(binary);

	        String s2 = new String(ss.utf8String.asArray());
	        System.out.println(s2);
	        
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

