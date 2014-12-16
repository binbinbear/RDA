package com.vmware.horizontoolset.viewapi.operator;

import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.client.Connection;

public class Machine {

	private final Connection conn;
	public final MachineSummaryView summaryView;

	public Machine(Connection conn, MachineSummaryView v) {
		this.conn = conn;
		this.summaryView = v;
	}
}
