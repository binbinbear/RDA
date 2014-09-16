package com.vmware.horizontoolset.viewapi.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.horizontoolset.viewapi.Session;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
public class ViewAPIServiceImpl implements ViewAPIService{
	private static Logger log = Logger.getLogger(ViewAPIServiceImpl.class);
	private ViewAPIConnect _connection;
	private ViewQueryService _queryService;
	private String _host;
	private String _user;
	private String _pass;
	private String _domain;
	public ViewAPIServiceImpl(String host, String username, String password,String domain){
		//clear all cache since 
		Cache.emptyCache();
		this._host = host;
		this._user = username;
		this._pass = password;
		this._domain = domain;
		this._connection = new ViewAPIConnect(host);
		this._connection.login(username, password, domain);
		this._queryService = new ViewQueryService(this._connection);
		
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
	public void disconnect() {
		log.debug("Release resource: Disconnect the VIew API Service");
		if (this._connection!=null){
			this._connection.close();
			this._connection = null;
			this._queryService = null;
		}
		
	}

}
