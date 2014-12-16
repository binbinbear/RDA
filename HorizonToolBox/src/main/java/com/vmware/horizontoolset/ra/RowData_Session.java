package com.vmware.horizontoolset.ra;


public class RowData_Session {
	
	public final String recordId;
	public String userName;
	public String sessionType;
	public String desktopName;
	
	public RowData_Session(String id) {
		recordId = id;
	}
}