﻿using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;
using System.Diagnostics;
using System.Threading;
using System.IO;
using System.Windows.Forms;
using System.Reflection;
using System.Security.AccessControl;
using System.Security.Principal;

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

        private static Form parent;

        public static void Init(Form parent, MsgHandler msgHandler)
        {
            Installer.parent = parent;
            Installer.msgHandler = msgHandler;

            string version = "Horizon Remote Assistance v" + Assembly.GetExecutingAssembly().GetName().Version;

            installForEndUser = new Step[] {
                new Step {message=version},
                new Step {message="Installing for virtual desktop..."},
                new Step {f=TerminateProcess_HRARequestor, message="Check existing HRA Requestor..."},
                new Step {f=CopyHRARequestorToAllUserDesktop, message="Install Horizon Remote Assistance to desktop (all user)..."},
                new Step {f=EnableWindowsRAFeatureForClient, message="Enable Windows Feature: Remote Assistance..."},
                new Step {f=UpdateClientRegistryPermission, message="Update registry..."},
                new Step {f=AllowRemoteAssistance, message="Allow receiving remote assistance..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."},
                new Step {f=EnsureMSRAInitialization, message="Start MSRA to ensure initial config..."}
            };

            installForAdmin = new Step[] {
                new Step {message=version},
                new Step {message="Installing for helpdesk..."},
                //new Step {f=InstallAdminEx, message="Install AdminEx web model into View Connection Server..."},
                new Step {f=TerminateProcess_HRALauncher, message="Check existing HRA Launcher..."},
                new Step {f=EnableWindowsRAFeature, message="Enable Windows Feature: Remote Assistance..."},
                new Step {f=CopyHRALauncher, message="Copy HRA launcher..."},
                new Step {f=RegisterHRAFileType, message="Associate HRA file type..."},
                //new Step {f=InstallAdminExShortcutOnDesktop, message="Install AdminEx shortcut on desktop..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."},
                //new Step {f=CheckAdminEx, message="Checking AdminEx web model availability..."},
            };

            installLauncher = new Step[] {
                new Step {message=version},
                new Step {message="Install component for Helpdesk..."},
                new Step {f=EnableWindowsRAFeature, message="Enable Windows Feature: Remote Assistance..."},
                new Step {f=CopyHRALauncher, message="Copy HRA launcher..."},
                new Step {f=RegisterHRAFileType, message="Associate HRA file type..."},
                new Step {f=ConfigFirewallRuleForRemoteAssistance, message="Configure Firewall..."},
            };

            uninstall = new Step[] {
                new Step {message=version},
                new Step {message="Uninstalling..."},
                new Step {f=TerminateProcess_HRARequestor, message="Check existing HRA Requestor..."},
                new Step {f=TerminateProcess_HRALauncher, message="Check existing HRA Launcher..."},
                new Step {f=RemoveAdminEx, message="Remove AdminEx web model from View Connection Server..."},
                new Step {f=DeregisterHRAFileType, message="Remove HRA file type association..."},
                new Step {f=RemoveHRALauncher, message="Remove HRA launcher..."},
                new Step {f=RemoveAdminExShortcutFromDesktop, message="Remove AdminEx shortcut..."},
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

        public static void TerminateProcess_HRARequestor()
        {
            int killed = Util.KillProcesses("Horizon Remote Assistance");
            if (killed > 0)
                msg("  Found: " + killed);
        }

        public static void TerminateProcess_HRALauncher()
        {
            int killed = Util.KillProcesses("Horizon Remote Assistance Launcher");
            if (killed > 0)
                msg("  Found: " + killed);
        }

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
            string fileName = Util.CreateUrlShortcutOnCurrentUserDesktop(Config.AdminExShortcutName, Config.AdminExUrl, Config.HRALauncherPath);
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
            //create dir
            msg("  " + Config.HRARequestorDir);
            Directory.CreateDirectory(Config.HRARequestorDir);

            //copy file
            string fileName = Config.HRARequestorPath;
            msg("  " + fileName);
            Util.SaveEmbeddedResToFile("HRAInstaller.files.Horizon Remote Assistance.exe", fileName);

            //link to default desktop
            fileName = Util.CreateExeShortcutOnAllUserDesktop(Config.HRARequestorName, "Horizon Remote Assistance Requestor", Config.HRARequestorPath);
            msg("  " + fileName);
        }

        private static void RemoveHRARequestorFromAllUserDesktop()
        {
            DeleteFileOrDir(Config.HRARequestorPath);
            DeleteFileOrDir(Config.HRARequestorDir);
            DeleteFileOrDir(Config.HRARequestorDesktopShortcut);
        }

        private static void DeleteFileOrDir(string path)
        {
            if (File.Exists(path))
            {
                msg("  " + path);
                File.Delete(path);
            }
            else if (Directory.Exists(path))
            {
                msg("  " + path);
                Directory.Delete(path, true);
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
            DeleteFileOrDir(Config.HRALauncherPath);
            DeleteFileOrDir(Config.HRALauncherDir);
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
            DeleteFileOrDir(fileName);
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

        private static void EnsureMSRAInitialization()
        {
            if (Installer.parent != null)
            {
                Installer.parent.Invoke(new MethodInvoker(()=>
                {
                    MessageBox.Show(Installer.parent, "To make sure MSRA is initialized for first time use, the installer will launch MSRA now. \r\nPlease ignore it and just CLOSE the MSRA window after it's launched.", "Horizon Remote Assistance Installer", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }));
            }
            else
            {
                //console silent mode
            }

            msg("Starting MSRA to ensure it's initialized for first time use...");
            Process p = Process.Start("msra.exe");
            p.WaitForExit(60 * 1000);
            msg("Killing MSRA...");
            try
            {
                p.Kill();
            }
            catch { }
        }

        private static void UpdateClientRegistryPermission()
        {
            //Grant everyone "READ" permission to View agent config

            string key = @"HKEY_LOCAL_MACHINE\Software\VMware, Inc.\VMwareVDM\Agent\Configuration";

            using (RegistryKey rk = Registry.LocalMachine.OpenSubKey(key, true))
            {
                if (rk == null)
                {
                    msg("  No Horizon View Agent configuration found.");
                    return;
                }

                RegistrySecurity rs = rk.GetAccessControl();

                // Creating registry access rule for 'Everyone' NT account
                SecurityIdentifier sid = new SecurityIdentifier(WellKnownSidType.WorldSid, null);
                NTAccount account = sid.Translate(typeof(NTAccount)) as NTAccount;

                RegistryAccessRule rar = new RegistryAccessRule(
                    account.ToString(),
                    RegistryRights.ReadPermissions,
                    InheritanceFlags.ContainerInherit | InheritanceFlags.ObjectInherit,
                    PropagationFlags.None,
                    AccessControlType.Allow);

                rs.AddAccessRule(rar);
                rk.SetAccessControl(rs);
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        [System.Runtime.InteropServices.DllImport("Shell32.dll")]
        private static extern int SHChangeNotify(int eventId, int flags, IntPtr item1, IntPtr item2);

        private static void RegisterHRAFileType()
        {
            string appPath = Config.HRALauncherPath;

            if (Registry.GetValue("HKEY_CLASSES_ROOT\\HorizonRemoteAssistance", String.Empty, null) == null)
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
            if (Registry.GetValue("HKEY_CLASSES_ROOT\\HorizonRemoteAssistance", String.Empty, null) != null)
            {
                msg("  HKEY_LOCAL_MACHINE\\Software\\Classes\\HorizonRemoteAssistance");
                Registry.LocalMachine.DeleteSubKeyTree("Software\\Classes\\HorizonRemoteAssistance");
                Registry.LocalMachine.DeleteSubKey("Software\\Classes\\.HorizonRemoteAssistance");
            }
        }
    }
}
