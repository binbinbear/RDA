package com.vmware.horizontoolset.policy.polfile;

public class PolFileException extends RuntimeException {
	

	private static final long serialVersionUID = 917826202591588338L;

	public PolFileException() {
    }
	
    public PolFileException(String message) {
        super(message);
    }

    public PolFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolFileException(Throwable cause) {
        super(cause);
    }
}