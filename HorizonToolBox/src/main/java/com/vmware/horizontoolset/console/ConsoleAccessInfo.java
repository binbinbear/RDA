package com.vmware.horizontoolset.console;

/**
 * ConsoleAccessInfo consists all information needed to
 * establish console connection for a specific VM on 
 * the hosting hypervisor.
 * 
 * @author nanw
 *
 */
public interface ConsoleAccessInfo {
	
	String getProtocol();
	
	String getHost();
	
	int getPort();
	
	String getType();
	
	String getThumbprint();
	
	String getUri() throws Exception;
	
	String getAttribute(String name);
}
