﻿using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Net;
using System.Net.Security;
using System.Reflection;
using System.Net.Sockets;
using System.IO;

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
            EmbeddedDll.Load("Newtonsoft.Json.dll");

            string[] args = Environment.GetCommandLineArgs();

            foreach (string a in args)
            {
                string arg = a.ToLower();
                if ("-server".Equals(arg))
                {
                    Logger.Log("Start server mode");
                    Server.Start();
                    Application.Exit();
                    return;
                }

                if ("-form".Equals(arg))
                {
                    Logger.Log("Start form mode");

                    Application.EnableVisualStyles();
                    Application.SetCompatibleTextRenderingDefault(false);

                    Application.Run(new Form1());
                    Application.Exit();
                    return;
                }

                if (arg.StartsWith("-postfile:"))
                {
                    string debugFile = arg.Substring("-postfile:".Length);
                    HraInvitation._debugPostFile = debugFile;
                    Logger.Log("Posting file: " + debugFile);
                    SendInvitationToBroker();
                    return;
                }
            }

            Logger.Log("Start requestor mode");

            DialogResult ret = MessageBox.Show("Do you want to invite your administrator to assist you? The request may take several seconds or minutes to be sent.", "Horizon Remote Assistance", MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
            if (ret.Equals(DialogResult.OK))
            {
                SendInvitationToBroker();
            }

            Application.Exit();
        }

        private static void SendInvitationToBroker()
        {
            //
            //  Read brokers from registry
            //
            string[] brokerAddrs = GetBrokerAddresses();
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
            string text;
            try
            {
                text = HraInvitation.create();
            }
            catch (Exception e)
            {
                MessageBox.Show("An error occured starting Remote Assistance. Details:\n" + e.ToString());
                Logger.Log(e);
                return;
            }

            text = _MassageContent(text);
            Logger.Log("-----------------Temp Inv----------------------");
            Logger.Log(text);
            Logger.Log("-----------------------------------------------");

            //
            //  Post remote assistance request (including invitation file) to AdminEx on all brokers
            //
            //Util.InitiateSSLTrust();
            Util.UriHackFix();

            int success = 0;
            foreach (string addr in brokerAddrs)
            {
                try
                {
                    if (PostRequest(addr, text))
                        success++;
                }
                catch (Exception e)
                {
                    Logger.Log(e);
                    MessageBox.Show(e.Message + ". Connection Server: " + addr);
                }
            }

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

        private static bool PostRequest(string brokerAddr, string content)
        {
            List<string> addrs = new List<string>();

            try
            {
                IPAddress[] addresses = Dns.GetHostAddresses(brokerAddr);
                foreach (IPAddress ipa in addresses)
                {
                    if (ipa.AddressFamily == AddressFamily.InterNetwork)
                        addrs.Add(ipa.ToString());
                }
            }
            catch (Exception e)
            {
                Logger.Log(e);
                return false;
            }
            addrs.Add(brokerAddr);

            Logger.Log("Posting to broker: " + brokerAddr);

            //string address = "http://10.112.119.165:8080/admin_ex/hra";

            foreach (string addr in addrs)
            {
                string url = "https://" + addr + "/admin_ex/hra";
                if (HttpGet(url, "inv", content))
                {
                    return true;
                }
            }
            return false;
        }

        private static bool HttpGet(string url, string var, string content)
        {
            using (var wb = new WebClient())
            {
                wb.Headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

                wb.QueryString.Add(var, content);

                Logger.Log("Requesting: " + url);

                try
                {
                    //we should post, however View Connection server has disabled POST.
                    //string ret = wb.UploadValues(.UploadString(url, );
                    string ret = wb.DownloadString(url);
                    Logger.Log("Response: " + ret);
                    return true;
                }
                catch (Exception ex)
                {
                    Logger.Log(ex);
                    return false;
                }
            }
        }

        private static string _MassageContent(string s)
        {
            //return Uri.EscapeDataString(s);
            //return HttpUtility.UrlEncode(s);
            return s.Replace("#", "%23").Replace("&", "%26").Replace("+", "%2B");
        }

        private static string[] GetBrokerAddresses()
        {
            string brokerAddrs = RegUtil.ReadLocalMachine(@"SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration", "Broker");
            Logger.Log("Brokers from registry: " + brokerAddrs);

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


    }
}