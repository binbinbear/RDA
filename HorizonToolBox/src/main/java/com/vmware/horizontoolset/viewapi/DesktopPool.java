package com.vmware.horizontoolset.viewapi;

import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.viewapi.impl.ViewAPIConnect;
import com.vmware.vdi.vlsi.binding.vdi.entity.DesktopId;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryData;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.UserAssignment;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.MachineCName.MachineSummaryViewCName;

public class DesktopPool {

	private ViewAPIConnect _connection;
	private DesktopSummaryData summary;
	
	public final DesktopId id;
	private DesktopInfo info;
	
	public DesktopPool(ViewAPIConnect _connection, DesktopSummaryView dsv) {
		this._connection = _connection;
		this.id = dsv.id;
		this.summary = dsv.desktopSummaryData;
	}
	
	public DesktopPool(ViewAPIConnect _connection, DesktopId id) {
		this._connection = _connection;
		this.id = id;
	}

	public DesktopInfo getInfo() {
		if (info == null) {
			Desktop service = this._connection.get(Desktop.class);
			info = service.get(id);
		}
		return info;
	}
	
	public DesktopSummaryData getSummary() {
		if (summary == null) {
			Desktop service = this._connection.get(Desktop.class);
			summary = service.getSummaryView(id).desktopSummaryData;
		}
		return summary;
	}
	
	public void dump() {
				
		System.out.println("------------");
		System.out.println(getSummary().displayName + ": " + getInfo().type);
		
		DesktopInfo info = getInfo();
		
		UserAssignment ua;
		if (info.manualDesktopData != null) {
			ua = info.manualDesktopData.userAssignment;
			System.out.println(ua.userAssignment);
		}
		
		if (info.automatedDesktopData != null) {
			ua = info.automatedDesktopData.userAssignment;
			System.out.println(ua.userAssignment);
		}
		
		if (info.rdsDesktopData != null) {
			//FarmId farmId = info.rdsDesktopData.farm;
			//System.out.println("Farm ID: " + farmId);
		}

		
//		GlobalEntitlementId eid = info.globalEntitlementData.globalEntitlement;
//		
//		GlobalEntitlement globalEntitlement = op.getConnection().get(GlobalEntitlement.class);
//		GlobalEntitlementInfo gEntInfo = globalEntitlement.get(eid);
		
	}

	public List<MachineSummaryView> getMachines(DesktopId desktopId) {

		QueryFilter filter = QueryFilter.equals(MachineSummaryViewCName.MACHINE_SUMMARY_VIEW_CNAME.base.desktop, desktopId); 
        List<MachineSummaryView> ret = new ArrayList<>();
        try (Query<MachineSummaryView> query = new Query<>(this._connection, MachineSummaryView.class, filter)) {
            for (MachineSummaryView info : query) {
               ret.add(info);
            }
        }
        return ret;
	}

}
