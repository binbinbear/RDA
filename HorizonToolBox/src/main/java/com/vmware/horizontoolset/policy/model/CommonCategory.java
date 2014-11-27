package com.vmware.horizontoolset.policy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonCategory {
//Block all devices unless they are included via Include filter rule
	private boolean blockAll;

	public boolean isBlockAll() {
		return blockAll;
	}

	public void setBlockAll(boolean blockAll) {
		this.blockAll = blockAll;
	}
	
	
	// Number of days to keep production logs:;
	private int daysToKeepLogs;

	public int getDaysToKeepLogs() {
		return daysToKeepLogs;
	}

	public void setDaysToKeepLogs(int daysToKeepLogs) {
		this.daysToKeepLogs = daysToKeepLogs;
	}
	
}
