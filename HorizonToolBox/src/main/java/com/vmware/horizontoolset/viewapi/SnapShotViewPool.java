package com.vmware.horizontoolset.viewapi;

import com.vmware.horizontoolset.report.SnapShotReport;

public interface SnapShotViewPool extends ViewPool{
	//return a table
	public String getInformation();
	
	public void upateReport(SnapShotReport report);
}
