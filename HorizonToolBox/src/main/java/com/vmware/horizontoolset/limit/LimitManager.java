package com.vmware.horizontoolset.limit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.util.EventDBUtil;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;

public class LimitManager {

	private static Logger log = Logger.getLogger(LimitManager.class);
	
	private static final String LIMIT_MAMAGER_KEY = "LIMIT_MGR";
	
	private static List<AppLimitInfo> apps = new ArrayList<AppLimitInfo>();
	
	private static ConcurrencyCalculator connCalc = new ConcurrencyCalculator();
	
	
	private static volatile boolean initialized = false;
	
	private static ViewAPIService _api;
	private static EventDBUtil _dbUtil;
	
	public static List<AppLimitInfo> list(HttpSession session) {
		
		updateAppConcurrency(session);
		
		synchronized (apps) {
			return new ArrayList<AppLimitInfo>(apps);
		}
	}

	static void updateAppConcurrency(HttpSession session) {
		
		log.debug("LimitManager: updateAppConcurrency - entry");
		
		if (session != null)
			_api = SessionUtil.getViewAPIService(session);

		if (_api == null)
			return;


		//
		//	load current data, whether from persistent storage, or memory.
		//
		Map<String, AppLimitInfo> currData;
		boolean justInitialized = false;
		if (!initialized) {
			log.debug("LimitManager: updateAppConcurrency - init.");
			currData = load(session);

			initialized = true;
			justInitialized = true;
		} else {
			currData = new HashMap<>();
			synchronized (apps) {
				for (AppLimitInfo a : apps)
					currData.put(a.appId, a);
			}
		}
		
		//
		//	Retrieve all existing apps. Migrate limit setting from existing data (currData).
		//
		Map<String, AppLimitInfo> newData = new HashMap<>();
		List<ApplicationInfo> appPools = _api.getAllApplicationPools();

		log.debug("LimitManager: updateAppConcurrency - existing apps=" + appPools.size());
		
		for (ApplicationInfo info : appPools) {
			String id = info.data.name;
			
			AppLimitInfo a = currData.get(id);
			if (a == null)
				a = new AppLimitInfo(id, 0, 0);	//looks like a new app. Create the info.
			newData.put(id, a);
		}
		
		//
		//	list passed events, calculate current concurrency
		//
		int daysToPoll = justInitialized ? 7 : 1;
		processEvents(session, daysToPoll);
		
		//
		//	Data is ready. Store. 
		//
		Collection<AppLimitInfo> newSettings = newData.values();
		synchronized (apps) {
			apps.clear();
			apps.addAll(newSettings);			
		}

		//
		//	Send notification if any limit is exceeded
		//
		
		monitorLimitation(newSettings);
		
		
		log.debug("LimitManager: updateAppConcurrency - exit");
	}

	private static void processEvents(HttpSession session, int days) {
		if (session != null)
			_dbUtil = SessionUtil.getDB(session);
		
		List<Event> events;
		if (_dbUtil != null)
			events = _dbUtil.getEvents(days, true);
		else {
			try (EventDBUtil dbu = EventDBUtil.createDefault();) {
				events = dbu.getEvents(days, true);
			}
		}
		
		log.info("LimitManager: processEvents: " + events.size());
		connCalc.process(events);
		
		synchronized (apps) {
			for (AppLimitInfo a : apps) {
				a.concurrency = connCalc.getConcurrency(a.appId);
			}
		}
	}

	private static void monitorLimitation(Collection<AppLimitInfo> apps) {
		log.info("LimitManager: monitorLimitation. App count=" + apps.size());
		
		for (AppLimitInfo a : apps) {
			if (a.concurrency > a.limit) {
				log.info("LimitManager: monitorLimitation: Exceeded: App=" + a.appId + ", limit=" + a.limit + ", current=" + a.concurrency);
			}
		}
	}

	public static void update(HttpSession session, String appId, int n) {
		if (n < 0)
			n = 0;
		
		boolean modified = false;
		synchronized (apps) {
			for (AppLimitInfo a : apps) {
				if (a.appId.equals(appId)) {
					if (a.limit != n) {
						a.limit = n;
						modified = true;
						break;
					}
				}
			}
		}
		
		if (modified)
			save(session);
	}

	private static void save(HttpSession session) {
		StringBuilder value = new StringBuilder();
		
		synchronized (apps) {
			for (AppLimitInfo a : apps) {
				if (value.length() > 0)
					value.append("||");
				value.append(a.appId).append(",,").append(a.limit);
			}
		}
		
		SharedStorageAccess ssa = SessionUtil.getSSA(session);
		if (ssa != null)
			ssa.set(LIMIT_MAMAGER_KEY, value.toString());
	}
	
	private static Map<String, AppLimitInfo> load(HttpSession session) {
		log.debug("LimitManager: load - entry.");
		
		String value = null;
		SharedStorageAccess ssa;
		boolean loaded = false;
		if (session != null) {
			ssa =  SessionUtil.getSSA(session);
			if (ssa != null) {
				value = ssa.get(LIMIT_MAMAGER_KEY);				
				loaded = true;
			}
		}
		if (!loaded)
			value = SharedStorageAccess.defaultContextGet(LIMIT_MAMAGER_KEY);
		
		if (value == null)
			return Collections.emptyMap();
		
		Map<String, AppLimitInfo> ret = new HashMap<>();
		
		String[] tmp = value.split("\\|\\|");
		for (String s : tmp) {
			try {
				String[] tmp2 = s.split(",,");
				if (tmp2.length == 2) {
					String id = tmp2[0];
					int limit = Integer.parseInt(tmp2[1]);
					ret.put(id, new AppLimitInfo(id, limit, 0));
				}
			} catch (Exception e) {
				//omit
			}
		}
		log.debug("LimitManager: load - count=" + ret.size());
		
		return ret;
	}

}
