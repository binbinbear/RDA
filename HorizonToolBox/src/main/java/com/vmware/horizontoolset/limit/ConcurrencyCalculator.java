package com.vmware.horizontoolset.limit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.util.EventImpl;

/**
 * Count concurrent users per app, by associating a set object (all connected users)
 * for each app.
 * 
 * Input: events (cares about: "requesting app", "user logout")
 * Output: concurrency of each app.
 * 
 * @author nanw
 *
 */
class ConcurrencyCalculator {

	private static Logger log = Logger.getLogger(ConcurrencyCalculator.class);
	
	//"User ASDF/asdf requested Application Calculator"
	private final static Pattern APP_REQUEST = Pattern.compile("^User (.+) requested Application (.+)$");
	//"User ASDF/asdf has logged out"
	private final static Pattern USER_LOGOUT = Pattern.compile("^User (.+) has logged out.*$");
	
	
	private Map<String, Set<String>> concurrencyPerApp = new HashMap<>();
	private long lastProcessedTimestamp;
	
	public synchronized void process(List<Event> events) {

		long t = 0;
		for (Event en : events) {
			t = en.getTime().getTime();
			
			//skip all events that have already been processed.
			if (t < lastProcessedTimestamp) {
				log.debug("Skip msg by timestamp. eventTime=" + t + ", lastTime=" + lastProcessedTimestamp + ", msg=" + en.getShortMessage());
				continue;
			}
			
			if (!(en instanceof EventImpl))
				continue;
			
			String msg = en.getShortMessage();
			
			log.debug("msg:" + msg + ", type=" + en.getType());
			
			//if the event is app request...
			Matcher m = APP_REQUEST.matcher(msg);
			if (m.matches()) {
				String user = m.group(1);
				String app = m.group(2);
				
				onAppRequest(user, app);
			} else {
				
				//if the event is user logout
				m = USER_LOGOUT.matcher(msg);
				if (m.matches()) {
					String user = m.group(1);
					
					onUserLogout(user);
				}
			}
		}
		
		lastProcessedTimestamp = t;
	}

	private void onUserLogout(String user) {
		log.debug("on logout: user=" + user);
		
		user = user.toLowerCase();
		
		for (Set<String> users : concurrencyPerApp.values()) {
			boolean removed = users.remove(user);
			if (removed)
				log.debug("removed from one app");
		}
	}

	private void onAppRequest(String user, String app) {
		
		app = app.toLowerCase();
		user = user.toLowerCase();
		
		Set<String> users = concurrencyPerApp.get(app);
		if (users == null) {
			users = new HashSet<>();
			concurrencyPerApp.put(app, users);
		}
		users.add(user);
		
		log.debug("on request: app=" + app + ", user=" + user + ", concurrency=" + users.size());
	}

	public synchronized int getConcurrency(String appId) {
		
		appId = appId.toLowerCase();
		
		Set<String> users = concurrencyPerApp.get(appId);
		return users == null ? 0 : users.size();
	}
	
	
	public static void main(String[] args) {

		Matcher m = APP_REQUEST.matcher("User ASDF/asdf requested Application Calculator");
		if (m.matches()) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}

		m = USER_LOGOUT.matcher("User ASDF/asdf has logged out");
		if (m.matches()) {
			System.out.println(m.group(1));
		}

		System.out.println("OK");
	}
}