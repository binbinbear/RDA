using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using ETEUtils;

namespace HRARequestor
{
    class Config
    {
        private static readonly string REG_KEY = @"HKEY_LOCAL_MACHINE\SOFTWARE\VMware, Inc.\VMware Toolbox\HorizonRemoteAssistance";
        private static readonly string ATTR_SERVER_KEY = "serverKey";
        private static readonly string ATTR_PORT = "port";
        private static readonly string DefaultKey = "*";
        private static readonly int DefaultPort = 18443;

        public int Port { get; private set; }

        public string ServerKey { get; private set; }

        public bool IsServerMode
        {
            get 
            {
                return ServerKey != null;
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
                    if ("-SERVER".Equals(arg))
                    {
                        //expect server key
                        string k = ExpectString(args, ++i);
                        conf.ServerKey = k == null ? "" : k;
                    }
                    else if ("-PORT".Equals(arg))
                    {
                        //expect port number
                        int? n = ExpectNumber(args, ++i);
                        if (n.HasValue)
                            conf.Port = n.Value;
                    }
                }

                //load default values if necessary
                if (string.Empty.Equals(conf.ServerKey))
                    conf.ServerKey = RegUtil.ReadString(REG_KEY, ATTR_SERVER_KEY, DefaultKey);

                if (conf.Port == 0)
                    conf.Port = RegUtil.ReadStringAsInt(REG_KEY, ATTR_PORT, DefaultPort);

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
                throw new Exception("Invalid number parameter");
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
