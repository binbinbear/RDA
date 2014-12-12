package com.vmware.horizontoolset.check;

public class VersionInCompatiableException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -709503511157755589L;

	private String message = " is not a tested server version, please update your Toolbox!";
	public VersionInCompatiableException(String serverVersion){
		this.message = serverVersion + message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}


	
	

}
