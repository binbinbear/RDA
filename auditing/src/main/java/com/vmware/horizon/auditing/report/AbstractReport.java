package com.vmware.horizon.auditing.report;

import java.util.Date;

public abstract class AbstractReport {
	private Date updatedDate;
	public AbstractReport(){
		this.updatedDate =new Date();
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	public void updateDate(){
		this.updatedDate = new Date();
	}

}
