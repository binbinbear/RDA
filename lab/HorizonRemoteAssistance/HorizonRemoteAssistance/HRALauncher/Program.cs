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

            Log.InitInLocalAppData("VMware\\Horizon Remote Assistance", "launcher", "log", true);

            if (args.Length != 2)
            {
                Log.Error("Incorrect argument count.");
                return;
            }

            LogEnvironment();

            string invitationFileName = args[1];
            Log.Info(invitationFileName);
            Log.Info("");

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
                Log.Error(e);
            }

            Log.Info("Exit");
            Application.Exit();
        }

        static void LogEnvironment()
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
    }
}
