package com.vmware.horizontoolset.email;



public class EmailContentProps {
	private String toAddress;
	public String getToAddress() {
		return toAddress;
	}
	
	private String title="Email notification from toolbox";
	private String body="You message is here";
	
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	/**
	 * 
	 * @param toAddress   separated by ","
	 */
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	
}
