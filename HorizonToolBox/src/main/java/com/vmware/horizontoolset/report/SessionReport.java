package com.vmware.horizontoolset.report;

import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;

public class SessionReport extends AbstractReport{

	private List<SessionPool> pools;
	private List<SessionFarm> farms;
	public SessionReport(List<SessionPool> viewpools, List<SessionFarm> farms){
		this.pools = new ArrayList<SessionPool>();
		for (SessionPool pool: viewpools){
			if (pool.getSessionCount()>0){
				this.pools.add(pool);
			}
		}
		
		this.farms = new ArrayList<SessionFarm>();
		for (SessionFarm farm: farms){
			if (farm.getAppSessionCount()>0){
				this.farms.add(farm);
			}
		}
	}
	
	public List<SessionPool> getPools() {
		return pools;
	}
	
	public List<SessionFarm> getFarms(){
		return farms;
	}
}
