package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

public class SessionMsg {

	public static enum Type {
		SUCCESS,
		INFO,
		WARN,
		SEVERE
	}
	
	public static class Msg {
		public final String msg;
		public final Type type;

		public Msg(String msg, Type type) {
			super();
			this.msg = msg;
			this.type = type;
		}
	}
	
	private static final int SAVE_CAPACITY = 20;
	private List<Msg> msgs = new ArrayList<Msg>(SAVE_CAPACITY);
	
	public static void addSuccess(HttpSession session, String msg) {
		add(session, msg, Type.SUCCESS);
	}
	
	public static void addInfo(HttpSession session, String msg) {
		add(session, msg, Type.INFO);
	}

	public static void addWarn(HttpSession session, String msg) {
		add(session, msg, Type.WARN);
	}

	public static void addSevere(HttpSession session, String msg) {
		add(session, msg, Type.SEVERE);
	}
	
	public static void add(HttpSession session, String msg, Type type) {
		List<Msg> msgs = get(session).msgs;
		
		synchronized(msgs) {
			
			while (msgs.size() >= SAVE_CAPACITY)
				msgs.remove(0);
			msgs.add(new Msg(msg, type));
		}
	}
	
	public synchronized static List<Msg> list(HttpSession session) {
		return new ArrayList<Msg>(get(session).msgs);
	}
	
	public synchronized static void clear(HttpSession session) {
		List<Msg> msgs = get(session).msgs;
		
		synchronized(msgs) {
			msgs.clear();
		}
	}
	
	private static SessionMsg get(HttpSession session) {
		synchronized (session) {
			String k = SessionMsg.class.getName();
			SessionMsg mgr = (SessionMsg)session.getAttribute(k);
			if (mgr == null) {
				mgr = new SessionMsg();
				session.setAttribute(k, mgr);
			}
			return mgr;
		}
	}
}
