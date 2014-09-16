package com.vmware.horizontoolset.viewapi.impl;

import java.nio.charset.Charset;

import com.vmware.horizontoolset.viewapi.EventDBSettings;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.EventDatabase.EventDatabaseSettings;
import com.vmware.vdi.vlsi.binding.vdi.util.SecureString;

public class EventDBSettingsImpl implements EventDBSettings{

	private EventDatabaseSettings settings;
	public EventDBSettingsImpl(EventDatabaseSettings settings){
		this.settings = settings;
	}
	
	@Override
	public String getServer() {
		return settings.server;
	}

	@Override
	public String getType() {
		return settings.type;
	}

	@Override
	public int getPort() {
		return settings.port;
	}

	@Override
	public String getDBName() {
		return settings.name;
	}

	@Override
	public String getUserName() {
		return settings.userName;
	}

	private static final Charset charset =  Charset.forName("UTF-8");
	@Override
	public String getSecurePassword() {
		SecureString securePassword = settings.password;
		return  new String(securePassword.utf8String.asArray(),charset );
	}

	@Override
	public String getTablePrefix() {
		return settings.getTablePrefix();
	}

}
