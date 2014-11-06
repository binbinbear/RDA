package com.vmware.horizontoolset.report;
import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.viewapi.Farm;
import com.vmware.horizontoolset.viewapi.ViewPool;

public class ViewPoolReport extends AbstractReport{
	private List<ViewPool> pools;
	private List<Farm> farms;
	public ViewPoolReport(List<ViewPool> viewpools, List<Farm> farms){
		this.pools = new ArrayList<ViewPool>();
		for (ViewPool pool: viewpools){
			if (pool.getName() != null){
				this.pools.add(pool);
			}
		}
		
		this.farms = farms;
		
	}
	
	public List<ViewPool> getPools() {
		return pools;
	}
	
	public List<Farm> getFarms() {
		return farms;
	}
}
