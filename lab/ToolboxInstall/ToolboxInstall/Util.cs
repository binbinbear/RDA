using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;
using System.Security.Permissions;
using System.Runtime.InteropServices;
using System.Runtime.InteropServices.ComTypes;
using System.Reflection;
using System.Management;
using System.Diagnostics;

namespace ToolboxInstall
{
    class Util
    {
        public static void IgnoreSSLCert()
        {
            ServicePointManager
                .ServerCertificateValidationCallback +=
                (sender, cert, chain, sslPolicyErrors) => true;
        }

        public static int TestWebReadiness(string url, out string msg)
        {
            try
            {
                HttpWebRequest myReq = (HttpWebRequest)WebRequest.Create(url);
                msg = null;
                return (int)((HttpWebResponse)myReq.GetResponse()).StatusCode;
            }
            catch (WebException we)
            {
                msg = we.Message;
                if (we.Response != null && we.Response is HttpWebResponse)
                    return (int)((HttpWebResponse)we.Response).StatusCode;
            }
            catch (Exception e)
            {
                msg = e.Message;
            }
            return -1;
        }

        public static string CreateUrlShortcutOnCurrentUserDesktop(string linkName, string linkUrl, string iconFile)
        {
            string desktopDir = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory);
            string fileName = desktopDir + "\\" + linkName + ".url";
            using (StreamWriter writer = new StreamWriter(fileName))
            {
                writer.WriteLine("[InternetShortcut]");
                writer.WriteLine("URL=" + linkUrl);
                if (iconFile != null)
                {
                    writer.WriteLine("IconFile=" + iconFile);
                    writer.WriteLine("IconIndex=0");
                }
            }
            return fileName;
        }

        public static string CreateExeShortcutOnCurrentUserDesktop(string linkName, string desc, string targetFilePath)
        {
            string desktopDir = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory);
            return CreateExeShortcut(desktopDir, linkName, desc, targetFilePath);
        }

        public static string CreateExeShortcutOnAllUserDesktop(string linkName, string desc, string targetFilePath)
        {
            string desktopDir = Util.GetAllUserDesktopFolderPriorNET40();
            return CreateExeShortcut(desktopDir, linkName, desc, targetFilePath);
        }

        public static string CreateExeShortcut(string path, string linkName, string desc, string targetFilePath)
        {
            IShellLink link = (IShellLink)new ShellLink();

            // setup shortcut information
            link.SetDescription(desc);
            link.SetPath(targetFilePath);

            // save it
            IPersistFile file = (IPersistFile)link;
            string fileName = Path.Combine(path, linkName + ".lnk");
            file.Save(fileName, false);
            return fileName;
        }

        public static string DeleteDesktopFile(string name)
        {
            string desktopDir = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory);
            string fileName = desktopDir + "\\" + name;

            if (File.Exists(fileName))
            {
                File.Delete(fileName);
                return fileName;
            }
            return null;
        }

        internal static void SaveEmbeddedResToFile(string resName, string fileName)
        {
            var assembly = Assembly.GetExecutingAssembly();

            byte[] buf;
            using (Stream stream = assembly.GetManifestResourceStream(resName))
            {
                int len = (int) stream.Length;
                buf = new byte[len];
                stream.Read(buf, 0, len);
            }

            File.WriteAllBytes(fileName, buf);
        }

        [DllImport("shell32.dll")]
        static extern int SHGetFolderPath(IntPtr hwndOwner, int nFolder, IntPtr hToken, uint dwFlags, [Out] StringBuilder pszPath);

        public static string GetAllUserDesktopFolderPriorNET40()
        {
            StringBuilder lpszPath = new StringBuilder(260);

            const int CSIDL_COMMON_DESKTOPDIRECTORY = 0x0019;    // All Users\Desktop
            SHGetFolderPath(IntPtr.Zero, CSIDL_COMMON_DESKTOPDIRECTORY, IntPtr.Zero, 0, lpszPath);
            
            string path = lpszPath.ToString();
            new FileIOPermission(FileIOPermissionAccess.PathDiscovery, path).Demand();
            return path;
        }

        public static bool IsFeatureInstalled(string id)
        {
            try
            {
                ManagementClass objMC = new ManagementClass("Win32_ServerFeature");
                ManagementObjectCollection objMOC = objMC.GetInstances();
                foreach (ManagementObject objMO in objMOC)
                {
                    string featureId = (string)objMO.Properties["Id"].Value.ToString();
                    if (featureId.Equals(id))
                        return true;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
            return false;
        }

        public static bool EnableWindowsFeature(string name, out string output)
        {
            Process p = new Process();
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.FileName = Environment.SystemDirectory + "\\dism.exe";
            p.StartInfo.Arguments = "/online /enable-feature /featureName:RemoteAssistance";
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            p.Start();
            // Do not wait for the child process to exit before
            // reading to the end of its redirected stream.
            // p.WaitForExit();
            // Read the output stream first and then wait.
            output = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            return p.ExitCode == 0;


            //p.StartInfo.FileName = "cmd.exe";
            //p.StartInfo.UseShellExecute = false;
            //p.StartInfo.RedirectStandardInput = true;
            //p.StartInfo.RedirectStandardOutput = true;
            //p.StartInfo.RedirectStandardError = true;
            //p.StartInfo.CreateNoWindow = true;
            //p.Start();
            //p.StandardInput.WriteLine(@"dism.exe /online /enable-feature /featureName:RemoteAssistance");
            //p.StandardInput.WriteLine(@"exit");
            //output = "";
            //p.WaitForExit();

            return true;
        }

        public static int KillProcesses(string name)
        {
            Process[] procs = Process.GetProcessesByName(name);
            int n = 0;
            foreach (Process p in procs)
            {
                p.Kill();
                n++;
            }
            return n;
        }

        [ComImport]
        [Guid("00021401-0000-0000-C000-000000000046")]
        internal class ShellLink
        {
        }

        [ComImport]
        [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
        [Guid("000214F9-0000-0000-C000-000000000046")]
        internal interface IShellLink
        {
            void GetPath([Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder pszFile, int cchMaxPath, out IntPtr pfd, int fFlags);
            void GetIDList(out IntPtr ppidl);
            void SetIDList(IntPtr pidl);
            void GetDescription([Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder pszName, int cchMaxName);
            void SetDescription([MarshalAs(UnmanagedType.LPWStr)] string pszName);
            void GetWorkingDirectory([Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder pszDir, int cchMaxPath);
            void SetWorkingDirectory([MarshalAs(UnmanagedType.LPWStr)] string pszDir);
            void GetArguments([Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder pszArgs, int cchMaxPath);
            void SetArguments([MarshalAs(UnmanagedType.LPWStr)] string pszArgs);
            void GetHotkey(out short pwHotkey);
            void SetHotkey(short wHotkey);
            void GetShowCmd(out int piShowCmd);
            void SetShowCmd(int iShowCmd);
            void GetIconLocation([Out, MarshalAs(UnmanagedType.LPWStr)] StringBuilder pszIconPath, int cchIconPath, out int piIcon);
            void SetIconLocation([MarshalAs(UnmanagedType.LPWStr)] string pszIconPath, int iIcon);
            void SetRelativePath([MarshalAs(UnmanagedType.LPWStr)] string pszPathRel, int dwReserved);
            void Resolve(IntPtr hwnd, int fFlags);
            void SetPath([MarshalAs(UnmanagedType.LPWStr)] string pszFile);
        }
    }
}
