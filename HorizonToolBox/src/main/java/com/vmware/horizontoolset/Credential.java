package com.vmware.horizontoolset;

import java.io.UnsupportedEncodingException;

import com.vmware.vdi.vlsi.binding.vdi.util.SecureString;
import com.vmware.vim.binding.impl.vmodl.BinaryImpl;
import com.vmware.vim.binding.vmodl.Binary;


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
		return toExplicitString(password);
	}

	public void setPassword(String password) {
		this.password = toSecuredString(password);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


	private static SecureString toSecuredString(String s) {
		try {
			Binary b = new BinaryImpl(s.getBytes("UTF-8"));
			return new SecureString(b);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static String toExplicitString(SecureString s) {
		try {
			return new String(s.getUtf8String().asArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
    private String username;

    private SecureString password;

	private String domain;

	
}
