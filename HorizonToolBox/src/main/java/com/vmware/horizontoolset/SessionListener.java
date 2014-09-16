package com.vmware.horizontoolset;

import javax.servlet.http.HttpSessionEvent;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.SessionUtil;

public class SessionListener implements javax.servlet.http.HttpSessionListener {

	private static Logger log = Logger.getLogger(SessionListener.class);
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		log.debug("Session created");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.debug("Session destroyed");
		SessionUtil.releaseSession(se.getSession());
	}


}