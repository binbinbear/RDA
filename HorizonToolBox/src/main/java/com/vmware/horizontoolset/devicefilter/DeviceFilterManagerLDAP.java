package com.vmware.horizontoolset.devicefilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

public class DeviceFilterManagerLDAP implements DeviceFilterManager {

	private IFilterJarModifier jarmodifier = new FilterJarModifier();

	private IWebXMLModifier xmlmodifier = new WebXMLModifier();

	private FilterStorage storage = new FilterStorage();
	private Logger log = Logger.getLogger(DeviceFilterManagerLDAP.class);

	@Override
	public List<DeviceFilterPolicy> getAllPolicies() {

		return storage.policies.get();
	}

	@Override
	public synchronized void updateFilterPolicy(DeviceFilterPolicy policy) {
		storage.addOrUpdate(policy);
	}

	@Override
	public synchronized void removeFilterPolicy(String pool) {

		storage.remove(pool);
	}


	@Override
	public boolean isEnabled() {
		// check web.xml and the jar
		return (jarmodifier.isFilterJarFound() && xmlmodifier.hasToolboxFilter());
	}

	@Override
	public synchronized void enable() {
		jarmodifier.putFilterJar();
		xmlmodifier.insertToolboxFilter();
		restartBroker();
	}

	@Override
	public synchronized void disable() {
		jarmodifier.removeFilterJar();
		xmlmodifier.removeToolboxFilter();
		restartBroker();
	}

	private void exeCuteCommand(String[] command) throws IOException{
		Process process;
		InputStream inputStream = null;

			process = new ProcessBuilder(command).start();

			inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				log.info("Command Result:" + line);
			}
			inputStream.close();

	}

	private void restartBroker() {
			try {
				String[] command1 = { "cmd.exe", "/c", "taskkill", "/IM", "ws_tomcatservice.exe", "/F" };
				exeCuteCommand(command1);
				String[] command2 = { "cmd.exe", "/c", "net", "start", "wstomcat" };
				exeCuteCommand(command2);
			} catch(Exception ex){
				log.error("Can't stop/start tomcat service",ex);
			}



	}

}
