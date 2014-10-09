package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.horizontoolset.viewapi.ViewPool;
import com.vmware.horizontoolset.viewapi.ViewType;

public  class BasicViewPool implements ViewPool{
	
	private String name;
	private ViewType viewType;
	
	public BasicViewPool(DesktopSummaryView desktop){
		this.name = desktop.getDesktopSummaryData().getDisplayName();
		this.viewType = ViewType.getType(desktop);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ViewType getViewType() {
		return this.viewType;
	}

}
