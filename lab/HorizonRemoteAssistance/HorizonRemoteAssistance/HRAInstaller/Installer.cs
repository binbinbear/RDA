using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using System.Diagnostics;
using System.Threading;
using System.IO;
using System.Windows.Forms;

namespace HRAInstaller
{
    class Installer
    {

        public const int RET_OK = 0;
        public const int RET_CANCELED = 1;
        public const int RET_INVALID_ARG = 2;
        public const int RET_ERROR = 3;
        public const int RET_VIEW_INSTALLATION_NOT_FOUND = 4;
        public const int RET_VIEW_DIR_NOT_EXIST = 5;

        public delegate void MsgHandler(string s);

        class Step
        {
            internal delegate void func();

            internal func f;
            internal string message;
        }

        private static Step[] installForEndUser;
        private static Step[] installForAdmin;
        private static Step[] installLauncher;
        private static Step[] uninstall;
        private static Step[] targetSteps;
        private static int currentStep;
        private static MsgHandler msgHandler;
        public static int returnCode = RET_CANCELED;

        public static void Init(MsgHandler msgHandler)
        {
            Installer.msgHandler = msgHandler;

            installForEndUser = new Step[] {
                new Step {message="Installing for virtual desktop..."},
                new Step {f=CopyHRARequestorToAllUserDesktop, message="Install Horizon Remote Assistance to desktop (all user)..."},
                new Step {f=EnableWindowsRAFeatureForClient, message="Enable Windows Feature (Win2008): Remote Assistance..."},
                new Step {f=AllowRemoteAssistance, message="Allow receiving remote assistance..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."}

            };

            installForAdmin = new Step[] {
                new Step {message="Installing for helpdesk..."},
                //new Step {f=InstallAdminEx, message="Install AdminEx web model into View Connection Server..."},
                new Step {f=EnableWindowsRAFeature, message="Enable Windows Feature: Remote Assistance..."},
                new Step {f=CopyHRALauncher, message="Copy HRA launcher..."},
                new Step {f=RegisterHRAFileType, message="Associate HRA file type..."},
                //new Step {f=InstallAdminExShortcutOnDesktop, message="Install AdminEx shortcut on desktop..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."},
                //new Step {f=CheckAdminEx, message="Checking AdminEx web model availability..."},
            };

            installLauncher = new Step[] {
                new Step {message="Installing launcher only, for administrator..."},
                new Step {f=EnableWindowsRAFeature, message="Enable Windows Feature: Remote Assistance..."},
                new Step {f=CopyHRALauncher, message="Copy HRA launcher..."},
                new Step {f=RegisterHRAFileType, message="Associate HRA file type..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."},
            };

            uninstall = new Step[] {
                new Step {message="Uninstalling..."},
                new Step {f=RemoveAdminEx, message="Remove AdminEx web model from View Connection Server..."},
                new Step {f=DeregisterHRAFileType, message="Remove HRA file type association..."},
                new Step {f=RemoveHRALauncher, message="Remove HRA launcher..."},
                new Step {f=RemoveAdminExShortcutFromDesktop, message="Remove AdminEx shortcut on desktop..."},
                new Step {f=RemoveHRARequestorFromAllUserDesktop, message="Remove Horizon Remote Assistance from desktop (all user)..."},
                new Step {f=RemoveAdminExGenerated, message="Remove AdminEx web model generated..."},
            };
        
            
            switch (Config.installType)
            {
                case Config.INSTALL_FOR_END_USER:
                    targetSteps = installForEndUser;
                    break;
                case Config.INSTALL_FOR_ADMIN:
                    targetSteps = installForAdmin;
                    break;
                case Config.INSTALL_LAUNCHER:
                    targetSteps = installLauncher;
                    break;
                default:
                    targetSteps = uninstall;
                    break;
            }
            currentStep = 0;
        }

