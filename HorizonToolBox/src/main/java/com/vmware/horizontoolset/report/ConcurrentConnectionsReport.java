package com.vmware.horizontoolset.report;

import java.util.Collection;
import com.vmware.horizontoolset.usage.ConcurrentConnection;

public class ConcurrentConnectionsReport extends AbstractReport{
	private Collection<ConcurrentConnection> concurrentConnections;
	public ConcurrentConnectionsReport(Collection<ConcurrentConnection> concurrentConnections){
		this.concurrentConnections = concurrentConnections;
	}
	public Collection<ConcurrentConnection> getConcurrentConnections() {
		return concurrentConnections;
	}
	
	
}
