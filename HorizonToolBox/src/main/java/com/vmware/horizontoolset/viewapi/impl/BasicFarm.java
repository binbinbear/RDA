package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.Farm;
import com.vmware.vdi.vlsi.binding.vdi.resources.Farm.FarmSummaryView;

public class BasicFarm implements Farm{

	private String name;
	public BasicFarm(FarmSummaryView info){
		this.name= info.data.name;
	}
	@Override
	public String getName() {
		return this.name;
	}

}
