package com.vmware.horizontoolset.ra;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vmware.horizontoolset.console.ConsoleAccessInfo;
import com.vmware.horizontoolset.console.VCVersion;
import com.vmware.horizontoolset.console.VMServiceImplVCenter;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.StringUtil;
import com.vmware.horizontoolset.viewapi.operator.Machine;
import com.vmware.horizontoolset.viewapi.operator.Session;
import com.vmware.horizontoolset.viewapi.operator.ViewOperator;
import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.util.TaskModuleUtil;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter.VirtualCenterInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineInfo;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;


@RestController
public class RemoteAssistRestController {

	private static Logger log = Logger.getLogger(RemoteAssistRestController.class);
	private static final int maxDisplayActiveSessionCount = 100;
	
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
				if((inv.machine == null) && (inv.user == null))  // filter the unsolicited RA ticket
					continue;
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
	}

    
      @RequestMapping(value = "/remoteassist/shadow/{session_id}", method = RequestMethod.GET)
    public String shadow(HttpSession session,
        @PathVariable("session_id") String sessionId, 
        HttpServletResponse response) {
    	
    	String sessionIdDecoded = DecodeSessionId(sessionId);
    	
    	Session ss = null;
    	ViewOperator vop = SessionUtil.getViewOperator(session);
    	ss = vop.getSessionById(sessionIdDecoded);
    	
		
		if (ss == null)
		{
			log.error("{ret: false, msg: 'No such session.'}");
			return "Cannot find this session.";
		}
		
		TaskModuleUtil moduleUtil = new TaskModuleUtil();
		Credential cred = moduleUtil.getLoginInfo(session);
		
		// Get the method to access this machine
		//1. IP 2. dns name for this machine
		String machineDnsNameOrIP = GetMachineIPBySession(session, ss);
		if(machineDnsNameOrIP == null) {
			machineDnsNameOrIP = ss.getMachineDNS();
			if(machineDnsNameOrIP ==  null) {
				log.error("Cannot find the machine name for this session " + sessionIdDecoded);
				return "Cannot find the related machine name for this session.";
			}
		}
		
    	HAUnsolicited ra = new HAUnsolicited("webapps\\toolbox\\static\\ra\\Change4User.exe", cred.getUsername(),
    			cred.getPassword(), cred.getDomain(), machineDnsNameOrIP);
    	
    	// create ra history
		HraHistoryItem histItem = new HraHistoryItem();
		histItem.user = ss.getUserName();
		histItem.machine = (ss.getMachineName() != null)? ss.getMachineName().toString(): "";
		histItem.desktopPool = (ss.getDesktopPoolName() != null)? ss.getDesktopPoolName().toString(): "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		histItem.startTime = df.format(new Date());
    	histItem.expert = cred.getUsername();

    	
    	if(ra.CreateRATicket()) {
    		try {
        		Gson gson = new Gson();
        		HraInvitation invitation = gson.fromJson(ra.ticketContent, HraInvitation.class);
        		HraManager.add(invitation);
        		
        		String action = "<a id=\"ticketlink\" href='remoteassist/download/" + invitation.id + "'><img src='img/start.png' class='start_icon'> Start Assist</a>";
        		histItem.actionResult = "Create RA Ticket successfully";
        		UnsolicitedRAHistoryMag.add(histItem);
        		
        		return action;
    		} catch (Exception e) {
    			log.error("Fail analyze RA request", e);
    		}	
    	}
    	
    	histItem.actionResult = "Create RA ticket failed";
		UnsolicitedRAHistoryMag.add(histItem);
		log.info("Current RA hist json: " + UnsolicitedRAHistoryMag.Transfer2Json());
		
    	return ra.retDescription;
    }
    
    
	@RequestMapping(value = "/remoteassist/sessions", method = RequestMethod.GET)
    public String sessions(HttpSession session,
    		@RequestParam(value = "key", required = false, defaultValue = "") String key,
        HttpServletResponse response) {
    	
		StringBuilder html = new StringBuilder();
		
		try
		{
			ViewOperator vop = SessionUtil.getViewOperator(session);
	
			//List<Session> sessions = vop.activeSessions.get();
			List<Session> sessions = vop.activeSessions.get(true);
			log.error("get session list done, count is " + sessions.size());
			
	
			if (sessions.isEmpty() || (sessions.size() == 0)) {
				html.append("<tr><td colspan=20><i>No active session.</i></td></tr>");
			} else {
				//<thead><tr><th>User</th><th>Machine name</th><th>Desktop pool</th><th>Type</th><th>OS Version</th><th>Action</th></tr></thead>
				int passedFilterCount = 0;
				for (int i = 0; i < sessions.size(); i++) {
					Session ss = sessions.get(i);
					
					log.info("user: " + ss.getUserName() +", machine: " + ss.getMachineName() +", desktop: " + ss.getDesktopPoolName());
					if(ss.getType().compareToIgnoreCase("DESKTOP") != 0)
						continue;
					
					boolean bPassedFilter = true;
					do
					{
						if (!StringUtil.isEmpty(ss.getUserName()) && ss.getUserName().toLowerCase().contains(key)) {
							break;
						}
						
						Object m = ss.getMachineName();
						if ((m != null) && m.toString().toLowerCase().contains(key)) {
							break;
						}
						
						Object desktop = ss.getDesktopPoolName();
						if ((desktop != null) && desktop.toString().toLowerCase().contains(key)) {
							break;
						}
						
						bPassedFilter = false;
					}while(false);
					
					if(bPassedFilter == false)
						continue;
					
					passedFilterCount++;
					if(passedFilterCount > maxDisplayActiveSessionCount) {
						break;
					}
					String encodeSessionId = EncodeSessionId(ss.getId());
					String action = "<a class='radesktopsession' href='remoteassist/shadow/" + encodeSessionId + "'><img src='img/start.png' class='start_icon'> Remote Assist</a>";
					String type = (i % 2 == 0) ? "" : " class='tr_even'";
					html.append("<tr").append(type)
						.append("><td>").append(ss.getUserName())
						.append("</td><td>").append(ss.getMachineName())
						.append("</td><td>").append(ss.getDesktopPoolName())
						.append("</td><td>").append(ss.getType())
						.append("</td><td>").append(action)
						.append("</td></tr>\n");
				}
			}
		} catch(Exception ex) {
			log.error("Get active sessions failed: " + ex.toString());
			html.append("<tr><td colspan=20><i>No active session.</i></td></tr>");
		}
		
		
		return html.toString();
    }	
	
	
	@RequestMapping(value = "/remoteassist/raHists", method = RequestMethod.GET)
    public String raHists(HttpSession session,
    		@RequestParam(value = "key", required = false, defaultValue = "") String key,
        HttpServletResponse response) {
    	
		List<HraHistoryItem> raHists = UnsolicitedRAHistoryMag.list();
		if(raHists != null)
			log.info("get unsolicited ra history list done, count is " + raHists.size());
		
		StringBuilder html = new StringBuilder();

		if (raHists.isEmpty() || (raHists.size() == 0)) {
			html.append("<tr><td colspan=20><i>No unsolicited remote assist history.</i></td></tr>");
		} else {
			for (int i = raHists.size()- 1; i >= 0; i--) {
				HraHistoryItem raHistItem = raHists.get(i);
				
				//<tr><th>User</th><th>Machine name</th><th>Desktop pool</th><th>Start Time</th><th>expert</th><th>Result</th></tr>
				
				String type = (i % 2 == 0) ? "" : " class='tr_even'";
				html.append("<tr").append(type)
					.append("><td>").append(raHistItem.user)
					.append("</td><td>").append(raHistItem.machine)
					.append("</td><td>").append(raHistItem.desktopPool)
					.append("</td><td>").append(raHistItem.startTime)
					.append("</td><td>").append(raHistItem.expert)
					.append("</td><td>").append(raHistItem.actionResult)
					.append("</td></tr>\n");
			}
		}
		
		return html.toString();
    }	
	
	private static String EncodeSessionId(String sessionId) {
		
		/*
		 
	    for (int i = 0; i < sessionId.length(); i++) {
	 
	        char c = sessionId.charAt(i);
	 
	        sessionIdEncoded.append("u" + Integer.toHexString(c));
	    }
	 
	    return sessionIdEncoded.toString();
	    */
	    
	    String ret = new sun.misc.BASE64Encoder().encode(sessionId.getBytes());
	    return ret;
	}
	
	private static String DecodeSessionId(String sessionIdEncoded) {
		
		  /*StringBuffer string = new StringBuffer();
			 
		    String[] hex = sessionIdEncoded.split("u");
		 
		    for (int i = 1; i < hex.length; i++) {
		 
		        int data = Integer.parseInt(hex[i], 16);
		 
		        string.append((char) data);
		    }
		 
		    return string.toString();
		    */
		    
	 byte[] bt = null;    
	   try {    
	       sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
	       bt = decoder.decodeBuffer( sessionIdEncoded );    
	   } catch (IOException e) {    
	       e.printStackTrace();    
	   }    
	   
	   return new String(bt);    
	}
	
	private static String GetMachineIPBySession(HttpSession session, Session ss) {
    	VCVersion vCVersion =null;
    	String vmid = ss.getMachine(true).getVmid();
    	
    	log.info("Requesting console for: " + vmid +", session: " + ss);
    	Machine m = SessionUtil.getMachine(session, vmid);
    	if (m==null){
    		log.error("Cannot get the machine");
    		return null;
    	}
    	
    	MachineInfo minfo = SessionUtil.getViewAPIService(session).getMachineInfo(m.getVmid());
    	
    	if (minfo == null){
    		log.error("Cannot get the mininfo");
    		return null;
    	}
    	try {
    		log.error("start to get vcinfo");
    		VirtualCenterInfo vcinfo =  SessionUtil.getViewAPIService(session).getVCInfo(m.getVcenterId());
    		if(vcinfo != null){
    	  		VMServiceImplVCenter vmservice = new VMServiceImplVCenter(SessionUtil.getLDAP(session).getVDIContext(),vcinfo.serverSpec.serverName,minfo.managedMachineData.getVirtualCenterData().path);
        		
        		String ip = vmservice.getVMIP();
        		if(ip != null)
        			log.info("The machine ip is " + ip);
        		return ip;
    		}
  

    	} catch (Exception e) {
    		log.warn("Failed to get VM IP address" + e.toString());
		}
        
    	return null;
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < 5; i++) {
			HraHistoryItem histItem = new HraHistoryItem();
			histItem.user = "user" + i;
			histItem.machine = "machine" + i;
			histItem.desktopPool = "pool" + i;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			histItem.startTime = df.format(new Date());
	    	histItem.expert = "expert" + i;
	    	histItem.actionResult = "result" + i;
	    	
	    	UnsolicitedRAHistoryMag.add(histItem);
		}
		
		List<HraHistoryItem> raHists = UnsolicitedRAHistoryMag.list();
		
		/*String histJson = UnsolicitedRAHistoryMag.Transfer2Json();
		//log.info("hist json: " + histJson);
		
		
		HraHistoryItem[] raHistItems = JsonUtil.jsonToJava(histJson, HraHistoryItem[].class);
		List<HraHistoryItem> raHistItemList = Arrays.asList(raHistItems); 
		for(int i = 0; i < raHistItemList.size(); i++) {
			HraHistoryItem hist = raHistItemList.get(i);
			int j = 0;
			j++
			;
			//log.info(hist.actionResult + ", " + hist.desktopPool + ", " + hist.expert + ", " + hist.machine + ", " + hist.startTime + ", " + hist.user);
		}*/
	}
}
