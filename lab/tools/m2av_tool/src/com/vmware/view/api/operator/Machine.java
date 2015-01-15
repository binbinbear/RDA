package com.vmware.view.api.operator;

import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;

public class Machine {

	private MachineSummaryView summaryView;

	public Machine(MachineSummaryView v) {
		this.summaryView = v;
	}
	
	public MachineSummaryView getSummaryView() {
		return summaryView;
	}
}
