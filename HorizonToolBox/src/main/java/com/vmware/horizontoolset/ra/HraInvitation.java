package com.vmware.horizontoolset.ra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.vmware.horizontoolset.util.HttpUtil;
import com.vmware.horizontoolset.util.TimeInterval;

public class HraInvitation {
/**
 * the following public string must be public since they are from gson
 * Otherwise, they may be renamed by compiler.jar when building
 */
    public String machine;
    public String user;
    public String domain;
    public String os;
    
	public String inv;
	public String code;
	public int nonce;

	private transient Path path;
	private transient long time;
	private transient long timeoutSeconds;
	
	public transient boolean started;
	public transient int id;
	private static AtomicInteger counter = new AtomicInteger();
	
	private static int agentPort = 18443;		//for admin initiated RA only.
	private static boolean enableSSL = false;	//for admin initiated RA only.
	
	void init() {
		if (time != 0)
			throw new IllegalStateException();
		time = System.currentTimeMillis();
		id = counter.incrementAndGet();
		parseTimeout();
	}

	private HraInvitation() {
	}
	
	private void parseTimeout() {
		//DtLength="60"

		final String FLAG = "DtLength=\"";
		
		try {
			do {
				int j = inv.indexOf(FLAG);
				if (j < 0)
					break;
				j += FLAG.length();
				int k = inv.indexOf('"', j);
				if (k < 0)
					break;
				String txt = inv.substring(j, k);
				timeoutSeconds = Integer.parseInt(txt) * 60;
				
				} while (false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (timeoutSeconds < 600)
			timeoutSeconds = 600;	//safe time here. We lack of the time unit information here.
	}
	
	public void launch() throws Exception {
		if (started)
			throw new IllegalStateException("Already started");
		
		started = true;
		
		path = Files.createTempFile("Invitation_", ".HorizonRemoteAssistance");
		
		Files.write(path, inv.getBytes("UTF-8"));
		
		Runtime rt = Runtime.getRuntime();
		
		String[] cmd = {
				getHraLauncherPath(), 
				"/openfile", 
				path.toString()
		};
		String[] envp = {};
		
		Process proc = rt.exec(cmd, envp, path.getParent().toFile());
	}
	
	private static String getHraLauncherPath() {
		String hraInstallDir = "C:\\Program Files\\Horizon Remote Assistance";
		return hraInstallDir + "\\Horizon Remote Assistance Launcher.exe";
	}
	
	public static void fetchInvitationAndLaunch(String host) throws Exception {
		String code = "asdf";
		String nonce = String.valueOf(System.currentTimeMillis());
		String protocol = enableSSL ? "https" : "http";
		String url = protocol + "://" + host + ":" + agentPort + "/hra?v=" + code + "&nonce=" + nonce;
		String json = HttpUtil.getHTML(url);
		if (json == null)
			throw new IOException("Fail retrieving invitation.");
		
		Gson gson = new Gson();
		HraInvitation inv = gson.fromJson(json, HraInvitation.class);
		inv.launch();
	}
	
	public boolean isTimeout() {
		int seconds = (int) ((System.currentTimeMillis() - time) / 1000); 
		return seconds > timeoutSeconds;
	}
	
	public boolean isLegacy() {
		int hours = (int) ((System.currentTimeMillis() - time) / 1000 / 60 / 60); 
		return hours > 24;
	}
	
	public String getUserNameDisplay() {
		if (domain != null)
			return domain + '/' + user;
		return user;
	}
	
	public String getStaleDisplay() {
		return new TimeInterval(time).getDescription();
	}
	
	public String getInvContent() {
		return inv;
	}
	
	static HraInvitation _createTest() {
		HraInvitation a = new HraInvitation();
		a.inv = "test";
		a.time = System.currentTimeMillis();
		return a;
	}
	
	public String toString() {
		String msg = machine + ", " + getUserNameDisplay() + ", " + os + ". Ticket length=";
		
		if (inv == null)
			msg += 0;
		else
			msg += inv.length();
		return msg;
	 }

	public static void configAgentPort(int n) {
		agentPort = n;
	}

	public static void configSSL(boolean b) {
		enableSSL = b;
	}
	
	public static void main(String[] args) {
		try {
			fetchInvitationAndLaunch("10.117.162.22");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
