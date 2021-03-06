package com.vmware.horizontoolset.wsproxy;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public class TrustManagerFactoryImpl extends TrustManagerFactorySpi {
    public TrustManagerFactoryImpl() { }
    public static String getAlgorithm() { return "XTrust509"; }
    protected void engineInit(KeyStore keystore) throws KeyStoreException { }
    protected void engineInit(ManagerFactoryParameters mgrparams) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException(  "Not supported");
    }
    
    protected TrustManager[] engineGetTrustManagers() {
            return new TrustManager[] {
                    new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return null; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };
    }
}