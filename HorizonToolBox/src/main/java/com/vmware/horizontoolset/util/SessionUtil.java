package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.horizon.auditing.db.EventDBUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.operator.Machine;
import com.vmware.horizontoolset.viewapi.operator.ViewOperator;

public class SessionUtil {

	private static Logger log = Logger.getLogger(SessionUtil.class);
	private static ConcurrentHashMap <String, ToolBoxSession> sessions = new ConcurrentHashMap<String,ToolBoxSession>();
	private static TranslateUtil translateUtil = new TranslateUtil(); 
	//maximum sessions, default is 30
	private static int maximumSessions = 30;
	
	public static void setMaximumSessions(int max){
		SessionUtil.maximumSessions = max;
	}
	
	
	private static void restrictMaxCapacity(){
		
		int toBeRemoved = sessions.size() - maximumSessions;
		if (toBeRemoved <= 0)
			return;
		
		ArrayList<ToolBoxSession> sessionlist = new ArrayList<ToolBoxSession>(sessions.values());
		Collections.sort(sessionlist);
		
		for (int i = 0; i < toBeRemoved; i++){
			HttpSession hsession = sessionlist.get(i).getSession();
			log.debug("Start to release session:" + hsession.getCreationTime());
			releaseSession(hsession);
		}
	}
	
	private synchronized static ToolBoxSession getOrNewToolBoxSession(HttpSession session){
		if (session == null){
			return null;
		}
		ToolBoxSession ts = sessions.get(session.getId());
		if (ts==null){
			ts = new ToolBoxSession(session);
			sessions.put(session.getId(), ts);
			restrictMaxCapacity();
		}
		return ts;
	}
	
	public static <T> T getSessionObj(HttpSession session, Class<T> klass) {
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		return ts == null ? null : ts.get(klass);
	}
	
	public static void setSessionObj(HttpSession session, Object o) {
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		if (ts != null)
			ts.set(o);
	}

	public static void releaseSession(HttpSession session){
		if (session == null){
			return;
		}
		
		ToolBoxSession ts = sessions.remove(session.getId());
		if (ts!=null){
			ts.release();
		}
		
		
		//check the sessions to make sure no dead session
		
	}
	
	////////////////////////////////////////////////////////////////////////
	//	HELPERS
	////////////////////////////////////////////////////////////////////////
	
	public static ViewAPIService getViewAPIService(HttpSession session){
		return getSessionObj(session, ViewAPIService.class);
	}
	
	public static LDAP getLDAP(HttpSession session){
		return getSessionObj(session, LDAP.class);
	}
		
	public static void setUser(HttpSession session, String username){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		if (ts!=null){
			ts.setUser(username);
		}
	}
	
	public static String getuser(HttpSession session){
		ToolBoxSession ts =  getOrNewToolBoxSession(session);
		return (ts==null)? null:ts.getUser();
	}
	
	public static EventDBUtil getDB(HttpSession session){
		return getSessionObj(session, EventDBUtil.class);
	}

	/**
	 * @author Gao Xiaoning
	 * @param session
	 * @return
	 */
	public static ViewOperator getViewOperator(HttpSession session) {
		ViewOperator vo = getSessionObj(session, ViewOperator.class);
		if (null == vo) {
			ViewAPIService api = getSessionObj(session, ViewAPIService.class);
			if (api == null)
				return null;
			vo = new ViewOperator(api.getConn());
			setSessionObj(session, vo);
		}		
		return vo;
	}
	
	/**
	 * @author ziqil
	 */
	public static void  setLocale(HttpSession session, Locale locale){
		String language = translateUtil.getLocaleLanguage(locale);
		String translatedJsonURL = "./locale/"+language+".json";
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		if(ts!=null){
			ts.setJsonURL(translatedJsonURL);
		}
	}
	public static String getTranslatedJsonURL(HttpSession session){
		ToolBoxSession ts = getOrNewToolBoxSession(session);
		return (ts==null)? null:ts.getJsonURL();
	}


	public static List<String> getAllDesktopPools(HttpSession session) {
		return getViewOperator(session).getDesktopPoolNames();
	}
	
	public static List<Machine> getVMs(HttpSession session, String poolname) {
		return getViewOperator(session).getDesktopPool(poolname).machines.get();
	}
	
	public static List<Machine> getAllVMs(HttpSession session) {
		List<Machine> allvms = new ArrayList<Machine>();
		List<String> allpools = getAllDesktopPools(session);
		for (String pool: allpools ){
			allvms.addAll(getVMs(session, pool));
		}
		return allvms;
	}
	
	public static Machine getMachine(HttpSession session, String vmid) {
		if (StringUtil.isEmpty(vmid)){
			return null;
		}
		 List<Machine> allvms = getAllVMs(session);
		 for (Machine vm: allvms){
			 if (vmid.equalsIgnoreCase(vm.getVmid())){
				 return vm;
			 }
		 }
		 return null;
	}
}
