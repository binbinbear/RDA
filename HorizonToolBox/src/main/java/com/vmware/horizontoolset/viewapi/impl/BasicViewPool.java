package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.ViewPool;
import com.vmware.horizontoolset.viewapi.ViewType;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;

public  class BasicViewPool implements ViewPool{
	
	private String name;
	private ViewType viewType;
	
	public BasicViewPool(DesktopSummaryView desktop){
		this.name = desktop.getDesktopSummaryData().getDisplayName();
		this.viewType = ViewType.getType(desktop);
	}
	
	public BasicViewPool(ApplicationInfo info){
		this.name =info.getData().getDisplayName();
		this.viewType = ViewType.APP;
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
