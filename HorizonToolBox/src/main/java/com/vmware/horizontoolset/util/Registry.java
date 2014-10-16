package com.vmware.horizontoolset.util;

import com.vmware.vim25.GuestProgramSpec;

public class Registry {

	private String regkey = "REG_SZ";
	private GuestProgramSpec spec = new GuestProgramSpec();
	
	public GuestProgramSpec addRegistry(String keyname, String valuename, String value){
		spec.programPath = "C:\\WINDOWS\\System32\\reg.exe";
		spec.arguments = "add " + keyname + " /v " + valuename + " /t " + regkey + " /d " + value + " /f";
		return spec;
	}

	public GuestProgramSpec deleteRegistry(String keyname){
		spec.programPath = "C:\\WINDOWS\\System32\\reg.exe";
		spec.arguments = "delete " + keyname + " /f";
		return spec;
	}

}
