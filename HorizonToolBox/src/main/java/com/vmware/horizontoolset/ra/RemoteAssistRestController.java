package com.vmware.horizontoolset.ra;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.operator.Session;
import com.vmware.horizontoolset.viewapi.operator.ViewOperator;
import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.util.TaskModuleUtil;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;

@RestController
public class RemoteAssistRestController {

	private static Logger log = Logger.getLogger(RemoteAssistRestController.class);
	
    @RequestMapping(value="/remoteassist/list", method=RequestMethod.GET)
    public synchronized String list() {
        return getTableHtml();
    }
    
    @RequestMapping(value = "/remoteassist/download/{file_id}", method = RequestMethod.GET)
    public void download(HttpSession session,
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
			
			String userName = SessionUtil.getuser(session);
			log.info("[Remote Assistance] User: [" + userName + "] is launching invitation: " + inv);
			
	        //response.setContentType("application/octet-stream; name=\"Invitation.HorizonRemoteAssistance\"");
			response.setContentType("application/octet-stream; name=\"Invitation.msrcIncident\"");
	        //response.setHeader("Content-disposition","attachment; filename=\"Invitation.HorizonRemoteAssistance\"");
			response.setHeader("Content-disposition","attachment; filename=\"Invitation.msrcIncident\"");
			
	        //String content = JsonUtil.javaToJson(inv.inv);
			String content = inv.inv;
	        byte[] bytes = content.getBytes("UTF-8");
	        
	        response.setCharacterEncoding("UTF-8");
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
			
			Set<String> alreadyRequested = new HashSet<>();
			
			for (int i = 0; i < invs.size(); i++) {
				HraInvitation inv = invs.get(i);
				String type = (i % 2 == 0) ? "" : " class='tr_even'";
				String status;
				String action = "";
				String uid = inv.machine + inv.user;
				if (inv.started) {
					alreadyRequested.add(uid);
					status = "Processed";
				} else if (inv.isTimeout())
					status = "Stale";
				else if (alreadyRequested.contains(uid))
					status = "Re-submitted";
				else {
					alreadyRequested.add(uid);
					status = "Waiting";
					action = "<a href='remoteassist/download/" + inv.id + "'><img src='img/start.png' class='start_icon'> Start Assist</a>";
				}
				html.append("<tr").append(type)
					.append("><td>").append(inv.getStaleDisplay())
					.append("</td><td>").append(inv.getUserNameDisplay())
					.append("</td><td>").append(inv.machine)
					.append("</td><td>").append(inv.os)
					.append("</td><td>").append(status)
					.append("</td><td>").append(action)
					.append("</td></tr>\n");
			}
		}
		
		return html.toString();
//		HtmlTemplate tmpl = HtmlTemplate.load(HraServlet.class, "RAList.template");
//		tmpl.replace("TABLE_ROWS", html.toString());
//		return tmpl.toString();
	}

    @RequestMapping(value = "/remoteassist/shadow/{session_id}", method = RequestMethod.GET)
    public String shadow(HttpSession session,
        @PathVariable("session_id") String sessionId, 
        HttpServletResponse response) {
    	
    	log.info("eneter shadow!! session: " + sessionId);
    	
    	String sessionIdDecoded = DecodeSessionId(sessionId);
    	log.info("Decoded sessionId: " + sessionIdDecoded);
    	
    	
    	Session ss = null;
    	ViewOperator vop = SessionUtil.getViewOperator(session);
    	ss = vop.getSessionById(sessionIdDecoded);
    	
		
		if (ss == null)
			log.error("{ret: false, msg: 'No such session.'}");
		else
			log.info("{ret: true, msg: '" + ss.getUserName() + "'}");
			
		TaskModuleUtil moduleUtil = new TaskModuleUtil();
		
		Credential cred = moduleUtil.getLoginInfo(session);
		log.info(cred.getUsername() + ", " + cred.getPassword() + ", " + cred.getDomain());
		
    	HAUnsolicited ra = new HAUnsolicited("C:\\ra\\Change4User.exe", cred.getUsername(),
    			cred.getPassword(), cred.getDomain(), ss.getMachine(false).getDnsname());
    	
    	if(ra.CreateRATicket()) {
    		try {
        		Gson gson = new Gson();
        		HraInvitation invitation = gson.fromJson(ra.ticketContent, HraInvitation.class);
        		HraManager.add(invitation);
        		
        		String action = "<a id=\"ticketlink\" href='remoteassist/download/" + invitation.id + "'><img src='img/start.png' class='start_icon'> Start Assist</a>";
        		
        		return action;
    		} catch (Exception e) {
    			log.error("Fail analyze RA request", e);
    		}	
    	}
    	
    	
    	return ra.retDescription;
    	
    }
    
	@RequestMapping(value = "/remoteassist/sessions", method = RequestMethod.GET)
    public String sessions(HttpSession session, 
        HttpServletResponse response) {
    	
		log.error("get session list");
		ViewOperator vop = SessionUtil.getViewOperator(session);
		log.error("get session list2");

		//List<Session> sessions = vop.activeSessions.get();
		List<Session> sessions = vop.activeSessions.get();
		log.error("get session list done, count is " + sessions.size());
		
		StringBuilder html = new StringBuilder();

		if (sessions.isEmpty()) {
			html.append("<tr><td colspan=20><i>No active session.</i></td></tr>");
		} else {
			//<thead><tr><th>User</th><th>Machine name</th><th>Desktop pool</th><th>Type</th><th>OS Version</th><th>Action</th></tr></thead>
			for (int i = 0; i < sessions.size(); i++) {
				Session ss = sessions.get(i);
				if(ss.getType().compareToIgnoreCase("DESKTOP") != 0)
					continue;
				
				log.error("id: " + ss.getId());
				String action = "<a class='radesktopsession' href='remoteassist/shadow/" + EncodeSessionId(ss.getId()) + "'>shadow</a>";
				String type = (i % 2 == 0) ? "" : " class='tr_even'";
				html.append("<tr").append(type)
					.append("><td>").append(ss.getUserName())
					.append("</td><td>").append(ss.getMachineName())
					.append("</td><td>").append(ss.getDesktopPoolName())
					.append("</td><td>").append(ss.getType())
					.append("</td><td>").append("TODO")
					.append("</td><td>").append(action)
					.append("</td></tr>\n");
			}
		}
		
		List<SessionLocalSummaryView> views = vop.SessionLocalSummaryViews.get();
		for(int i = 0; i < views.size(); i++) {
			SessionLocalSummaryView v = views.get(i);
			log.info("session state:" + v.sessionData.getSessionState() + ", type: " + v.sessionData.sessionType + ", machine: " + v.namesData.machineOrRDSServerName);
		}
		
		
		return html.toString();
    }	
	
	private static String EncodeSessionId(String sessionId) {
		
		StringBuffer sessionIdEncoded = new StringBuffer();
		 
	    for (int i = 0; i < sessionId.length(); i++) {
	 
	        char c = sessionId.charAt(i);
	 
	        sessionIdEncoded.append("u" + Integer.toHexString(c));
	    }
	 
	    return sessionIdEncoded.toString();
	}
	
	private static String DecodeSessionId(String sessionIdEncoded) {
		
		  StringBuffer string = new StringBuffer();
			 
		    String[] hex = sessionIdEncoded.split("u");
		 
		    for (int i = 1; i < hex.length; i++) {
		 
		        int data = Integer.parseInt(hex[i], 16);
		 
		        string.append((char) data);
		    }
		 
		    return string.toString();
	}
}
