package com.vmware.horizontoolset.device.guidata;


public class RowData_Whitelist {
	public final long recordId;
	public final String clientId;
	public final String clientType;
	public final String userName;
	public final String userDnsDomain;
	public final String lastAccessTime;
	
	public RowData_Whitelist(long recordId, String clientId,
			String clientType, String userName, String userDnsDomain,
			long lastAccessTime) {
		this.recordId = recordId;
		this.clientId = clientId;
		this.clientType = clientType;
		this.userName = userName;
		this.userDnsDomain = userDnsDomain;
		this.lastAccessTime = FormatUtil.formatTime(lastAccessTime);
	}
	
}