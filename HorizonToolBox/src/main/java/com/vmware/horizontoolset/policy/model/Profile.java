package com.vmware.horizontoolset.policy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile {
	public Profile(){
		
	}
	public Profile(String name){
		this.setName(name);
	}
	private String name; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private CommonCategory commonCategory;
	private PCoIPCategory pcoipCategory;
	private USBCategory usbCategory;
	public CommonCategory getCommonCategory() {
		return commonCategory;
	}
	public void setCommonCategory(CommonCategory commonCategory) {
		this.commonCategory = commonCategory;
	}
	public PCoIPCategory getPcoipCategory() {
		return pcoipCategory;
	}
	public void setPcoipCategory(PCoIPCategory pcoipCategory) {
		this.pcoipCategory = pcoipCategory;
	}
	public USBCategory getUsbCategory() {
		return usbCategory;
	}
	public void setUsbCategory(USBCategory usbCategory) {
		this.usbCategory = usbCategory;
	}
}
