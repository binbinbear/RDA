using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Reflection;

namespace HRALauncher
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            string[] args = Environment.GetCommandLineArgs();

            if (args.Length != 2)
            {
                Logger.Log("Incorrect argument count.");
                return;
            }

            LogEnvironment();

            string invitationFileName = args[1];
            Logger.Log(invitationFileName);
            Logger.Log("");

            try
            {
                using (HraInvitationLauncher inv = HraInvitationLauncher.Load(invitationFileName))
                {
                    inv.SaveIncidentFile();
                    inv.LaunchMSRA();
                }
            }
            catch (Exception e)
            {
                Logger.Log(e);
            }

            Logger.Log("Exit");
            Application.Exit();
        }

        static void LogEnvironment()
        {
            Logger.Log(Assembly.GetExecutingAssembly().GetName().ToString());
            Logger.Log("Time: " + DateTime.Now);
            Logger.Log("Current Directory: " + Environment.CurrentDirectory);
            Logger.Log("Machine Name: " + Environment.MachineName);
            Logger.Log("OS Version: " + Environment.OSVersion);
            Logger.Log("User Domain Name: " + Environment.UserDomainName);
            Logger.Log("User Name: " + Environment.UserName);
            Logger.Log("Version: " + Environment.Version);
            Logger.Log("System Directory: " + Environment.SystemDirectory);
        }
    }
}
