package com.vmware.horizontoolset.policy.model;


//java端 profile数据模型
public class ProfileModel {
	public String profileName;
	public String descrpiton;
	public PolicyModel[] policies;
	
	public ProfileModel(String profileName, String descrpiton,
			PolicyModel[] policies) {
		
		this.profileName = profileName;
		this.descrpiton = descrpiton;
		this.policies = policies;
	}
	
}
