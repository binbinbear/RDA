package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.vdi.vlsi.binding.vdi.resources.Farm.FarmInfo;

public class SessionFarmImpl implements SessionFarm{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((farmname == null) ? 0 : farmname.hashCode());
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
		SessionFarmImpl other = (SessionFarmImpl) obj;
		if (farmname == null) {
			if (other.farmname != null)
				return false;
		} else if (!farmname.equals(other.farmname))
			return false;
		return true;
	}

	private int sessionCount = 1;
	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}

	private String farmname;
	
	public SessionFarmImpl(String farmname, int count){
		this.farmname = farmname;
		this.sessionCount = count;
	}
	
	public SessionFarmImpl(FarmInfo info, int sessionCount){
		this.farmname = info.data.displayName;
		this.sessionCount = sessionCount;
	}
	@Override
	public String getName() {
		return this.farmname;
	}

	@Override
	public int getAppSessionCount() {
		return this.sessionCount;
	}

}
