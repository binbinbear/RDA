package com.vmware.horizontoolset.policy.model;
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
