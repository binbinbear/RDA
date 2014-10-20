using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using System.Threading;

namespace HRALauncher
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            string[] args = Environment.GetCommandLineArgs();

            if (args.Length != 2)
                return;

            string invitationFileName = args[1];

            //invitationFileName = invitationFileName + ".msrcIncident";

            Logger.Log(invitationFileName);

            //File.Move(invitationFileName, newName);

            //MessageBox.Show(newName);

            Logger.Log("Time: " + DateTime.Now);
            Logger.Log("Current Directory: " + Environment.CurrentDirectory);
            Logger.Log("Machine Name: " + Environment.MachineName);
            Logger.Log("OS Version: " + Environment.OSVersion);
            Logger.Log("User Domain Name: " + Environment.UserDomainName);
            Logger.Log("User Name: " + Environment.UserName);
            Logger.Log("Version: " + Environment.Version);
            Logger.Log("System Directory: " + Environment.SystemDirectory);
            Logger.Log("");
            Logger.Log("Starting MSRA...");
            Process proc = new Process();
            proc.StartInfo.FileName = "msra.exe";
            proc.StartInfo.Arguments = "/openfile \"" + invitationFileName + "\"";
            proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

            proc.Start();

            LoginAutomator.FillPasswordAndProceed(proc);

            Logger.Log("Waiting for MSRA complete...");
            proc.WaitForExit();

            Logger.Log("Clean up...");
            try
            {
                File.Delete(invitationFileName);
            }
            catch (Exception e)
            {
                Logger.Log(e);
            }

            Logger.Log("Exit");
            Application.Exit();
        }

    }
}
