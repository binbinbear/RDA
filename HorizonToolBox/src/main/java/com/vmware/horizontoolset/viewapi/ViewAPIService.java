package com.vmware.horizontoolset.viewapi;

import java.util.List;

import com.vmware.horizontoolset.util.DesktopPool;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.VirtualCenter.VirtualCenterInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;



public interface ViewAPIService extends AutoCloseable {
	
	public List<SnapShotViewPool> getDetailedAutoPools();
	
	public List<SessionPool> getSessionPools();
	public List<SessionFarm> getSessionFarms();
	
	public int getSessionCount();

	/**
	 * if there are more than 10 pools but less than 1000 sessions, get one by one is too slow. So I use this interface to get all sessions and count by myself.
	 * @return
	 */
	public List<Session> getAllSessions();
	
	@Override
	public void close();

	/**
	 * 
	 * @return all desktop pools including rds pools, not including application pools
	 */
	public List<ViewPool> getAllDesktopPools();

	public List<ApplicationInfo> getAllApplicationPools();

	public List<Farm> getAllFarms();
	
	
	/**
	 * 
	 * @return all RDS hosts
	 */
	public List<RDS> getAllRDS();
	

	List<VirtualCenterInfo> listVirtualCenters();
	
	public List<ConnectionServer> getConnectionServers();
}
