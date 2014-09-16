package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.ViewType;

public class SessionPoolImpl implements SessionPool{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((poolname == null) ? 0 : poolname.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionPoolImpl other = (SessionPoolImpl) obj;
		if (poolname == null) {
			if (other.poolname != null)
				return false;
		} else if (!poolname.equals(other.poolname))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	private String poolname;
	private ViewType type;
	private int sessionCount = 1;
	
	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}


	public SessionPoolImpl(String poolname,   ViewType type, int count){
		this.poolname= poolname;
		this.type = type;
		this.sessionCount = count;
	}
	
	
	public SessionPoolImpl(DesktopSummaryView view,   ViewType type, int sessionCount){
		this.poolname= view.desktopSummaryData.displayName;
		this.type = type;
		this.sessionCount = sessionCount;
	}

	@Override 
	public int getSessionCount(){
		return this.sessionCount;
	}
	@Override
	public String getName() {
		return this.poolname;
	}
	@Override
	public ViewType getViewType() {
		return type;
	}

}
