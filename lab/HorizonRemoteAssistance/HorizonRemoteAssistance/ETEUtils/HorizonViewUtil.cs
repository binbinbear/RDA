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
            string brokerAddrs = Registry.GetValue(@"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration", "Broker", String.Empty).ToString();
            Log.Info("Brokers from registry: " + brokerAddrs);

            if (brokerAddrs == null || brokerAddrs.Equals(String.Empty))
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
