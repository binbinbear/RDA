package com.vmware.horizontoolset.wsproxy;

import java.security.AccessController;
import java.security.PrivilegedAction;


public final class XTrustProvider extends java.security.Provider {
        
        /**
	 * 
	 */
	private static final long serialVersionUID = -1084848138230801221L;
		public final static String XTRUSTPROVIDER = "XTrustAllProvider";
        private final static String INFO = "XTrust All Provider (implements trust factory with truststore validation disabled)";
        private final static double VERSION = 1.0D;
        
        public XTrustProvider() {
                super(XTRUSTPROVIDER, VERSION, INFO);
                
                AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                                put("TrustManagerFactory." + TrustManagerFactoryImpl.getAlgorithm(), TrustManagerFactoryImpl.class.getName());
                                return null;
                        }
                });
        }
        

}