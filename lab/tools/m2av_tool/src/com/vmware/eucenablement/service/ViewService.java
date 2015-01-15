package com.vmware.eucenablement.service;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.eucenablement.util.SessionUtil;
import com.vmware.eucenablement.util.VolumeSession;
import com.vmware.view.api.operator.ViewOperatorCached;

public class ViewService {
	private static Logger log = Logger.getLogger(VolumeSession.class);
	public ViewOperatorCached op;

	// get the service agent
	public synchronized static ViewService instance(HttpSession session) {
		ViewService api = SessionUtil.getViewService(session);
		if (api == null) {
			log.warn(" View Server session Timeout");
		}
		return api;
	}

	// init view operator cache.
	private ViewService(String server, String name, String password, String domain) {
		try {
			this.op = new ViewOperatorCached(server, name, password, domain);
		} catch (Throwable th) {
			this.op = null;
		}

	}

	// login to view server, and store view service in session
	public synchronized static void login(HttpSession session, String server, String name, String password, String domain) {
		log.info(server + name + password + domain);
		ViewService api = new ViewService(server, name, password, domain);
		if (api.op == null) {
			return;
		}
		SessionUtil.setSessionObj(session, api);
	}

}
