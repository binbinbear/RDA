package com.vmware.horizontoolset.policy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PCoIPCategory {
	//Configure clipboard redirection:
	private  ClipboardRediretion clipboardRediretion;

	public ClipboardRediretion getClipboardRediretion() {
		return clipboardRediretion;
	}

	public void setClipboardRediretion(ClipboardRediretion clipboardRediretion) {
		this.clipboardRediretion = clipboardRediretion;
	}
	
	
	//Turn off Build-to-Lossless feature
	private boolean turnOffLossLess;

	public boolean isTurnOffLossLess() {
		return turnOffLossLess;
	}

	public void setTurnOffLossLess(boolean turnOffLossLess) {
		this.turnOffLossLess = turnOffLossLess;
	}
}