package com.vmware.horizontoolset.viewapi.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;







import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter.VirtualCenterInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.resources.DesktopCName.DesktopSummaryViewCName;
import com.vmware.horizontoolset.util.DesktopPool;
import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewPool;
public class ViewAPIServiceImpl implements ViewAPIService{
	private static Logger log = Logger.getLogger(ViewAPIServiceImpl.class);
	private ViewAPIConnect _connection;
	private ViewQueryService _queryService;

	private VirtualCenter _virtualCenterService;
	private String _host;
	private String _user;
	private String _pass;
	private String _domain;
	public ViewAPIServiceImpl(String host, String username, String password,String domain){
		//clear all cache since 
		Cache.emptyCache();
		this._host = host;
		this.set_user(username);
		this.set_pass(password);
		this.set_domain(domain);

		this._connection = new ViewAPIConnect(host);
		this._connection.login(username, password, domain);
		this._queryService = new ViewQueryService(this._connection);
		this._virtualCenterService = this._connection.get(VirtualCenter.class);
	}
	
	@Override
	public List<SnapShotViewPool> getDetailedAutoPools() {
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
		Cache.emptyCache();
		 return this._queryService.getAllPools();
	}

	
	
	@Override
	public List<SessionPool> getSessionPools() {
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
		return this._queryService.getAllSessionPools();
	}

	@Override
	public List<SessionFarm> getSessionFarms() {
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
		return this._queryService.getAllSessionFarms();
	}

	@Override
	public List<Session> getAllSessions() {
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
		List<SessionLocalSummaryView> list = this._queryService.getAllSessions();
		List<Session> sessions = new ArrayList<Session>();
		for (SessionLocalSummaryView session: list){
			sessions.add(new SessionImpl(session));
		}
		return sessions;
	}

	@Override
	public int getSessionCount() {
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return 0;
		}
		return this._queryService.getAllSessionCount();
	}

	@Override
	public void close() {
		log.debug("Release resource: Disconnect the VIew API Service");
		if (this._connection!=null){
			this._connection.close();
			this._connection = null;
			this._queryService = null;
		}
		
	}



	public List<ViewPool> getAllPools()
	{
		if (this._queryService == null){
			log.info("Closed VIEW API can't be used!");
			return null;
		}
			
		return this._queryService.getAllBasicPools();
	}



	@Override
	public List<VirtualCenterInfo> listVirtualCenters() {
		
		List<VirtualCenterInfo> ret = new ArrayList<>();
		VirtualCenterInfo[] array =  this._virtualCenterService.list();
		for(int i=0; i<array.length; i++){
			ret.add(array[i]);
		}
		return ret;
	}

	
	@Override
	public DesktopPool getDesktopPool(String name) {
    	
    	QueryFilter filter = QueryFilter.equals(
    			DesktopSummaryViewCName.DESKTOP_SUMMARY_VIEW_CNAME.desktopSummaryData.name, name);
    	
    	List<DesktopSummaryView> ret = new ArrayList<>();
        try (Query<DesktopSummaryView> query = new Query<>(this._connection, DesktopSummaryView.class, filter)) {
            for (DesktopSummaryView info : query) {
               ret.add(info);
            }
        }
        
        if (ret.isEmpty())
        	return null;
        
    	return new DesktopPool(this._connection, ret.get(0));
    }

	public String get_user() {
		return _user;
	}

	public void set_user(String _user) {
		this._user = _user;
	}

	public String get_pass() {
		return _pass;
	}

	public void set_pass(String _pass) {
		this._pass = _pass;
	}

	public String get_domain() {
		return _domain;
	}

	public void set_domain(String _domain) {
		this._domain = _domain;
	}

}
