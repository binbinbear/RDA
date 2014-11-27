using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Diagnostics;
using System.Threading;
using System.Windows.Forms;
using Newtonsoft.Json.Linq;

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

        private HraInvitation()
        {
            user = Environment.UserName;
            domain = Environment.UserDomainName;
            machine = Environment.MachineName;
            os = Environment.OSVersion.ToString();
        }

        public static string create()
        {
            HraInvitation inst = new HraInvitation();

            if (_DebugPostFile == null)
                inst.start();
            else
                inst.inv = _DebugPostFile;

            JObject jobject = JObject.FromObject(inst);

            return jobject.ToString();
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
                        Logger.Log("Kill existing MSRA processes: " + p.Id);
                        p.Kill();
                    }
                }
                catch (Exception e)
                {
                    Logger.Log(e);
                }
            }
        }

        private void start()
        {
            killAllRemoteAssistProcesses();

            string path = Path.GetTempPath();
            //string path = @"Z:\remote assist\";
            fileName = path + "hra.msrcIncident";

            Logger.Log("Temp invitation file: " + fileName);

            File.Delete(fileName);

            Process proc = new Process();
            proc.StartInfo.FileName = "msra.exe";
            proc.StartInfo.WorkingDirectory = path;
            proc.StartInfo.Arguments = "/saveasfile hra.msrcIncident ******";
            proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

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

                File.Delete(fileName);

                //proc.WaitForExit();
            }
            finally
            {
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
            catch (Exception)
            {
                return false;
            }
        }

        public static Process GetMsraProcess()
        {
            return Process.GetProcessById(pid);
        }
    }
}
