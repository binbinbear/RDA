package com.vmware.horizontoolset.ra;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class RemoteAssistRestController {

	private static Logger log = Logger.getLogger(RemoteAssistRestController.class);
	
    @RequestMapping(value="/remoteassist/list", method=RequestMethod.GET)
    public synchronized String list() {
        return getTableHtml();
    }
    
    @RequestMapping(value = "/remoteassist/download/{file_id}", method = RequestMethod.GET)
    public void download(
        @PathVariable("file_id") String fileId, 
        HttpServletResponse response) {
    	
		int n = -1;
		try {
			n = Integer.parseInt(fileId);
		} catch (Exception e) {
		}

		try {
			HraInvitation inv = HraManager.get(n);
			if (inv == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			log.info("Downloading invitation: " + inv);
			
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-disposition","attachment; filename=\"Invitation.HorizonRemoteAssistance\"");
	        
	        String content = inv.getInvContent();
	        byte[] bytes = content.getBytes("UTF-8");
	        
	        response.setContentLength(bytes.length);
	        OutputStream out = response.getOutputStream();
	        out.write(bytes);
	        out.flush();
		} catch (Exception e) {
			log.warn("Error downloading ticket.", e);
		}
    }
    
    @RequestMapping(value = "/remoteassist/upload", method = RequestMethod.GET)
    public String upload(HttpSession session,
			@RequestParam(value="inv", required=true) String inv) {
    	
    	try {
    		log.info("Request ticket: " + inv);
    		Gson gson = new Gson();
    		HraInvitation invitation = gson.fromJson(inv, HraInvitation.class);
    		HraManager.add(invitation);
			return "OK";
		} catch (Exception e) {
			log.error("Fail receiving RA request", e);
			return "Error: " + e;
		}	
    }
    
	private static String getTableHtml() {
		
		StringBuilder html = new StringBuilder();

		List<HraInvitation> invs = HraManager.list();
		if (invs.isEmpty()) {
			html.append("<tr><td colspan=20><i>No remote assistance request</i></td></tr>");
		} else {
			for (int i = 0; i < invs.size(); i++) {
				HraInvitation inv = invs.get(i);
				String type = (i % 2 == 0) ? "" : " class='tr_even'";
				String action;
				if (inv.started)
					action = "Processed";
				else if (inv.isTimeout())
					action = "Stale";
				else
					//action = "<a href='javascript:launchRA(" + inv.id + ")'><img src='img/start.png' class='start_icon'> Start Assist</a>";
					action = "<a href='remoteassist/download/" + inv.id + "'><img src='img/start.png' class='start_icon'> Start Assist</a>";
				html.append("<tr").append(type)
					.append("><td>").append(inv.getStaleDisplay())
					.append("</td><td>").append(inv.getUserNameDisplay())
					.append("</td><td>").append(inv.machine)
					.append("</td><td>").append(inv.os)
					.append("</td><td>").append(action)
					.append("</td></tr>\n");
			}
		}
		
		return html.toString();
//		HtmlTemplate tmpl = HtmlTemplate.load(HraServlet.class, "RAList.template");
//		tmpl.replace("TABLE_ROWS", html.toString());
//		return tmpl.toString();
	}
}