package com.vmware.horizontoolset.devicefilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SessionUtil;

@RestController
public class DeviceFilterRestController {

	private static Logger log = Logger.getLogger(DeviceFilterRestController.class);


	private DeviceFilterManager devicemanager ;

	public DeviceFilterManager getDevicemanager() {
		return devicemanager;
	}

	public void setDevicemanager(DeviceFilterManager devicemanager) {
		this.devicemanager = devicemanager;
	}

	@RequestMapping("/devicefilter/all")
	public List<DeviceFilterPolicy> getAllPolicies(HttpSession session) {

		List<String> pools = SessionUtil.getAllDesktopPools(session);
		List<String> applicationpools = SessionUtil.getAllAppPools(session);
		if (applicationpools!=null && !applicationpools.isEmpty()){
			pools.addAll(applicationpools);
		}

		List<DeviceFilterPolicy> policies = this.devicemanager.getAllPolicies(pools);

		log.info("Policies found:" + policies.size());

		return policies;
	}

	@RequestMapping("/devicefilter/update")
	public String updateFilterPolicy(HttpSession session,
			@RequestParam(value = "policyStr", required = true) String policyStr) {

		try {
			log.info("set policy:" + policyStr);

			DeviceFilterPolicy policy = JsonUtil.jsonToJava(policyStr, DeviceFilterPolicy.class);

			this.devicemanager.updateFilterPolicy(policy);

			return "successful ";
		} catch (Exception e) {
			log.warn("Error updating access policy for  " + policyStr, e);
			return "failed";
		}
	}

	@RequestMapping("/devicefilter/remove")
	public String removeFilterPolicy(HttpSession session, @RequestParam(value = "pool", required = true) String pool) {

		try {
			log.info("remove policy for pool:" + pool);

			this.devicemanager.removeFilterPolicy(pool);

			return "successful ";
		} catch (Exception e) {
			log.warn("Error updating access policy for  " + pool, e);
			return "failed";
		}
	}

	// show the past 1000 blocked access
	private int max_history = 1000;

	@RequestMapping("/devicefilter/result")
	public List<BlockedAccess> getAllResults(HttpSession session) {
		// TODO: read from DB
		List<BlockedAccess> all = new ArrayList<BlockedAccess>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getLogFile()));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null && i++ < max_history) {
				// process this line
				// INFO -
				// {"mac":"A8:20:66:25:A7:39","ip":"10.112.116.99","user":"admin","pool":"Paint","device":"steng_mac","type":"Mac","date":"Mar
				// 14, 2016 4:09:34 PM"}
				String json = line.substring(line.indexOf('{'));
				try {
					BlockedAccess access = JsonUtil.jsonToJava(json, BlockedAccess.class);
					all.add(access);
				} catch (Exception ex) {
					log.error("Can't parse json:" + json, ex);
				}
			}
			reader.close();

		} catch (Exception ex) {
			log.error("Can't process filter result", ex);
		}

		return all;
	}

	private static final String filterlogname = "ToolboxBlocked.log";

	private File getLogFile() {
		String serverPath = Application.getViewServerPath();

		return new File(serverPath + "\\bin\\" + filterlogname);
	}

}
