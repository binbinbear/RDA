package com.vmware.horizontoolset.viewapi.operator;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.EntitledUserOrGroup.EntitledUserOrGroupLocalSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.DesktopCName.DesktopSummaryViewCName;
import com.vmware.vdi.vlsi.cname.vdi.users.EntitledUserOrGroupCName.EntitledUserOrGroupLocalSummaryViewCName;

/**
 * A cached layer to wrap View API, which provides a way to
 * access objects in an hierarchical manner.
 * 
 * e.g. to access all machines of a specified desktop pool:
 * 		myViewOperator.getDesktopPool(poolName).machines
 * 
 * @author nanw
 */
public class ViewOperator implements AutoCloseable {

	public ViewOperatorNoCache op;
	
	public final CachedObjs<DesktopPool> desktopPools = new CachedObjs<DesktopPool>() {

		@Override
		protected void populateCache(List<DesktopPool> objects) {
			List<DesktopSummaryView> dsvs = op.listDesktopPools();
	    	for (int i = 0; i < dsvs.size(); i++) {
	    		DesktopSummaryView v = dsvs.get(i);
	    		objects.add(new DesktopPool(op.getConnection(), v));
	    	}    			
		}
	};
	
	public final CachedObjs<Session> sessions = new CachedObjs<Session>() {

		@Override
		protected void populateCache(List<Session> objects) {
			List<SessionLocalSummaryView> views = op.listAll(SessionLocalSummaryView.class);
	    	for (int i = 0; i < views.size(); i++) {
	    		SessionLocalSummaryView v = views.get(i);
	    		objects.add(new Session(op.getConnection(), v));
	    	}    			
		}
	};
	
	public final CachedObjs<Session> activeSessions = new CachedObjs<Session>() {

		@Override
		protected void populateCache(List<Session> objects) {
			List<SessionLocalSummaryView> views = op.listAll(SessionLocalSummaryView.class);
	    	for (int i = 0; i < views.size(); i++) {
	    		SessionLocalSummaryView v = views.get(i);
	    		if(v.getSessionData().getSessionState().compareToIgnoreCase("CONNECTED") == 0) {
	    			objects.add(new Session(op.getConnection(), v));
	    		}
	    	}    			
		}
	};
	
	public final CachedObjs<SessionLocalSummaryView> SessionLocalSummaryViews = new CachedObjs<SessionLocalSummaryView>() {

		@Override
		protected void populateCache(List<SessionLocalSummaryView> objects) {
			List<SessionLocalSummaryView> views = op.listAll(SessionLocalSummaryView.class);
	    	for (int i = 0; i < views.size(); i++) {
	    		SessionLocalSummaryView v = views.get(i);
	    		objects.add(v);
	    	}    			
		}
	};
	
    public ViewOperator(String host, String user, String password, String domain) {
    	op = new ViewOperatorNoCache(host, user, password, domain);
    }

	public ViewOperator(Connection conn) {
		op = new ViewOperatorNoCache(conn);
	}

    @Override
    public void close() {
    	desktopPools.clear();
        op.close();
    }
    
	public void deleteAllEntitlementsForUser(String name, String domain) {
		
		//ADUserOrGroupSummaryView v = op.getADUserOrGroup(name, domain);
		
	
		//list user entitlements
		QueryFilter filter = QueryFilter.equals(EntitledUserOrGroupLocalSummaryViewCName.ENTITLED_USER_OR_GROUP_LOCAL_SUMMARY_VIEW_CNAME.base.domain, domain);
		List<EntitledUserOrGroupLocalSummaryView> entitlements 
			= op.listAll(EntitledUserOrGroupLocalSummaryView.class, filter);
        
    	for (EntitledUserOrGroupLocalSummaryView en : entitlements) {
    	}
	}
	
	/**
	 * Query to server of the specified pool
	 * @param name
	 * @return
	 */
    public DesktopPool getDesktopPoolNoCache(String name) {
    	
    	QueryFilter filter = QueryFilter.equals(
    			DesktopSummaryViewCName.DESKTOP_SUMMARY_VIEW_CNAME.desktopSummaryData.name, name);
    	
    	List<DesktopSummaryView> ret = new ArrayList<>();
        try (Query<DesktopSummaryView> query = new Query<>(op.getConnection(), DesktopSummaryView.class, filter)) {
            for (DesktopSummaryView info : query) {
               ret.add(info);
            }
        }
        
        if (ret.isEmpty())
        	return null;
        
    	return new DesktopPool(op.getConnection(), ret.get(0));
    }
    
    /**
     * Get desktop pool, use cache whenever possible
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
    
    public List<String> getDesktopPoolNames() {
    	List<DesktopPool> pools = desktopPools.get(); 
		List<String> ret = new ArrayList<>(pools.size());
		for (DesktopPool p : pools)
			ret.add(p.getSummary().name);
		return ret;
    }

	public Session getSessionById(String sessionId) {
		for (Session ss : sessions.get()) {
			if (ss.summaryView.id.id.equals(sessionId))
				return ss;
		}
		return null;
	}
}
