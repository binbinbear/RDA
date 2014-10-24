package com.vmware.horizontoolset.dbgenerator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vmware.horizontoolset.util.LDAP;

public class LDAPTest {

	@Test
	public void testCEIP() {
		LDAP ldap = new LDAP("10.117.160.101", "asdf", "asdf");
		boolean isEanbled = ldap.isCEIPEnabled();
		if (isEanbled){
			ldap.disableCEIP();
			assertFalse(ldap.isCEIPEnabled());
			ldap.enableCEIP();
			assertTrue(ldap.isCEIPEnabled());
		}else{
			ldap.enableCEIP();
			assertTrue(ldap.isCEIPEnabled());
			ldap.disableCEIP();
			assertFalse(ldap.isCEIPEnabled());
			
		}
	}

}
