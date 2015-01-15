package com.vmware.eucenablement.service;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.eucenablement.horizontoolset.av.api.VolumeAPI;
import com.vmware.eucenablement.util.SessionUtil;
import com.vmware.eucenablement.util.VolumeSession;

public class VolumeService {
	private static Logger log = Logger.getLogger(VolumeSession.class);
	public VolumeAPI volume;

	private VolumeService() {
		volume = new VolumeAPI();
	}

	// get Volume agent service
	public synchronized static VolumeService instance(HttpSession session) {
		VolumeService api = SessionUtil.getVolumeService(session);
		if (null == api) {
			log.warn("App Volumes Session Timeout");
		}
		return api;
	}

	// login to manager, and store volume service in session
	public synchronized static void login(HttpSession session, String server, String domain, String name, String password) {
		VolumeService api = new VolumeService();
		try {
			log.info(server + domain + name + password);
			api.volume.connect(server, domain, name, password);
		} catch (Exception e) {
			log.error("can not connect to appVolumes");
			api=null;
			return;
		}
		SessionUtil.setSessionObj(session, api);
	}
}
