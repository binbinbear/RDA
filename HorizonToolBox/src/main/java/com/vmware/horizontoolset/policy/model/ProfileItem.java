package com.vmware.horizontoolset.policy.model;

import java.util.concurrent.atomic.AtomicInteger;

public class ProfileItem {
	
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
