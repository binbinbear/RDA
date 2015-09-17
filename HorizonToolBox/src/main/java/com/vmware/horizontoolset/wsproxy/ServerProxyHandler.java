package com.vmware.horizontoolset.wsproxy;

import java.security.Security;

import org.apache.log4j.Logger;




public class ServerProxyHandler extends BaseWebSocketProxyHandler {
	// server proxy handler is a mere redirect tunnel. No additional logic.
	private static Logger log = Logger.getLogger(ServerProxyHandler.class);

	//TODO: here I trust all ssl certificates. We may need to set trusted certification
	public ServerProxyHandler(){
		trustAll();
	}
		private void trustAll(){
		

        if(Security.getProvider(XTrustProvider.XTRUSTPROVIDER) == null) {
        	log.debug("Start to insert provider");
        	Security.insertProviderAt(new XTrustProvider(), 2);
        	log.debug("Start to set algorithm");
                Security.setProperty("ssl.TrustManagerFactory.algorithm", TrustManagerFactoryImpl.getAlgorithm());
        }

	}
}
