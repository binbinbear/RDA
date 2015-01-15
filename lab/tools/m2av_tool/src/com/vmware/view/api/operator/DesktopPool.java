package com.vmware.view.api.operator;

import java.util.List;

import com.vmware.vdi.vlsi.binding.vdi.entity.DesktopId;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryData;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.UserAssignment;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.EntitledUserOrGroup.EntitledUserOrGroupLocalSummaryView;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.MachineCName.MachineSummaryViewCName;
import com.vmware.vdi.vlsi.cname.vdi.users.EntitledUserOrGroupCName.EntitledUserOrGroupLocalSummaryViewCName;

public class DesktopPool {

	final ViewOperator op;
	private DesktopSummaryData summary;
	public final DesktopId id;
	private DesktopInfo info;
	
	public final CachedObjs<Machine> machines = new CachedObjs<Machine>() {
		@Override
		protected void populateCache(List<Machine> objects) {
			QueryFilter filter = QueryFilter.equals(MachineSummaryViewCName.MACHINE_SUMMARY_VIEW_CNAME.base.desktop, id); 
	        try (Query<MachineSummaryView> query = new Query<>(op.getConnection(), MachineSummaryView.class, filter)) {
	            for (MachineSummaryView v : query) {
	            	objects.add(new Machine(v));
	            }
	        }
		}
	};
	
	public final CachedObjs<String> entitledUsers = new CachedObjs<String>() {
		@Override
		protected void populateCache(List<String> objects) {
			QueryFilter filter = QueryFilter.contains(EntitledUserOrGroupLocalSummaryViewCName.ENTITLED_USER_OR_GROUP_LOCAL_SUMMARY_VIEW_CNAME.localData.desktops, new DesktopId[] {id}); 
	        try (Query<EntitledUserOrGroupLocalSummaryView> query = new Query<>(op.getConnection(), EntitledUserOrGroupLocalSummaryView.class, filter)) {
	            for (EntitledUserOrGroupLocalSummaryView v : query) {
	            	objects.add(v.base.loginName);
	            }
	        }
		}
	};
	
	DesktopPool(ViewOperator op, DesktopSummaryView dsv) {
		this.op = op;
		this.id = dsv.id;
		this.summary = dsv.desktopSummaryData;
	}
	
	public DesktopPool(ViewOperator op, DesktopId id) {
		this.op = op;
		this.id = id;
	}

	public DesktopInfo getInfo() {
		if (info == null) {
			Desktop service = op.getConnection().get(Desktop.class);
			info = service.get(id);
		}
		return info;
	}
	
	public DesktopSummaryData getSummary() {
		if (summary == null) {
			Desktop service = op.getConnection().get(Desktop.class);
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

}
