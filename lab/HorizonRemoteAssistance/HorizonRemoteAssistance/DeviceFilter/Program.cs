using System;
using System.Collections.Generic;
using System.Windows.Forms;
using ETEUtils;
using System.Diagnostics;
using Newtonsoft.Json.Linq;
using Microsoft.Win32;
using System.Reflection;

namespace DeviceFilter
{
    static class Program
    {
        private static Config conf;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Log.InitInLocalAppData("VMware\\Horizon Toolbox", "DeviceFilter.log");

            try
            {
                LogEnvironment();

                conf = Config.ProcessArgs();
                if (conf == null)
                {
                    Log.Error("Invalid argumetns");
                    return;
                }

                if (!conf.IsFilterMode)
                    return;
                
                Log.Info("Conf.port=" + conf.Port);

                if (!queryAnyBroker())
                {
                    Log.Info("Disconnect");
                    if (conf.IsDebug)
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
            Log.Info("Exit");
        }

        public static void LogEnvironment()
        {
            Log.Info(Assembly.GetExecutingAssembly().GetName().ToString());
            Log.Info("Time: " + DateTime.Now);
            Log.Info("Current Directory: " + Environment.CurrentDirectory);
            Log.Info("Machine Name: " + Environment.MachineName);
            Log.Info("OS Version: " + Environment.OSVersion);
            Log.Info("User Domain Name: " + Environment.UserDomainName);
            Log.Info("User Name: " + Environment.UserName);
            Log.Info("Version: " + Environment.Version);
            Log.Info("System Directory: " + Environment.SystemDirectory);
        }

        private static bool queryAnyBroker()
        {
            string deviceInfoStr = GetDeviceInfoJson();

            Log.Info("Device Info: " + deviceInfoStr);

            //DEBUG start
            if (conf.IsDebug)
            {
                return PostDeviceInfoToServer(conf.DebugServer, deviceInfoStr);
            }
            //DEBUG end

            string[] brokers = HorizonViewUtil.GetBrokerAddresses();
            if (brokers == null)
            {
                Log.Info("Broker not found in registry");
                return false;
            }

            HttpUtil._IgnoreSSL();

            string[] urls = new string[brokers.Length];
            for (int i = 0; i < brokers.Length; i++)
                urls[i] = "https://" + brokers[i] + ":" + conf.Port + "/toolbox/deviceFilter/check";

            Dictionary<string, string> parameters = new Dictionary<string, string>();
            parameters.Add("di", deviceInfoStr);

            int success = HttpUtil.BatchGet(urls, parameters, "ACCESS_ALLOWED", true);

            return success > 0;
        }

        //*
        private static bool PostDeviceInfoToServer(string host, string value)
        {
            string url = conf.IsDebug ? "http://" : "https://";
            url += host + ':' + conf.Port + "/toolbox/deviceFilter/check";

            return HttpUtil.Get(url, "di", value, "ACCESS_ALLOWED");
        }
        //*/

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
