package com.vmware.horizontoolset.viewapi.impl;

import org.apache.log4j.Logger;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewType;

public class PoolFactory {

	private static Logger log = Logger.getLogger(PoolFactory.class);
	
	public static BasicViewPool getBasicViewPool(DesktopSummaryView desktop){
		return new BasicViewPool(desktop);
	}
	
	public static SnapShotViewPool getPool(DesktopSummaryView desktop, ViewQueryService service){
		
		log.debug("desktop type:"+desktop.desktopSummaryData.type+" name:"+ desktop.desktopSummaryData.displayName);
		DesktopInfo info = service.getDesktopInfo(desktop);
		if (info == null){
			return null;
		}
		ViewType type = ViewType.getType(desktop);
		switch (type){
		case FullClone:
			return new FullClonePoolImpl(info, service);
		case LinkedClone:
			return new LinkedClonePoolImpl(info,service);
		default:
				return null;
		}
		

	}

	public static SessionPool getSessionPool(DesktopSummaryView desktop,
			int sessionCount) {

		log.debug("desktop type:"+desktop.desktopSummaryData.type+" name:"+ desktop.desktopSummaryData.displayName);
		ViewType type = ViewType.getType(desktop);
		return new SessionPoolImpl(desktop, type, sessionCount);
		
	}
}
