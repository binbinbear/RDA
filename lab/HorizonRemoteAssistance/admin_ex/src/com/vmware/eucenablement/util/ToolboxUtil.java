package com.vmware.eucenablement.util;

public class ToolboxUtil {
	
	public static final String TOOLBOX_DATA_PATH;
	
	static {
		String programDataPath = System.getProperty("ProgramData", "/ProgramData");
		TOOLBOX_DATA_PATH = programDataPath + "\\HorizonToolbox";
	}
}
