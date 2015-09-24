package com.vmware.horizontoolset.ra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * Servlet implementation class HraServlet
 */
public class HraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger log = Logger.getLogger(HraServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HraServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//download file
		String download = request.getParameter("download");
		if (download != null) {
			downloadInvitation(response, download);
			return;
		}
		
		//launch
		//*OBSOLETED*
		String id = request.getParameter("id");
		if (id != null) {
			String html = launch(id);
			response.getWriter().print(html);
			return;
		}
		
		//post invitation
		String inv = request.getParameter("inv");
		if (inv != null) {
			try {
				processInvitationUpload(inv);
				response.getWriter().print("OK");
			} catch (Exception e) {
				log.error("Fail receiving RA request", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}	
			return;
		}
			
		//test function
		String test = request.getParameter("test");
		if (test != null) {
			String ret = test(test);
			response.getWriter().print(ret);
			return;
		}
		
		response.getWriter().print(showList());
	}
	
	private void downloadInvitation(HttpServletResponse response, String id) throws IOException {

		int n = -1;
		try {
			n = Integer.parseInt(id);
		} catch (Exception e) {
		}
		
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
        
        
	}

	private String test(String value) {
		ProcessBuilder pb = new ProcessBuilder();
		//add by wx 9-15
	    String sysDriver = System.getProperty("user.home");
	    sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		pb.command(sysDriver + ":\\RuntimeDumper.exe", value);
		//pb.command("c:\\RuntimeDumper.exe", value);
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.INHERIT);
		
		try {
			pb.start();
			return "OK";
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
	}
	
	private void processInvitationUpload(String inv) {
		log.info("Request ticket: " + inv);
		Gson gson = new Gson();
		HraInvitation invitation = gson.fromJson(inv, HraInvitation.class);
		HraManager.add(invitation);
	}

	private String showList() {
		
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
					action = "<a href='hra?download=" + inv.id + "'><img src='img/start.png' class='start_icon'> Start Assist</a>";
				html.append("<tr").append(type)
					.append("><td>").append(inv.getStaleDisplay())
					.append("</td><td>").append(inv.getUserNameDisplay())
					.append("</td><td>").append(inv.machine)
					.append("</td><td>").append(inv.os)
					.append("</td><td>").append(action)
					.append("</td></tr>\n");
			}
		}
		
		HtmlTemplate tmpl = HtmlTemplate.load(HraServlet.class, "RAList.template");
		tmpl.replace("TABLE_ROWS", html.toString());
		return tmpl.toString();
	}

	private static String launch(String id) {
		
		int n = -1;
		try {
			n = Integer.parseInt(id);
		} catch (Exception e) {
		}
		
		String msg;
		try {
			if (HraManager.launch(n))
				msg = "OK";
			else
				msg = "Remote assistance request not found, or already started.";
		} catch (Exception e) {
			msg = e.toString();
		}
		return msg;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try (BufferedReader reader = request.getReader()) {
		
			Gson gson = new Gson();
			HraInvitation inv = gson.fromJson(reader, HraInvitation.class);

			log.debug("Received invitation: " + inv);
			HraManager.add(inv);
			
			response.setStatus(HttpServletResponse.SC_OK);			
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void main(String[] args) {
		HtmlTemplate tmpl = HtmlTemplate.load(HraServlet.class, "RAList.template");
		tmpl.replace("TABLE_ROWS", "_X_X_X_");
		System.out.println(tmpl.toString());
	}
}
