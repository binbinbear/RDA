package com.vmware.horizontoolset.viewapi;

import java.util.List;

import com.vmware.horizontoolset.viewapi.impl.BasicViewPool;

public interface ViewAPIService {
	public List<SnapShotViewPool> getDetailedAutoPools();
	
	public List<SessionPool> getSessionPools();
	public List<SessionFarm> getSessionFarms();
	
	public int getSessionCount();

	/**
	 * if there are more than 10 pools but less than 1000 sessions, get one by one is too slow. So I use this interface to get all sessions and count by myself.
	 * @return
	 */
	public List<Session> getAllSessions();
	
	public void disconnect();

	public List<BasicViewPool> getAllPools();
	
}
