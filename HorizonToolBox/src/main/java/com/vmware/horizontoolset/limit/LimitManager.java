package com.vmware.horizontoolset.limit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.usage.Event;
import com.vmware.horizontoolset.util.EmailUtil;
import com.vmware.horizontoolset.util.EventDBUtil;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.util.TaskModuleUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;

public class LimitManager {

	private static Logger log = Logger.getLogger(LimitManager.class);
	
	private static final String LIMIT_MAMAGER_KEY = "LIMIT_MGR";
	
	private static List<AppLimitInfo> apps = new ArrayList<AppLimitInfo>();
	
	private static ConcurrencyCalculator connCalc = new ConcurrencyCalculator();
	
	
	private static volatile boolean initialized = false;
	private static long lastUpdate;
	private static final long UPDATE_THRESHOLD = 10L * 60 * 1000;
	
	public static List<AppLimitInfo> list(HttpSession session) {
		
		updateAppConcurrency(session, false);
		
		synchronized (apps) {
			return new ArrayList<AppLimitInfo>(apps);
		}
	}

	static void updateAppConcurrency(HttpSession session, boolean force) {
		
		log.debug("LimitManager: updateAppConcurrency - entry. IsScheduledTask=" + (session == null));
		
		//skip update if not in force mode, and updating too quick
		if (!force) {
			long now = System.currentTimeMillis();
			if (now - lastUpdate < UPDATE_THRESHOLD) {
				log.debug("LimitManager: skip quick refresh");
				return;
			}
			lastUpdate = now;
		}
		
		List<AppLimitInfo> newDataList = getFreshNewDataList(session);

		if (newDataList == null)
			return;
		
		//
		//	Data is ready. Store. 
		//
		synchronized (apps) {
			apps.clear();
			apps.addAll(newDataList);			
		}

		//
		//	Send notification if any limit is exceeded
		//
		monitorLimitation(newDataList);
		
		log.debug("LimitManager: updateAppConcurrency - exit");
	}

	private static List<AppLimitInfo> getFreshNewDataList(HttpSession session) {

		try (ViewAPIService api = TaskModuleUtil.getViewAPIService(session);) {
		
			if (api == null)
				return null;
			
			//
			//	load current data, whether from persistent storage, or memory.
			//
			Map<String, AppLimitInfo> oldData;
			boolean justInitialized = false;
			if (!initialized) {
				log.debug("LimitManager: updateAppConcurrency - init.");
				oldData = load(session);
	
				initialized = true;
				justInitialized = true;
			} else {
				oldData = new HashMap<>();
				synchronized (apps) {
					for (AppLimitInfo a : apps)
						oldData.put(a.appId, a);
				}
			}
			
			//
			//	Retrieve all existing apps. There might be old deleted app 
			//	in "oldData". "newData" represents the current data. We migrate
			//	limit setting from oldData to newData.
			//
			Map<String, AppLimitInfo> newData = new HashMap<>();
			List<ApplicationInfo> appPools = api.getAllApplicationPools();
	
			log.debug("LimitManager: updateAppConcurrency - existing apps=" + appPools.size());
			
			for (ApplicationInfo info : appPools) {
				String id = info.data.name;
				
				AppLimitInfo a = oldData.remove(id);
				if (a == null)
					a = new AppLimitInfo(id, 0, 0);	//looks like a new app. Create the info.
				newData.put(id, a);
			}
			
			//
			//	list passed events, calculate current concurrency
			//
			List<AppLimitInfo> newDataList = new ArrayList<>(newData.values());
			Collections.sort(newDataList, new Comparator<AppLimitInfo>() {
				@Override
				public int compare(AppLimitInfo o1, AppLimitInfo o2) {
					return o1.appId.compareTo(o2.appId);
				}
			});
			
			int daysToPoll = justInitialized ? 7 : 1;
			processEvents(daysToPoll, newDataList);
			
			return newDataList;
		} catch (Exception e) {
			log.error("Error retrieving current app list.", e);
			return null;
		}
	}
	
	private static void processEvents(int days, List<AppLimitInfo> dataList) {
		
		List<Event> events;
		try (EventDBUtil dbu = EventDBUtil.createDefault();) {
			events = dbu.getEvents(days, true);
		}
		
		log.info("LimitManager: processEvents: " + events.size());
		connCalc.process(events);
		
		for (AppLimitInfo a : dataList) {
			a.concurrency = connCalc.getConcurrency(a.appId);
			
			log.debug("app:" + a.appId + ", c=" + a.concurrency);
		}
		
		log.debug("Dump ConnCalc:" + JsonUtil.javaToJson(connCalc));
	}

	
	private static void monitorLimitation(Collection<AppLimitInfo> apps) {
		log.info("LimitManager: monitorLimitation. App count=" + apps.size());
		
		for (AppLimitInfo a : apps) {
			if (a.isLimitExceeded()) {
				
				log.info("LimitManager: monitorLimitation: Exceeded: App=" + a.appId + ", limit=" + a.limit + ", current=" + a.concurrency);
				if (a.messageSent) {
					log.info("LimitManager: monitorLimitation: skip sending email because already sent");
				} else {
					log.info("LimitManager: monitorLimitation: sending email...");
					sentNotificationFor(a);
				}
				
				a.messageSent = true;
			} else {
				a.messageSent = false;
			}
		}
	}

	private static void sentNotificationFor(AppLimitInfo a) {

		StringBuilder content = new StringBuilder();

		String serverName;
		try {
			serverName = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			serverName = "UNDETERMINED";
		}
		
		content
			.append("Application Pool Name: ").append(a.appId).append("\r\n")
			.append("Limit: ").append(a.limit).append("\r\n")
			.append("Current Concurrency: ").append(a.concurrency).append("\r\n")
			.append("\r\n")
			.append("View Connection Server: ").append(serverName).append("\r\n")
			.append("\r\n")
			.append("Logon Horizon Toolbox to adjust the settings: ")
			.append("https://" + serverName + "/toolbox/limit");
		
		String title = "Horizon Toolbox Monitor - App Usage Limit Exceeded.";
		EmailUtil.sendMail(title, content.toString());
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
						a.isLimitExceeded();
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
		
		SharedStorageAccess.set(LIMIT_MAMAGER_KEY, value.toString());
	}
	
	private static Map<String, AppLimitInfo> load(HttpSession session) {
		log.debug("LimitManager: load - entry.");
		
		String value = SharedStorageAccess.get(LIMIT_MAMAGER_KEY);	
		
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
