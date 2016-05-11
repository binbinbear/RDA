using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ETEUtils;

namespace HRAUnsolicited
{
    public class UnsolicitedConfig
    {
        public string RemoteHost { get; set; }
        public string Domain { get; set; }
        public string RemoteLoginUser { get; set; }
        public string PwdLoginUser { get; set; }

        public static UnsolicitedConfig ProcessArgs()
        {
            try
            {
                UnsolicitedConfig conf = new UnsolicitedConfig();
                string[] args = Environment.GetCommandLineArgs();

                for (int i = 0; i < args.Length; i++)
                {
                    string arg = args[i].ToUpper();
                    if ("-HOST".Equals(arg))
                    {
                        conf.RemoteHost =  ExpectString(args, ++i);
                    }
                    else if("-DOMAIN".Equals(arg))
                    {
                       conf.Domain = ExpectString(args, ++i);
                    }
                    else if("-USER".Equals(arg))
                    {
                        conf.RemoteLoginUser = ExpectString(args, ++i);
                    }
                    else if("-PASSWORD".Equals(arg))
                    {
                        conf.PwdLoginUser = ExpectString(args, ++i);
                    }
                }
                Log.Info("Process arguments done. " + conf.ToString());
                return conf;
            }
            catch(Exception e)
            {
                Log.Error(e);
            }

            return null;
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

        public override string ToString()
        {
            return string.Format("[UnsolicitedConfig]-{{RemoteHost: {0}, Domain: {1}, RemoteLoginUser: {2}}}",
                RemoteHost, Domain, RemoteLoginUser);
        }

        public Boolean Validate()
        {
            return true;
        }
    }
}
