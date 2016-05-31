package com.vmware.horizontoolset.viewapi.operator;

import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.MachineCName.MachineSummaryViewCName;

public class Session {
	
	private final Connection conn;
	
	public final SessionLocalSummaryView summaryView;
	
	private Machine machine; 
	
	public Session(Connection conn, SessionLocalSummaryView v) {
		this.conn = conn;
		this.summaryView = v;
	}

	public Machine getMachine(boolean forceReload) {
		
		if (machine == null || forceReload) {
			QueryFilter filter = QueryFilter.equals(MachineSummaryViewCName.MACHINE_SUMMARY_VIEW_CNAME.base.session, summaryView.id); 
	        try (Query<MachineSummaryView> query = new Query<>(conn, MachineSummaryView.class, filter)) {
	            for (MachineSummaryView v : query) {
	            	machine = new Machine(conn, v);
	            	break;
	            }
	        }
		}
		
		return machine;
	}

	public String getId() {
		return summaryView.id.id;
	}

	public String getUserName() {
		return summaryView.namesData.userName;
	}

	public String getType() {
		return summaryView.sessionData.sessionType;
	}

	public Object getMachineName() {
		return summaryView.namesData.machineOrRDSServerName;
	}

	public Object getDesktopPoolName() {
		return summaryView.namesData.desktopName;
	}
	
	public String getMachineDNS() {
		return summaryView.namesData.machineOrRDSServerDNS;
	}
}
