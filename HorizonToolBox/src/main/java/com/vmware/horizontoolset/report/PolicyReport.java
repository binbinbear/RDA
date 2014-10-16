package com.vmware.horizontoolset.report;

import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;

public class PolicyReport extends AbstractReport{

	private List<SessionPool> pools;
	private List<SessionFarm> policies;
	public PolicyReport(List<SessionPool> viewpools, List<SessionFarm> farms){
		this.pools = new ArrayList<SessionPool>();
		for (SessionPool pool: viewpools){
			if (pool.getSessionCount()>0){
				this.pools.add(pool);
			}
		}
		
		this.policies = new ArrayList<SessionFarm>();
		for (SessionFarm farm: farms){
			if (farm.getAppSessionCount()>0){
				this.policies.add(farm);
			}
		}
	}
	
	public List<SessionPool> getPools() {
		return pools;
	}
	
	public List<SessionFarm> getpolicies(){
		return policies;
	}
}
