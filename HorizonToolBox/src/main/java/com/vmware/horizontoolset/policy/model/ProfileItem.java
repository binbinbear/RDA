package com.vmware.horizontoolset.policy.model;

import com.vmware.horizontoolset.policy.util.GpoCache.GpoType;


public class ProfileItem implements Comparable{

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
	
	public ProfileItem(String name, String description, GpoType type){
		super();
		this.name = name;
		this.description = description;
		this.type = type;
	}

	//public int recordId;
	public String name;
	public String description;
	public GpoType type;
	
	@Override
	public int compareTo(Object o) {
        if(this==o){
            return 0;            
        }
        else if (o!=null) {   
        	ProfileItem o1 = (ProfileItem)o;
            return this.type.compareTo(o1.type);
        }
    	else{
    		return -1;
    	}

	}
	
}