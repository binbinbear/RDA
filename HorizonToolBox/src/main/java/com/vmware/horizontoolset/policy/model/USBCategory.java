package com.vmware.horizontoolset.policy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class USBCategory {
//Allow other input devices:
	private boolean allowOther;

	public boolean isAllowOther() {
		return allowOther;
	}

	public void setAllowOther(boolean allowOther) {
		this.allowOther = allowOther;
	}
	//Allow HID-Bootable:
	private boolean hidBootable;

	public boolean isHidBootable() {
		return hidBootable;
	}

	public void setHidBootable(boolean hidBootable) {
		this.hidBootable = hidBootable;
	}
}
