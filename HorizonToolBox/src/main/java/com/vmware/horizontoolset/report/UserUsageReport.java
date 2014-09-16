package com.vmware.horizontoolset.report;

import java.util.ArrayList;
import java.util.List;

import com.vmware.horizontoolset.usage.Connection;

public class UserUsageReport extends AbstractReport{

	private String username;
	private List<Connection> connections = new ArrayList<Connection>();
	
	
	public UserUsageReport(String username){
		this.username = username;
	}
	
	
	public void addConnection(Connection connection){
		this.connections.add(connection);
	}
	
	public String getUsername(){
		return this.username;
	}
	
	
	public List<Connection> getConnections(){
		return this.connections;
	}
}
