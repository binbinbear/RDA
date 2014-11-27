using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace DeviceFilter
{
    public class DeviceInfo
    {
        public string ViewClient_Client_ID;
        public string ViewClient_Type;
        public string ViewClient_IP_Address;
        public string ViewClient_Launch_ID;
        public string ViewClient_MAC_Address;

        public string ViewClient_LoggedOn_Username;
        public string ViewClient_LoggedOn_Domainname;
        public string ViewClient_Launch_SessionType;
        public string ViewClient_Machine_Domain;
        public string ViewClient_Machine_Name;

        //client computer attributes
        public string UserName;         
        public string UserDomain;
        public string UserDnsDomain;

        public static DeviceInfo LoadFromLocalSystem()
        {
            DeviceInfo di = new DeviceInfo();

            di.UserName = Load("USERNAME");
            di.UserDomain = Load("USERDOMAIN");
            di.UserDnsDomain = Load("USERDNSDOMAIN");

            di.ViewClient_Client_ID     = Load("ViewClient_Client_ID");
            di.ViewClient_Type          = Load("ViewClient_Type");
            di.ViewClient_IP_Address    = Load("ViewClient_IP_Address");
            di.ViewClient_Launch_ID     = Load("ViewClient_Launch_ID");
            di.ViewClient_MAC_Address   = Load("ViewClient_MAC_Address");

            di.ViewClient_LoggedOn_Username     = Load("ViewClient_LoggedOn_Username");
            di.ViewClient_LoggedOn_Domainname   = Load("ViewClient_LoggedOn_Domainname");
            di.ViewClient_Launch_SessionType    = Load("ViewClient_Launch_SessionType");
            di.ViewClient_Machine_Domain        = Load("ViewClient_Machine_Domain");
            di.ViewClient_Machine_Name          = Load("ViewClient_Machine_Name");

            return di;
        }

        private static string Load(string name)
        {
            return (string)Registry.GetValue("HKEY_CURRENT_USER\\Volatile Environment", name, null);
        }
    }
}
