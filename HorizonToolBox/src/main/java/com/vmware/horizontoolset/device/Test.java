package com.vmware.horizontoolset.device;

import com.vmware.horizontoolset.util.LDAP;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LDAP ldap = new LDAP("localhost", "asdf", "asdf", "asdf");
		
		String key = "toolbox.device.test";
		int n = ldap.getInt(key, 0);
		
		System.out.println(n);
		
		ldap.setAttribute(key, n + 1);
		
		ldap.close();
	}

}
