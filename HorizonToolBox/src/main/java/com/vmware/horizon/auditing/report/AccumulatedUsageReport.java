package com.vmware.horizon.auditing.report;

import java.util.Collection;



public class AccumulatedUsageReport extends AbstractReport{
	
	private Collection<AccumulatedUsage> allUsages;
	
	public Collection<AccumulatedUsage> getUsageReport(){
		return this.allUsages;
	}
	
	public AccumulatedUsageReport(Collection<AccumulatedUsage> allUsages){
		this.allUsages = allUsages;
		
	}
	
}
