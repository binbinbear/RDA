package com.vmware.horizontoolset.viewapi;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;

public enum ViewType {
	Manual, FullClone, LinkedClone, RDS, APP, UNKNOWN;
	private static final String AUTOMATEDKEY = "AUTOMATED";
	private static final String MANUALKEY = "MANUAL";
	private static final String RDSKEY = "RDS";
	private static final String VIEW_COMPOSERKEY = "VIEW_COMPOSER";
	private static final String VIRTUAL_CENTERKEY = "VIRTUAL_CENTER";
	
	public static ViewType getType(DesktopSummaryView desktop){
		if (AUTOMATEDKEY.equals(desktop.desktopSummaryData.type )){
			if (VIEW_COMPOSERKEY.equals(desktop.desktopSummaryData.source )){
				return ViewType.LinkedClone;
			}else if (VIRTUAL_CENTERKEY.equals(desktop.desktopSummaryData.source) ){
				return  ViewType.FullClone;
			}
		}else if (MANUALKEY.equals(desktop.desktopSummaryData.type )){
			return ViewType.Manual;
		}else if (RDSKEY.equals(desktop.desktopSummaryData.type)){
			return  ViewType.RDS;
		}
		//ignore unknown pool, maybe application pool
		return UNKNOWN;
	}
	
	public static ViewType getType(SessionLocalSummaryView session){
		if (session.referenceData.farm!=null && session.referenceData.desktop==null){
			return APP;
		}
		if (AUTOMATEDKEY.equals(session.namesData.desktopType )){
			if (VIEW_COMPOSERKEY.equals(session.namesData.desktopSource )){
				return ViewType.LinkedClone;
			}else if (VIRTUAL_CENTERKEY.equals(session.namesData.desktopSource ) ){
				return  ViewType.FullClone;
			}
		}else if (MANUALKEY.equals(session.namesData.desktopType )){
			return ViewType.Manual;
		}else if (RDSKEY.equals(session.namesData.desktopType)){
			return  ViewType.RDS;
		}
		return UNKNOWN;
	}
}
