using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace ETEUtils
{
    public class HorizonViewUtil
    {
        public static string[] GetBrokerAddresses()
        {
            string key = @"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration";
            Log.Info("Reading broker address from registry: " + key);

            object val = Registry.GetValue(key, "Broker", null);
            if (val == null)
            {
                Log.Info("Broker addr from registry is null.");
                return null;
            }
            Log.Info("Broker addr: " + val);

            string brokerAddrs = val.ToString().Trim();
            Log.Info("Brokers from registry: " + brokerAddrs);

            if (brokerAddrs.Equals(String.Empty))
                return null;

            string[] addrs = brokerAddrs.Split(' ');

            List<string> ret = new List<string>();
            foreach (string s in addrs)
            {
                if (s == null)
                    continue;
                string s2 = s.Trim();
                if (s2.Length == 0)
                    continue;
                ret.Add(s2);
            }
            return ret.ToArray();
        }
    }
}
