package com.vmware.horizontoolset.policy.model;


public class ProfileItem {
	//存ldap 模型
	public ProfileItem(){}
	
	public ProfileItem(String name) {
		super();
		this.name = name;
	}
	
	public ProfileItem(String name, String description) {
		super();
		//this.recordId = recordId;
		this.name = name;
		this.description = description;
	}

	
	//public int recordId;
	public String name;
	public String description;
	
	
}
