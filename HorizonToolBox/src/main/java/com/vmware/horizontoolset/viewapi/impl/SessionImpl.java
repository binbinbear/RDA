package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.ViewType;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;

public class SessionImpl implements Session{

	private String name;
	private ViewType type;
	public SessionImpl(SessionLocalSummaryView session){
		type = ViewType.getType(session);
		if (type == ViewType.APP){
			name = session.namesData.farmName;
		}else{
			name =  session.namesData.desktopName;
		}
		
	}

	@Override
	public String getPoolOrFarmName() {
		return name;
	}
	@Override
	public ViewType getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
