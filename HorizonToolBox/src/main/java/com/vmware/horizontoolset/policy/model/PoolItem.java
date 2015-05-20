package com.vmware.horizontoolset.policy.model;

public class PoolItem {
	
	public PoolItem(){}
	
	public PoolItem(String name) {
		this.name = name;
	}
	
	public PoolItem(String name, String ou) {
		this.name = name;
		this.ou = ou;
	}
	
	public String name;
	public String ou;	//
}
