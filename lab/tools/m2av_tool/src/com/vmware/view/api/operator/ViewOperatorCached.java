package com.vmware.view.api.operator;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.ADUserOrGroup.ADUserOrGroupSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.EntitledUserOrGroup.EntitledUserOrGroupLocalSummaryView;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.DesktopCName.DesktopSummaryViewCName;
import com.vmware.vdi.vlsi.cname.vdi.users.EntitledUserOrGroupCName.EntitledUserOrGroupLocalSummaryViewCName;

public class ViewOperatorCached implements AutoCloseable {

	public ViewOperator op;

	public final CachedObjs<DesktopPool> desktopPools = new CachedObjs<DesktopPool>() {

		@Override
		protected void populateCache(List<DesktopPool> objects) {
			List<DesktopSummaryView> dsvs = op.listDesktopPools();
			for (int i = 0; i < dsvs.size(); i++) {
				DesktopSummaryView v = dsvs.get(i);
				objects.add(new DesktopPool(op, v));
			}
		}
	};

	public ViewOperatorCached(String host, String user, String password, String domain) {
		op = new ViewOperator(host, user, password, domain);
	}

	public ViewOperatorCached(Connection conn) {
		op = new ViewOperator(conn);
	}

	@Override
	public void close() {
		desktopPools.clearCache();
		op.close();
	}

	@SuppressWarnings("unused")
	public void deleteAllEntitlementsForUser(String name, String domain) {

		ADUserOrGroupSummaryView v = op.getADUserOrGroup(name, domain);

		// list user entitlements
		QueryFilter filter = QueryFilter.equals(EntitledUserOrGroupLocalSummaryViewCName.ENTITLED_USER_OR_GROUP_LOCAL_SUMMARY_VIEW_CNAME.base.domain, domain);
		List<EntitledUserOrGroupLocalSummaryView> entitlements = op.listAll(EntitledUserOrGroupLocalSummaryView.class, filter);

		for (EntitledUserOrGroupLocalSummaryView en : entitlements) {
		}
	}

	/**
	 * Query to server of the specified pool
	 * 
	 * @param name
	 * @return
	 */
	public DesktopPool getDesktopPoolNoCache(String name) {

		QueryFilter filter = QueryFilter.equals(DesktopSummaryViewCName.DESKTOP_SUMMARY_VIEW_CNAME.desktopSummaryData.name, name);

		List<DesktopSummaryView> ret = new ArrayList<>();
		try (Query<DesktopSummaryView> query = new Query<>(op.getConnection(), DesktopSummaryView.class, filter)) {
			for (DesktopSummaryView info : query) {
				ret.add(info);
			}
		}

		if (ret.isEmpty())
			return null;

		return new DesktopPool(op, ret.get(0));
	}

	/**
	 * Get desktop pool, use cache whenever possible
	 * 
	 * @param name
	 * @return
	 */
	public DesktopPool getDesktopPool(String name) {

		for (DesktopPool p : desktopPools.get()) {
			if (p.getSummary().name.equals(name))
				return p;
		}
		return null;
	}
}
