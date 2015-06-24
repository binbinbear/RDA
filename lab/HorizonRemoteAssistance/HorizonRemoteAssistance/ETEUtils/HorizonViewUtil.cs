using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using System.Diagnostics;

namespace ETEUtils
{
    public class HorizonViewUtil
    {
        private readonly static string BROKER_ATTR = "ViewClient_Broker_DNS_Name";
        public static string[] GetBrokerAddresses()
        {
            //
            //  1. Try reading all broker addresses from agent config
            //
            string key = @"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration";
            Log.Info("Reading broker address from registry: " + key);

            object val = null;

            try
            {
                val = Registry.GetValue(key, "Broker", null);
                if (val == null)
                {
                    Log.Info("Broker addr from HKLM registry is null.");
                }
            }
            catch (Exception e)
            {
                Log.Info("Error reading broker address from HKLM: " + e.ToString());
            }

            //
            //  2. If we are out of luck (e.g. user permission), try reading broker
            //     address from current user registry
            //
            if (val == null)
            {
                Log.Info("Try reading broker address from HKCU, per session...");
                try
                {
                    int sessionId = Process.GetCurrentProcess().SessionId;
                    key = @"HKEY_CURRENT_USER\Volatile Environment\" + sessionId;

                    Log.Info("Key: " + key);

                    val = Registry.GetValue(key, BROKER_ATTR, null);
                }
                catch (Exception e)
                {
                    Log.Info(e.ToString());
                }
            }

            //
            //  3. If we are out of luck to retrieve broker info from session. Try
            //     the generic volatile environment
            //
            if (val == null)
            {
                Log.Info("Try reading broker address from HKCU, generic...");
                try
                {
                    key = @"HKEY_CURRENT_USER\Volatile Environment";

                    Log.Info("Key: " + key);

                    val = Registry.GetValue(key, BROKER_ATTR, null);
                }
                catch (Exception e)
                {
                    Log.Info(e.ToString());
                }
            }

            if (val == null)
            {
                Log.Error("Fail retrieving broker address from registry.");
                return null;
            }

            Log.Info("Broker addr: " + val);

            //
            //  4. Format as array
            //

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
