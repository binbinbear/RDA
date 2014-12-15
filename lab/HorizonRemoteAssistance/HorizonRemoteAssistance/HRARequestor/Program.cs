using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Net;
using System.Net.Security;
using System.Reflection;
using System.Net.Sockets;
using System.IO;
using ETEUtils;
using Microsoft.Win32;

namespace HRARequestor
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Log.InitInLocalAppData("VMware\\Horizon Remote Assistance\\", "requestor.log");

            try
            {
                LogEnvironment();

                Config conf = Config.ProcessArgs();
                if (conf == null)
                {
                    Log.Error("Invalid argumetns");
                    return;
                }

                Log.Info("Conf.port=" + conf.Port);

                if (conf.IsServerMode)
                {
                    Log.Info("Start server mode");
                    Server.Start(conf.Port, conf.ServerKey);
                }
                else
                {
                    Log.Info("Start requestor mode");
                    DialogResult ret = MessageBox.Show("Do you want to invite your administrator to assist you? The request may take several seconds or minutes to be sent.", "Horizon Remote Assistance", MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
                    if (ret.Equals(DialogResult.OK))
                        SendInvitationToBroker(conf.Port);
                }
            }
            catch (Exception e)
            {
                Log.Error(e);
            }
            Log.Info("Exit");
            Application.Exit();
        }

        private static void LogEnvironment()
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

        private static void SendInvitationToBroker(int port)
        {
            //
            //  Read brokers from registry
            //
            string[] brokerAddrs = HorizonViewUtil.GetBrokerAddresses();
            if (brokerAddrs == null || brokerAddrs.Length == 0)
            {
                MessageBox.Show("Broker address not found in registry. Contact your administrator");
                return;
            }

            //
            //  Precondition checking
            //
            CheckEnvironment();

            //
            //  Start remote assistance (MSRA), and generate invitation file
            //
            HraInvitation inv ;
            try
            {
                inv = HraInvitation.create();
            }
            catch (Exception e)
            {
                MessageBox.Show("An error occured starting Remote Assistance. Details:\n" + e.ToString());
                Log.Info(e);
                return;
            }

            String text = _MassageContent(inv.ToJson());
            Log.Info("-----------------Temp Inv----------------------");
            Log.Info(text);
            Log.Info("-----------------------------------------------");

            //
            //  Post remote assistance request (including invitation file) to AdminEx on all brokers
            //
            string[] urls = new string[brokerAddrs.Length];
            for (int i = 0; i < brokerAddrs.Length; i++)
                urls[i] = "https://" + brokerAddrs[i] + ":" + port + "/toolbox/remoteassist/upload";

            HttpUtil._IgnoreSSL();
            HttpUtil.UriHackFix();
            HttpUtil.SetDefaultConnectionLimit(20);
            HttpUtil.SetNoProxy();  //no proxy between View Agent and View Broker. Set no proxy will boost performance for his app.
            Dictionary<string, string> parameters = new Dictionary<string, string>();
            parameters.Add("inv", text);
            int success = HttpUtil.BatchGet(urls, parameters);

            //
            //  Show result
            //
            string msg;
            MessageBoxIcon icon;
            if (success > 0)
            {
                msg = "Remote assistance request has been sent. Contact your administrator to start remote assistance.";
                icon = MessageBoxIcon.Information;
            }
            else
            {
                msg = "Fail posting remote assistance request to View Connection servers.";
                icon = MessageBoxIcon.Error;
            }
            msg += " \r\n(Success " + success + " of " + brokerAddrs.Length + ")";
            MessageBox.Show(msg, "Horizon Remote Assistance", MessageBoxButtons.OK, icon);
        }

        private static void CheckEnvironment()
        {
            
        }


        private static string _MassageContent(string s)
        {
            //return Uri.EscapeDataString(s);
            //return HttpUtility.UrlEncode(s);
            return s.Replace("#", "%23").Replace("&", "%26").Replace("+", "%2B");
        }

    }
}
