package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.ViewPool;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;

public abstract class ViewPoolImpl implements ViewPool{

	protected DesktopInfo info;
	protected ViewQueryService service;
	
	
	private String name;
	
	public ViewPoolImpl(DesktopInfo info, ViewQueryService service){
		this.name = info.base.displayName;
		this.info = info;
		this.service = service;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String toString(){
		return "Pool name:"+this.name+"; Pool Type:"+this.getViewType()+";";
	}
	


}
