using System;
using System.Collections.Generic;
using System.Windows.Forms;
using ETEUtils;
using System.Diagnostics;
using Newtonsoft.Json.Linq;

namespace DeviceFilter
{
    static class Program
    {
        private static string _testServer;
        private static string VERSION = "10-22-2014";

        private static bool isDebug()
        {
            return _testServer != null;
        }

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            string[] args = Environment.GetCommandLineArgs();

            if (args.Length < 2 || !args[1].Equals("-client", StringComparison.OrdinalIgnoreCase))
                return;

            if (args.Length > 2)
            {
                string s = args[2];
                if (s.Equals("-version", StringComparison.OrdinalIgnoreCase))
                {
                    MessageBox.Show("Device Filter: " + VERSION);
                    return;
                }

                _testServer = args[2];
            }

            if (isDebug())
                Log.Init();
            else
                Log.InitInLocalAppData("VMware\\Horizon Toolbox", "DeviceFilter.log");

            try
            {

                if (!queryAnyBroker())
                {
                    Log.Info("Disconnect");
                    if (isDebug())
                    {
                        MessageBox.Show("DEBUG: skip disconnect");
                    }
                    else
                    {
                        Process.Start("tsdiscon.exe");
                    }
                }
                else
                {
                    Log.Info("Passed");
                }
            }
            catch (Exception e)
            {
                Log.Error(e);
            }
            Log.Info("exit");
        }

        private static bool queryAnyBroker()
        {
            string deviceInfoStr = GetDeviceInfoJson();

            Log.Info("Device Info: " + deviceInfoStr);

            //DEBUG start
            if (isDebug())
            {
                return PostDeviceInfoToServer(_testServer, deviceInfoStr);
            }
            //DEBUG end

            string[] brokers = HorizonViewUtil.GetBrokerAddresses();
            if (brokers == null)
            {
                Log.Info("Broker not found in registry");
                return false;
            }

            HttpUtil._IgnoreSSL();

            foreach (string broker in brokers)
            {
                Log.Info("Querying broker: " + broker);

                List<string> addrs = HttpUtil.resolveIPv4Addrs(broker);

                foreach (string addr in addrs)
                {
                    try
                    {
                        if (PostDeviceInfoToServer(addr, deviceInfoStr))
                        {
                            return true;
                        }
                    }
                    catch (Exception e)
                    {
                        Log.Info(e);
                        continue;
                    }
                }
            }

            return false;
        }

        private static bool PostDeviceInfoToServer(string host, string value)
        {
            string url = isDebug() ? "http://" : "https://";
            url += host + "/toolbox/deviceFilter/check";

            return HttpUtil.Get(url, "di", value, "ACCESS_ALLOWED");
        }

        private static string GetDeviceInfoJson()
        {
            DeviceInfo di = DeviceInfo.LoadFromLocalSystem();
            JObject jobject = JObject.FromObject(di);
            string json = jobject.ToString();

            //encode URI component
            json = json.Replace("#", "%23").Replace("&", "%26").Replace("+", "%2B");

            return json;
        }
    }
}
