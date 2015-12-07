package com.vmware.horizontoolset.viewapi.operator;

import java.util.List;

import com.vmware.vdi.vlsi.binding.vdi.entity.DesktopId;
import com.vmware.vdi.vlsi.binding.vdi.entity.SessionId;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryData;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.UserAssignment;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.EntitledUserOrGroup.EntitledUserOrGroupLocalSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.MachineCName.MachineSummaryViewCName;
import com.vmware.vdi.vlsi.cname.vdi.users.EntitledUserOrGroupCName.EntitledUserOrGroupLocalSummaryViewCName;
import com.vmware.vdi.vlsi.cname.vdi.users.SessionCName.SessionLocalSummaryViewCName;

public class DesktopPool {

	final Connection conn;
	private DesktopSummaryData summary;
	public final DesktopId id;
	private DesktopInfo info;

	public final CachedObjs<Machine> machines = new CachedObjs<Machine>(ViewConfig.SAFE_API_TIMEOUT) {

		@Override
		protected void populateCache(List<Machine> objects) {
			objects.clear();
			QueryFilter filter = QueryFilter.equals(MachineSummaryViewCName.MACHINE_SUMMARY_VIEW_CNAME.base.desktop, id);
	        try (Query<MachineSummaryView> query = new Query<>(conn, MachineSummaryView.class, filter)) {
	            for (MachineSummaryView v : query) {
	            	objects.add(new Machine(conn, v, summary.name));
	            }
	        }
		}
	};

	public final CachedObjs<String> entitledUsers = new CachedObjs<String>() {

		@Override
		protected void populateCache(List<String> objects) {
			objects.clear();
			QueryFilter filter = QueryFilter.contains(EntitledUserOrGroupLocalSummaryViewCName.ENTITLED_USER_OR_GROUP_LOCAL_SUMMARY_VIEW_CNAME.localData.desktops, new DesktopId[] {id});
	        try (Query<EntitledUserOrGroupLocalSummaryView> query = new Query<>(conn, EntitledUserOrGroupLocalSummaryView.class, filter)) {
	            for (EntitledUserOrGroupLocalSummaryView v : query) {
	            	objects.add(v.base.loginName);
	            }
	        }
		}
	};

	public final CachedObjs<Session> sessions = new CachedObjs<Session>(ViewConfig.SAFE_API_TIMEOUT) {

		@Override
		protected void populateCache(List<Session> objects) {
			objects.clear();
			QueryFilter filter = QueryFilter.equals(SessionLocalSummaryViewCName.SESSION_LOCAL_SUMMARY_VIEW_CNAME.referenceData.desktop, id);
	        try (Query<SessionLocalSummaryView> query = new Query<>(conn, SessionLocalSummaryView.class, filter)) {
	            for (SessionLocalSummaryView v : query) {
	            	objects.add(new Session(conn, v));
	            }
	        }
		}
	};

	DesktopPool(Connection conn, DesktopSummaryView dsv) {
		this.conn = conn;
		this.id = dsv.id;
		this.summary = dsv.desktopSummaryData;
	}

	public DesktopPool(Connection conn, DesktopId id) {
		this.conn = conn;
		this.id = id;
	}

	public DesktopInfo getInfo() {
		if (info == null) {
			Desktop service = conn.get(Desktop.class);
			info = service.get(id);
		}
		return info;
	}

	public DesktopSummaryData getSummary() {
		if (summary == null) {
			Desktop service = conn.get(Desktop.class);
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

	public static class MachineStatistics {
		public int notInUse;
		public int inUse;
	}

	public MachineStatistics getMachineStat(boolean forceRefresh) {

		List<Machine> machines = this.machines.get(forceRefresh);

		MachineStatistics ret = new MachineStatistics();

		for (Machine m : machines) {
			SessionId sid = m.summaryView.base.session;
			if (sid == null) {
				ret.notInUse++;
			} else {
				ret.inUse++;
				/*
				com.vmware.vdi.vlsi.binding.vdi.users.Session sessionService = va.op.getConnection().get(com.vmware.vdi.vlsi.binding.vdi.users.Session.class);
				SessionLocalSummaryView v = sessionService.getLocalSummaryView(sid);

				System.out.println(m.getSummaryView().base.name + ": " + v.namesData.userName);
				*/
			}
		}

		return ret;
	}

}
