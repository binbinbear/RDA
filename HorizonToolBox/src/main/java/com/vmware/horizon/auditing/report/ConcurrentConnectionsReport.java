package com.vmware.horizon.auditing.report;

import java.util.Collection;

public class ConcurrentConnectionsReport extends AbstractReport{
	private Collection<ConcurrentConnection> concurrentConnections;
	public ConcurrentConnectionsReport(Collection<ConcurrentConnection> concurrentConnections){
		this.concurrentConnections = concurrentConnections;
	}
	public Collection<ConcurrentConnection> getConcurrentConnections() {
		return concurrentConnections;
	}
	
	
}
