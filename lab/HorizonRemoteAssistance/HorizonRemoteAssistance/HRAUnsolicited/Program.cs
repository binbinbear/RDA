using System;
using System.Collections.Generic;
using System.Text;
using ETEUtils;
using System.Diagnostics;
using System.Threading;

namespace HRAUnsolicited
{
    class Program
    {
        private static readonly string SERVER_CONTEXT_PATH = "/toolbox/remoteassist/upload";
        private static readonly string HOT_FIX = "";
        public static readonly string TITLE = "Horizon Remote Assistance";
        /// <summary>
        /// 
        static void Main(string[] args)
        {
            try
            {
               // Thread.Sleep(1000 * 1000);
                InitLog();
            }
            catch (Exception e)
            {
                Log.Error("Fail initializing log: " + e.Message);
                return;
            }

            try
            {
                LogEnvironment();

                Log.Info("Start unsolicited requestor mode");
                UnsolicitedConfig conf = UnsolicitedConfig.ProcessArgs();
                if ((conf == null) || (false == conf.Validate()))
                {
                    Log.Error("Invalid input arguments");
                    if (conf != null)
                    {
                        Log.Error(conf.ToString());
                    }
                    
                    return;
                }
                SendInvitationToBroker(conf);
            }
            catch (Exception e)
            {

            }
            Log.Info("Exit");
        }

        private static void InitLog()
        {
            string userName = Environment.UserName;
            string fileName = "Unsocilicited_" + userName + "_" + Process.GetCurrentProcess().Id.ToString();
            string path = @"c:\\ra\\";
            Log.Init(path, fileName, "log", false);
        }

        private static void LogEnvironment()
        {
            Log.Info("Time: " + DateTime.Now);
            Log.Info("Current Directory: " + Environment.CurrentDirectory);
            Log.Info("Machine Name: " + Environment.MachineName);
            Log.Info("OS Version: " + Environment.OSVersion);
            Log.Info("User Domain Name: " + Environment.UserDomainName);
            Log.Info("User Name: " + Environment.UserName);
            Log.Info("Version: " + Environment.Version);
            Log.Info("System Directory: " + Environment.SystemDirectory);
            Log.Info("Process Id: " + Process.GetCurrentProcess().Id.ToString());
        }

        
        public static void SendInvitationToBroker(UnsolicitedConfig conf)
        {
            //
            //  Start remote assistance (MSRA), and generate invitation file
            //
            HraInvitation inv;
            try
            {
                inv = HraInvitation.create(conf);
            }
            catch (Exception e)
            {
                //MessageBox.Show("An error occured starting Remote Assistance. Make sure Windows Remote Assistance feature is enabled and firewall rule is configured.\nDetails:\n" + e.ToString(), GetTitle());
                Log.Info(e);
                return;
            }
            Log.Info("-----------------Before to Json----------------------");
            Log.Info(inv.inv);
            Log.Info("-----------------------------------------------");

            String text = inv.ToJson();
            Log.Info("-----------------Temp Inv----------------------");
            Log.Info(text);
            Log.Info("-----------------------------------------------");

            // Send result to parent process
            SendResult2ParentProcess(text);

            //UploadJsonTicket(text);
        }

        private static void SendResult2ParentProcess(String text)
        {
            string sHead = "MSGHEAD--";
            string sTail = "--MSGTAIL";
            String msg = sHead + text + sTail;
            Console.WriteLine(msg);
        }


        private static void UploadJsonTicket(String jsonText)
        {

            String text = _MassageContent(jsonText);
            //
            //  Post remote assistance request (including invitation file) to AdminEx on all brokers
            //
            string[] urls = new string[1];
            for (int i = 0; i < 1; i++)
                urls[i] = "https://" + "10.112.115.27" + ":" + 18443 + SERVER_CONTEXT_PATH;

            //if (!conf.UseSSL)
            HttpUtil._IgnoreSSL();

            HttpUtil.UriHackFix();
            HttpUtil.SetDefaultConnectionLimit(20);
            HttpUtil.SetNoProxy();  //no proxy between View Agent and View Broker. Set no proxy will boost performance for his app.
            Dictionary<string, string> parameters = new Dictionary<string, string>();
            parameters.Add("inv", text);
            int success = HttpUtil.BatchGet(urls, parameters, "OK", false);
        }

        private static string _MassageContent(string s)
        {
            //return Uri.EscapeDataString(s);
            //return HttpUtility.UrlEncode(s);
            return s.Replace("#", "%23").Replace("&", "%26").Replace("+", "%2B");
        }

  
    }
}
