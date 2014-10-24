package com.vmware.horizontoolset.device.guidata;
public class RowData_AccessLog {
		public final long recordId;
		public final String clientId;
		public final String clientType;
		public final String userName;
		public final String userDnsDomain;
		public final String time;
		public final String status;
		
		public RowData_AccessLog(long recordId, String clientId,
				String clientType, String userName, String userDnsDomain,
				long time, String status) {
			this.recordId = recordId;
			this.clientId = clientId;
			this.clientType = clientType;
			this.userName = userName;
			this.userDnsDomain = userDnsDomain;
			this.time = FormatUtil.formatTime(time);
			this.status = status;
		}
	}