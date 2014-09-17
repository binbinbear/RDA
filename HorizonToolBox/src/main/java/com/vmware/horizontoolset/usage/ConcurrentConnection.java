package com.vmware.horizontoolset.usage;

import java.util.Date;

public class ConcurrentConnection {
	private Date date;
	public Date getDate() {
		return date;
	}

	public int getConcurrent() {
		return concurrent;
	}

	private int concurrent;
	
	public ConcurrentConnection(Date date, int concurrent){
		this.date = date;
		this.concurrent = concurrent;
	}
	
}
