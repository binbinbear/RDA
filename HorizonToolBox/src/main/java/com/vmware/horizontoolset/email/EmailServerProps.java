package com.vmware.horizontoolset.email;



public class EmailServerProps {

	private String mailUser="";
	private String mailPassword="";
	private String protocal = "smtp";
	private String mailHost="";
	private String serverPort="25";
	
	
	private String toAddress;
	
	//key=value,key=value
	private String customizedProperty;
	
	
	public String getCustomizedProperty() {
		return customizedProperty;
	}
	public void setCustomizedProperty(String customizedProperty) {
		this.customizedProperty = customizedProperty;
	}
	private boolean isAuth = true;

	public String getMailUser() {
		return mailUser;
	}
	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}
	public String getMailPassword() {
		return mailPassword;
	}
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	public String getProtocal() {
		return protocal;
	}
	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	public String getMailHost() {
		return mailHost;
	}
	
	public boolean isValid(){
		return this.mailHost!=null && !this.mailHost.isEmpty() && this.mailUser!=null && !this.mailUser.isEmpty();
	}
	
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public boolean isAuth() {
		return isAuth;
	}
	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	

}
