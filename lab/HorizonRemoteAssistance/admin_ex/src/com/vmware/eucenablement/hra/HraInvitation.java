package com.vmware.eucenablement.hra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.vmware.eucenablement.util.HttpUtil;
import com.vmware.eucenablement.util.TimeInterval;

public class HraInvitation {

    public String machine;
    public String user;
    public String domain;
    public String os;
    
	private String inv;
	
	private transient Path path;
	private transient long time;
	private transient long timeoutSeconds;
	
	public transient boolean started;
	public transient int id;
	private static AtomicInteger counter = new AtomicInteger();

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
		//DtLength="360"

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
				timeoutSeconds = Integer.parseInt(txt);
			} while (false);
		} catch (Exception e) {
			timeoutSeconds = 360;
			e.printStackTrace();
		}
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
		//add by wx 9-15
		String sysDriver = System.getProperty("user.home");
		sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		String hraInstallDir = sysDriver + ":\\Program Files\\Horizon Remote Assistance";
		//String hraInstallDir = "C:\\Program Files\\Horizon Remote Assistance";
		return hraInstallDir + "\\Horizon Remote Assistance Launcher.exe";
	}
	
	public static void fetchInvitationAndLaunch(String host) throws Exception {
		String url = "http://" + host + ":32121/hra?v=1";
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
		return machine + ", " + getUserNameDisplay() + ", " + os;
	 }
}
