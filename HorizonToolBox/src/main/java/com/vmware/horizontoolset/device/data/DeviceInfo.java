package com.vmware.horizontoolset.device.data;

public class DeviceInfo {

    public String ViewClient_Client_ID;
    public String ViewClient_Type;
    public String ViewClient_IP_Address;
    public String ViewClient_Launch_ID;
    public String ViewClient_MAC_Address;

    public String ViewClient_LoggedOn_Username;
    public String ViewClient_LoggedOn_Domainname;
    public String ViewClient_Launch_SessionType;
    public String ViewClient_Machine_Domain;
    public String ViewClient_Machine_Name;

    //client computer attributes
    public String UserName;         
    public String UserDomain;
    public String UserDnsDomain;
    
    
	public boolean isSameDevice(DeviceInfo di) {
		return ViewClient_Client_ID.equals(di.ViewClient_Client_ID);
	}

	public boolean isValid() {
		return ViewClient_Client_ID != null;
	}
}
