package com.vmware.horizontoolset.report;

import java.util.Collection;

import com.vmware.horizontoolset.usage.AccumulatedUsage;


public class AccumulatedUsageReport {
	
	private Collection<AccumulatedUsage> allUsages;
	
	public Collection<AccumulatedUsage> getUsageReport(){
		return this.allUsages;
	}
	
	public AccumulatedUsageReport(Collection<AccumulatedUsage> allUsages){
		this.allUsages = allUsages;
		
	}
	
}
