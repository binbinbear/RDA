package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.RDS;
import com.vmware.vdi.vlsi.binding.vdi.resources.RDSServer.RDSServerSummaryView;

public class BasicRDS implements RDS{

	private String name;
	private String farmName;
	public BasicRDS(RDSServerSummaryView summaryView){
		this.name = summaryView.getAgentData().dnsName;
		this.farmName = summaryView.getSummaryData().farmName;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFarmName() {
		return farmName;
	}

}
