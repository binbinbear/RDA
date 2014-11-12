package com.vmware.horizontoolset.email;



public class EmailServerProps {

	private String mailUser="steng";
	private String mailPassword="ca$hc0w";
	private String protocal = "smtp";
	private String mailHost="10.112.118.148";
	private String serverPort="25";
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
	

}
