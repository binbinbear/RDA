package com.vmware.horizontoolset.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class LDAPTest {

	@Test
	public void testCEIP() {
		String asd = "12.3";
		String[] asdf = asd.split("[.]");
		System.out.println(asdf.length);
		for (int i=0;i<asdf.length;i++){
			System.out.println(asdf[i]);
		}
	}

}
