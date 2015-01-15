package com.vmware.eucenablement.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.eucenablement.service.VCenterService;
import com.vmware.eucenablement.service.ViewService;
import com.vmware.eucenablement.service.VolumeService;

public class SessionUtil {

	private static Logger log = Logger.getLogger(SessionUtil.class);
	private static ConcurrentHashMap<String, VolumeSession> sessions = new ConcurrentHashMap<String, VolumeSession>();

	// maximum sessions, default is 30
	private static int maximumSessions = 30;

	public static void setMaximumSessions(int max) {
		SessionUtil.maximumSessions = max;
	}

	private static void restrictMaxCapacity() {
		int toBeRemoved = sessions.size() - maximumSessions;
		if (toBeRemoved <= 0)
			return;
		ArrayList<VolumeSession> sessionlist = new ArrayList<VolumeSession>(sessions.values());
		Collections.sort(sessionlist);

		for (int i = 0; i < toBeRemoved; i++) {
			HttpSession hsession = sessionlist.get(i).getSession();
			log.debug("Start to release session:" + hsession.getCreationTime());
			releaseSession(hsession);
		}
	}

	private synchronized static VolumeSession getOrNewVolumeSeesion(HttpSession session) {
		if (session == null) {
			return null;
		}
		VolumeSession ts = sessions.get(session.getId());
		if (ts == null) {
			ts = new VolumeSession(session);
			sessions.put(session.getId(), ts);
			restrictMaxCapacity();
		}
		return ts;
	}

	private static <T> T getSessionObj(HttpSession session, Class<T> clazz) {
		VolumeSession ts = getOrNewVolumeSeesion(session);
		return ts == null ? null : ts.get(clazz);
	}

	public static void setSessionObj(HttpSession session, Object o) {
		VolumeSession ts = getOrNewVolumeSeesion(session);
		if (ts != null)
			ts.set(o);
	}

	public static void releaseSession(HttpSession session) {
		if (session == null) {
			return;
		}

		VolumeSession ts = sessions.remove(session.getId());
		if (ts != null) {
			ts.release();
		}
	}

	public static ViewService getViewService(HttpSession session) {
		return getSessionObj(session, ViewService.class);
	}

	public static VolumeService getVolumeService(HttpSession session) {
		return getSessionObj(session, VolumeService.class);
	}
	
	public static VCenterService getVCenterService(HttpSession session) {
		return getSessionObj(session, VCenterService.class);
	}
	
	public static void setUser(HttpSession session, String username) {
		VolumeSession ts = getOrNewVolumeSeesion(session);
		if (ts != null) {
			ts.setUser(username);
		}
	}

	public static String getuser(HttpSession session) {
		VolumeSession ts = getOrNewVolumeSeesion(session);
		return (ts == null) ? null : ts.getUser();
	}

}
