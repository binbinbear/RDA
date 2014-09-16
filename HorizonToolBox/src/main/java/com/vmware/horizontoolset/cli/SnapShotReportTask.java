package com.vmware.horizontoolset.cli;

import java.util.List;

import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.report.ReportUtil;
import com.vmware.horizontoolset.report.SnapShotReport;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;

public class SnapShotReportTask extends AbstractAPIThymeleafTask{

	private String templatePath = null;
	private String reportPath;
	public SnapShotReportTask(String server, Credential credential, String templatePath, String reportPath){
		super(server,credential);
		this.templatePath = templatePath;
		this.reportPath = reportPath;
	}
	
	@Override
	public String Execute() {
		List<SnapShotViewPool> list = service.getDetailedAutoPools();
		SnapShotReport report = ReportUtil.generateSnapShotReport(list);
		
		super.setVariable("date", report.getUpdatedDate().toString());
		super.setVariable("report", report);
		
		super.generateReport(templatePath, reportPath);
		
		return "";
	}

}
