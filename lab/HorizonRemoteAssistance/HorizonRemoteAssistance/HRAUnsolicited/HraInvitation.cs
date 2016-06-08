using System;
using System.Collections.Generic;
using System.IO;
using ETEUtils;
using CreateRAString;
using Newtonsoft.Json;
using System.Diagnostics;

namespace HRAUnsolicited
{
    public class HraInvitation
    {
        public string inv;
        public string code;
        public string remoteHost;
        public string remoteLoginUser;
     
        private HraInvitation(UnsolicitedConfig conf)
        {
            remoteHost = conf.RemoteHost;
            remoteLoginUser = conf.RemoteLoginUser;
        }


        public static HraInvitation create(UnsolicitedConfig conf)
        {
            HraInvitation inst = new HraInvitation(conf);
            inst.start();

            return inst;
        }


        private void start()
        {
            killAllRemoteAssistProcesses();

            this.inv = RATicketGenerator.RequestRATicket(remoteHost);
        }

        public string ToJson()
        {
            return JsonConvert.SerializeObject(this);
        }


        public void killAllRemoteAssistProcesses()
        {
            Process p1 = new Process();

            p1.StartInfo.FileName = "cmd.exe";
            p1.StartInfo.UseShellExecute = false;
            p1.StartInfo.RedirectStandardInput = true;
            p1.StartInfo.RedirectStandardOutput = true;
            p1.StartInfo.RedirectStandardError = true;
            p1.StartInfo.CreateNoWindow = true;
            p1.Start();
            p1.StandardInput.WriteLine(@"TASKKILL /S {0} /F /IM msra.exe", remoteHost);
            p1.StandardInput.WriteLine(@"exit");
            p1.WaitForExit(40000);      // 40 seconds

        }
    }
}
