package com.vmware.horizontoolset.console;


/**
 * Single VM related operation interface, for HACA.
 * 
 *
 */
public interface VMService {
	
	/**
	 * Request console access basic info.
	 * This is a ticket-like object which indicates
	 * console type and how it should be opened.
	 * 
	 * @return
	 */
	ConsoleAccessInfo requestConsoleAccessInfo();
	
	
	public boolean poweron();

	public boolean poweroff();
	
	public boolean suspend();
	
	public boolean reset();
	
	public String getPowerState();
}
