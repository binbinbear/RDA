package com.vmware.horizontoolset.viewapi;

public interface EventDBSettings {
	public String getServer();
	public String getType();
	public int getPort();
	public String getDBName();
	public String getUserName();
	public String getSecurePassword();
	public String getTablePrefix();
}
