using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Security.Principal;
using System.Collections;
using System.Reflection;

namespace Test
{
    class Program
    {
        static void Main(string[] args)
        {
            List<string> content = new List<string>();

            try
            {
                content.Add(Assembly.GetExecutingAssembly().GetName().ToString());
                content.Add("Date: " + DateTime.Now);
                content.Add("WindowsIdentity.GetCurrent().Name: " + WindowsIdentity.GetCurrent().Name);

                content.Add("");
                content.Add("Environment");
                content.Add("    CurrentDirectory: " + Environment.CurrentDirectory);
                content.Add("    CommandLine: " + Environment.CommandLine);
                content.Add("    ExitCode: " + Environment.ExitCode);
                content.Add("    HasShutdownStarted: " + Environment.HasShutdownStarted);
                content.Add("    MachineName: " + Environment.MachineName);
                content.Add("    OSVersion: " + Environment.OSVersion);
                content.Add("    ProcessorCount: " + Environment.ProcessorCount);
                content.Add("    SystemDirectory: " + Environment.SystemDirectory);
                content.Add("    TickCount: " + Environment.TickCount);
                content.Add("    UserDomainName: " + Environment.UserDomainName);
                content.Add("    UserInteractive: " + Environment.UserInteractive);
                content.Add("    UserName: " + Environment.UserName);
                content.Add("    Version: " + Environment.Version);
                content.Add("    WorkingSet: " + Environment.WorkingSet);

                content.Add("");
                content.Add("Folder Path: ");
                content.Add("    ApplicationData: " + Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData));
                content.Add("    CommonApplicationData: " + Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData));
                content.Add("    CommonProgramFiles: " + Environment.GetFolderPath(Environment.SpecialFolder.CommonProgramFiles));
                content.Add("    Cookies: " + Environment.GetFolderPath(Environment.SpecialFolder.Cookies));
                content.Add("    Desktop: " + Environment.GetFolderPath(Environment.SpecialFolder.Desktop));
                content.Add("    DesktopDirectory: " + Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory));
                content.Add("    Favorites: " + Environment.GetFolderPath(Environment.SpecialFolder.Favorites));
                content.Add("    History: " + Environment.GetFolderPath(Environment.SpecialFolder.History));
                content.Add("    InternetCache: " + Environment.GetFolderPath(Environment.SpecialFolder.InternetCache));
                content.Add("    LocalApplicationData: " + Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData));
                content.Add("    MyComputer: " + Environment.GetFolderPath(Environment.SpecialFolder.MyComputer));
                content.Add("    MyDocuments: " + Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments));
                content.Add("    MyMusic: " + Environment.GetFolderPath(Environment.SpecialFolder.MyMusic));
                content.Add("    MyPictures: " + Environment.GetFolderPath(Environment.SpecialFolder.MyPictures));
                content.Add("    Personal: " + Environment.GetFolderPath(Environment.SpecialFolder.Personal));
                content.Add("    ProgramFiles: " + Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles));
                content.Add("    Programs: " + Environment.GetFolderPath(Environment.SpecialFolder.Programs));
                content.Add("    Recent: " + Environment.GetFolderPath(Environment.SpecialFolder.Recent));
                content.Add("    SendTo: " + Environment.GetFolderPath(Environment.SpecialFolder.SendTo));
                content.Add("    StartMenu: " + Environment.GetFolderPath(Environment.SpecialFolder.StartMenu));
                content.Add("    Startup: " + Environment.GetFolderPath(Environment.SpecialFolder.Startup));
                content.Add("    System: " + Environment.GetFolderPath(Environment.SpecialFolder.System));
                content.Add("    Templates: " + Environment.GetFolderPath(Environment.SpecialFolder.Templates));

                content.Add("");
                content.Add("Environment.GetEnvironmentVariables:");
                foreach (DictionaryEntry en in Environment.GetEnvironmentVariables())
                {
                    content.Add("    " + en.Key + " = " + en.Value);
                }

                content.Add("");
                content.Add("Environment.GetCommandLineArgs:");
                foreach (var en in Environment.GetCommandLineArgs())
                {
                    content.Add("    " + en.ToString());
                }

                content.Add("");
                content.Add("Args:");
                foreach (var en in args)
                {
                    content.Add("    " + en);
                }
            }
            catch (Exception e)
            {
                content.Add(e.ToString());
            }

            content.Add("");
            content.Add("Done.");
            content.Add("");

            foreach (string s in content)
                Console.WriteLine(s);

            //File.WriteAllLines(, content.ToArray());
            using (StreamWriter sw = new StreamWriter(@"RuntimeDump.txt", false, Encoding.UTF8))
            {
                foreach (string s in content)
                {
                    sw.WriteLine(s);
                }
            }
            
        }
    }
}
