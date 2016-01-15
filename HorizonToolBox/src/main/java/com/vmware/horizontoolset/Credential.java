package com.vmware.horizontoolset;

public class Credential {

	public Credential(){

	}

	public Credential(String username, String password, String domain){
		this.setDomain(domain);
		this.setPassword(password);
		this.setUsername(username);
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


    private String username;

    private String password;

	private String domain;


}
