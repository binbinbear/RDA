package com.vmware.horizontoolset.report;
import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.viewapi.ViewPool;

public class ViewPoolReport extends AbstractReport{
	private List<ViewPool> pools;
	public ViewPoolReport(List<ViewPool> viewpools){
		this.pools = new ArrayList<ViewPool>();
		for (ViewPool pool: viewpools){
			if (pool.getName() != null){
				this.pools.add(pool);
			}
		}
		
	}
	
	public List<ViewPool> getPools() {
		return pools;
	}
}