        internal static bool RunNextStep()
        {
            if (currentStep >= targetSteps.Length)
            {
                if (!HasError())
                    Installer.returnCode = Installer.RET_OK;
                return false;
            }

            Step s = targetSteps[currentStep++];

            msg(s.message);

            if (s.f != null)
            {
                try
                {
                    s.f();
                }
                catch (Exception e)
                {
                    msg("! ERROR: " + e.Message);
                    returnCode = RET_ERROR;
                }
            }

            return !HasError();
        }

        internal static bool HasError()
        {
            return returnCode != RET_OK && returnCode != RET_CANCELED;
        }

        internal static void msg(string s)
        {
            msgHandler(s);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        public static void EnableWindowsRAFeatureForClient()
        {
            if (File.Exists(Config.MsraPath))
            {
                msg("  Already installed");
            }
            else
            {
                string stdout;
                if (!Util.EnableWindowsFeature("RemoteAssistance", out stdout))
                    msg("  " + stdout);
            }
        }

        public static void AllowRemoteAssistance()
        {
            Registry.SetValue(@"HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Remote Assistance", "fAllowToGetHelp", 1);
            Registry.SetValue(@"HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Remote Assistance", "fAllowFullControl", 1);
        }

        public static void ConfigFirewallRuleForRemoteAssistance()
        {
            Process p = new Process();
            p.StartInfo.FileName = "netsh.exe";
            p.StartInfo.Arguments = "advfirewall firewall set rule group=\"Remote Assistance\" new enable=yes";
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            p.Start();
            string output = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            int ret = p.ExitCode;

            if (ret != 0)
            {
                msg("  WARNING: return code=" + ret);
                msg("  Output: " + output);
            }
        }

        private static void EnableWindowsRAFeature()
        {
            if (Util.IsFeatureInstalled("42"))  //42: Remote Assistance
            {
                msg("  Feature already installed. Skip.");
                return;
            }

            string stdout;
            if (!Util.EnableWindowsFeature("RemoteAssistance", out stdout))
                msg("  " + stdout);
        }

        private static void InstallAdminExShortcutOnDesktop()
        {
            string fileName = Util.CreateUrlShortcutOnDesktop(Config.AdminExShortcutName, Config.AdminExUrl, Config.HRALauncherPath);
            msg("  " + fileName);
        }

        private static void RemoveAdminExShortcutFromDesktop()
        {
            string fileName = Config.AdminExShortcutName + ".url";
            string fileDeleted = Util.DeleteDesktopFile(fileName);
            if (fileDeleted != null)
                msg("  " + fileDeleted);
        }

        private static void CopyHRARequestorToAllUserDesktop()
        {
            string fileName = Config.HRARequestorPath;
            msg("  " + fileName);
            Util.SaveEmbeddedResToFile("HRAInstaller.files.Horizon Remote Assistance.exe", fileName);
        }

        private static void RemoveHRARequestorFromAllUserDesktop()
        {
            string fileName = Config.HRARequestorPath;
            if (File.Exists(fileName))
            {
                msg("  " + fileName);
                File.Delete(fileName);
            }
        }

        private static void CopyHRALauncher()
        {
            msg("  " + Config.HRALauncherDir);
            Directory.CreateDirectory(Config.HRALauncherDir);
            msg("  " + Config.HRALauncherPath);
            Util.SaveEmbeddedResToFile("HRAInstaller.files.Horizon Remote Assistance Launcher.exe", Config.HRALauncherPath);
        }

        private static void RemoveHRALauncher()
        {
            if (File.Exists(Config.HRALauncherPath))
            {
                msg("  " + Config.HRALauncherPath);
                File.Delete(Config.HRALauncherPath);
            }
            if (Directory.Exists(Config.HRALauncherDir))
            {
                msg("  " + Config.HRALauncherDir);
                Directory.Delete(Config.HRALauncherDir);
            }
        }

        private static void InstallAdminEx()
        {
            string path = ViewUtil.GetViewConnectionServerAppPath();
            if (path == null)
            {
                msg("  ! ERROR - Horizon View Connection installation directory not found");
                returnCode = RET_VIEW_INSTALLATION_NOT_FOUND;
            }
            else if (!Directory.Exists(path))
            {
                msg("  ! ERROR - Path does not exist: " + path);
                returnCode = RET_VIEW_DIR_NOT_EXIST;
            }
            else
            {
                path += "\\admin_ex.war";
                msg("  " + path);
                Util.SaveEmbeddedResToFile("HRAInstaller.files.admin_ex.war", path);
            }
        }

        private static void RemoveAdminEx()
        {
            string fileName = ViewUtil.GetViewConnectionServerAppPath() + "\\admin_ex.war";
            if (File.Exists(fileName))
            {
                msg("  " + fileName);
                File.Delete(fileName);
            }
        }

        private static void RemoveAdminExGenerated()
        {
            string fileName = ViewUtil.GetViewConnectionServerAppPath() + "\\admin_ex";
            if (Directory.Exists(fileName))
            {
                Thread.Sleep(3000);
                msg("  " + fileName);
                Directory.Delete(fileName, true);
            }
        }

        private static void CheckAdminEx()
        {
            msg("Waiting for AdminEx web model to be started in Horizon View Connection server, this may take a few minutes...");

            int retry = 3;
            int interval = 20 * 1000;

            msg("AdminEx URL: " + Config.AdminExUrl);

            //first always wait for a while. Tomcat is not that quick
            Thread.Sleep(5000);

            bool isAdminExOK = false;

            Util.IgnoreSSLCert();
            for (int i = 0; i < retry; i++)
            {
                msg("Checking AdminEx web model readiness (" + (i + 1) + "/" + retry + ")...");

                string info;
                int status = Util.TestWebReadiness(Config.AdminExUrl, out info);

                if (status == 200)
                {
                    isAdminExOK = true;
                    msg("  OK");
                    break;
                }
                else if (status == 404)
                {
                    //sleep and retry
                    Thread.Sleep(interval);
                    continue;
                }
                else
                {
                    //server error. out
                    msg(info);
                    break;
                }
            }

            if (!isAdminExOK)
            {
                msg("The AdminEx web model is not working properly. This program must be run on View Connection Server. Make sure View Connection Server is running.");
            }

        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////

        [System.Runtime.InteropServices.DllImport("Shell32.dll")]
        private static extern int SHChangeNotify(int eventId, int flags, IntPtr item1, IntPtr item2);

        private static void RegisterHRAFileType()
        {
            string appPath = Config.HRALauncherPath;

            if (Registry.GetValue("HKEY_CLASSES_ROOT\\HorizonRemoteAssistance", String.Empty, String.Empty) == null)
            {
                msg("  HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance");
                Registry.SetValue("HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance", "", "Remote Assistance for Horizon View");
                Registry.SetValue("HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance", "HorizonRemoteAssistance", "Remote Assistance for Horizon View");
                Registry.SetValue("HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance\\shell\\open\\command", "",
                    appPath + " \"%1\"");
                msg("  HKEY_LOCAL_MACHINE\\Software\\Classes\\.HorizonRemoteAssistance");
                Registry.SetValue("HKEY_LOCAL_MACHINE\\Software\\Classes\\.HorizonRemoteAssistance", "", "HorizonRemoteAssistance");

                //this call notifies Windows that it needs to redo the file associations and icons
                SHChangeNotify(0x08000000, 0x2000, IntPtr.Zero, IntPtr.Zero);
            }
        }

        private static void DeregisterHRAFileType()
        {
            if (Registry.GetValue("HKEY_CLASSES_ROOT\\HorizonRemoteAssistance", String.Empty, String.Empty) != null)
            {
                msg("  HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance");
                Registry.LocalMachine.DeleteSubKeyTree("Software\\Classes\\HorizonRemoteAssistance");
                Registry.LocalMachine.DeleteSubKey("Software\\Classes\\.HorizonRemoteAssistance");
            }
        }
    }
}
