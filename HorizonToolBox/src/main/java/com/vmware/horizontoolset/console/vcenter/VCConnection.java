package com.vmware.horizontoolset.console.vcenter;


import com.vmware.vim25.mo.ServiceInstance;
public interface VCConnection {
    // getters and setters

    String getHost();




    VCConnection connect();

    boolean isConnected();
    VCConnection disconnect();
    
    ServiceInstance getServiceInstance();

}

