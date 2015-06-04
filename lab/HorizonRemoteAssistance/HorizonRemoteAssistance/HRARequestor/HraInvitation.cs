using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Diagnostics;
using System.Threading;
using System.Windows.Forms;
using Newtonsoft.Json;
using ETEUtils;
using CreateRAString;

namespace HRARequestor
{
    public class HraInvitation
    {
        private static int pid;
        private string fileName;
        private static string _DebugPostFile;
        public static string _debugPostFile { 
            set 
            {
                _DebugPostFile = File.ReadAllText(value);
            }
        }

        public string machine;
        public string user;
        public string domain;
        public string os;
        public string inv;
        public string code;
        public int nonce;

        private HraInvitation()
        {
            user = Environment.UserName;
            domain = Environment.UserDomainName;
            machine = Environment.MachineName;
            os = Environment.OSVersion.ToString();
            nonce = new Random().Next();
        }

        public static HraInvitation create()
        {
            HraInvitation inst = new HraInvitation();

            if (_DebugPostFile == null)
                //inst.start();
                inst.start_v2();
            else
                inst.inv = _DebugPostFile;

            return inst;
        }

        public void close()
        {
            File.Delete(fileName);

            Process p = Process.GetProcessById(pid);
            p.Kill();
        }

        public static void killAllRemoteAssistProcesses()
        {
            Process[] processes = Process.GetProcessesByName("msra");
            foreach (Process p in processes)
            {
                try
                {
                    if (p.MainModule.FileVersionInfo.CompanyName.Equals("Microsoft Corporation"))
                    {
                        Log.Info("Kill existing MSRA processes: " + p.Id);
                        p.Kill();
                    }
                }
                catch (Exception e)
                {
                    Log.Error(e);
                }
            }
        }

        private void start_v2()
        {
            killAllRemoteAssistProcesses();

            code = "";  //empty password
            this.inv = RATicketGenerator.RequestRATicket("127.0.0.1");

        }

        private void start()
        {
            killAllRemoteAssistProcesses();

            string path = Path.GetTempPath();
            //string path = @"Z:\remote assist\";
            fileName = path + "hra.msrcIncident";

            Log.Info("Temp invitation file: " + fileName);

            File.Delete(fileName);

            Process proc = new Process();
            proc.StartInfo.FileName = "msra.exe";
            proc.StartInfo.WorkingDirectory = path;
            string code = GenerateCode();
            proc.StartInfo.Arguments = "/saveasfile hra.msrcIncident " + code;
            //proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

            try
            {
                proc.Start();
                pid = proc.Id;

                Thread.Sleep(3000);
                if (!loadInvitationFile())
                {
                    Thread.Sleep(5000);
                    loadInvitationFile();
                }

                //proc.WaitForExit();
            }
            finally
            {
                File.Delete(fileName);
                //close();
            }
        }

        private bool loadInvitationFile()
        {
            try
            {
                inv = File.ReadAllText(fileName);
                return true;
            }
            catch (Exception e)
            {
                Log.Error("Error loading invitation from file. File name: " + fileName);
                throw e;
            }
        }

        public static Process GetMsraProcess()
        {
            return Process.GetProcessById(pid);
        }

        private string GenerateCode() 
        {
            if (code != null)
                throw new Exception("Already generated");

            const string valid = "bcdefghijklmnopqrstuvwxyBCDEFGHIJKLMNOPQRSTUVWXY23456789";
            StringBuilder res = new StringBuilder();
            Random rnd = new Random();
            for (int i = 0; i < 6; i++)
            {
                res.Append(valid[rnd.Next(valid.Length)]);
            }
            string rawCode = res.ToString();

            res = new StringBuilder();
            char[] chars = rawCode.ToCharArray();
            for (int i = chars.Length - 1; i >= 0; i--)
            {
                char c = (char)(chars[i] + (i % 2 == 0 ? 1 : -1));
                res.Append(c);
            }
            code = res.ToString();

            return rawCode;
        }

        public string ToJson()
        {
            return JsonConvert.SerializeObject(this);
        }
    }
}
