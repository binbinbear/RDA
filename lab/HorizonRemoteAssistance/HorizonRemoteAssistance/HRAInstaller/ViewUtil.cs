using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using System.IO;

namespace HRAInstaller
{
    class ViewUtil
    {
        public static bool IsViewAgentInstalled()
        {
            var brokers = Registry.GetValue(@"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration", "Broker", null);
            return brokers != null;
        }

        private static string[] GetBrokerAddresses()
        {
            string brokerAddrs = (string) Registry.GetValue(@"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration", "Broker", null);
            if (brokerAddrs == null || brokerAddrs.Trim().Equals(""))
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

        public static bool IsViewConnectionServerInstalled()
        {
            return GetViewConnectionServerInstallPath() != null;
        }

        public static string GetViewConnectionServerInstallPath()
        {
            string dir = (string) Registry.GetValue(@"HKEY_LOCAL_MACHINE\Software\VMware, Inc.\VMware VDM", "ServerInstallPath", null);
            if (Directory.Exists(dir))
                return dir;
            return null;
        }

        public static string GetViewConnectionServerAppPath()
        {
            string val = GetViewConnectionServerInstallPath();
            if (val == null)
            {
                string defaultPath = @"C:\Program Files\VMware\VMware View\Server\broker\webapps";
                if (Directory.Exists(defaultPath))
                    val = defaultPath;
            }
            else
                val += @"broker\webapps";
            return val;
        }

    }
}
