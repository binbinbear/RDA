using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using ETEUtils;
using DeviceFilter;

namespace DeviceFilter
{
    class Config
    {
        private static readonly string REG_KEY = @"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware Toolbox\DeviceFilter";
        private static readonly int DefaultPort = 18443;

        public int Port { get; private set; }
        public string DebugServer { get; private set; }
        public bool IsFilterMode { get; private set; }

        public bool IsDebug
        {
            get
            {
                return DebugServer != null;
            }
        }

        public static Config ProcessArgs()
        {
            try
            {
                Config conf = new Config();

                string[] args = Environment.GetCommandLineArgs();
                for (int i = 0; i < args.Length; i++)
                {
                    string arg = args[i].ToUpper();
                    if ("-VERSION".Equals(arg))
                    {
                        VersionInfo.Print();
                    }
                    else if ("-PORT".Equals(arg))
                    {
                        //expect port number
                        int? n = ExpectNumber(args, ++i);
                        if (n.HasValue)
                            conf.Port = n.Value;
                    }
                    else if ("-SERVER".Equals(arg))
                    {
                        string server = ExpectString(args, ++i);
                        if (server == null)
                            server = RegUtil.ReadString(REG_KEY, "server", null);
                        conf.DebugServer = server;
                    }
                    else if ("-CLIENT".Equals(arg))
                    {
                        conf.IsFilterMode = true;
                    }
                }

                //load default values if necessary
                if (conf.Port == 0)
                    conf.Port = RegUtil.ReadStringAsInt(REG_KEY, "port", DefaultPort);

                return conf;
            }
            catch (Exception e)
            {
                Log.Error(e);
            }
            return null;
        }

        private static int? ExpectNumber(string[] args, int i)
        {
            if (i >= args.Length)
                return null;

            string val = args[i];
            if (val.StartsWith("-"))
                return null;

            int n;
            if (!int.TryParse(val, out n))
                throw new Exception("Invalid number argument: " + val);
            return n;
        }

        private static string ExpectString(string[] args, int i)
        {
            if (i >= args.Length)
                return null;

            string val = args[i];
            if (val.StartsWith("-"))
                return null;

            return val;
        }
    }
}
