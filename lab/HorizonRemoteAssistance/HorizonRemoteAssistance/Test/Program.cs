using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Security.Principal;
using System.Collections;

namespace Test
{
    class Program
    {
        static void Main(string[] args)
        {
            List<string> content = new List<string>();

            content.Add("Date: " + DateTime.Now);
            content.Add("WindowsIdentity.GetCurrent().Name: " + WindowsIdentity.GetCurrent().Name);
            content.Add("Environment.CurrentDirectory: " + Environment.CurrentDirectory);
            content.Add("Environment.CommandLine: " + Environment.CommandLine);
            content.Add("Environment.ExitCode: " + Environment.ExitCode);
            content.Add("Environment.HasShutdownStarted: " + Environment.HasShutdownStarted);
            content.Add("Environment.MachineName: " + Environment.MachineName);
            content.Add("Environment.OSVersion: " + Environment.OSVersion);
            content.Add("Environment.ProcessorCount: " + Environment.ProcessorCount);
            content.Add("Environment.SystemDirectory: " + Environment.SystemDirectory);
            content.Add("Environment.TickCount: " + Environment.TickCount);
            content.Add("Environment.UserDomainName: " + Environment.UserDomainName);
            content.Add("Environment.UserInteractive: " + Environment.UserInteractive);
            content.Add("Environment.UserName: " + Environment.UserName);
            content.Add("Environment.Version: " + Environment.Version);
            content.Add("Environment.WorkingSet: " + Environment.WorkingSet);

            content.Add("Environment.GetEnvironmentVariables:");
            foreach (DictionaryEntry en in Environment.GetEnvironmentVariables())
            {
                content.Add("  " + en.Key + " = " + en.Value);
            }

            content.Add("Environment.GetCommandLineArgs:");
            foreach (var en in Environment.GetCommandLineArgs())
            {
                content.Add("  " + en.ToString());
            }

            content.Add("Args:");
            foreach (var en in args)
            {
                content.Add("  " + en);
            }
            content.Add("");
            content.Add("Done.");
            content.Add("");

            foreach (string s in content)
                Console.WriteLine(s);

            //File.WriteAllLines(, content.ToArray());
            using (StreamWriter sw = new StreamWriter(@"c:\RuntimeDump.txt", true, Encoding.UTF8))
            {
                foreach (string s in content)
                {
                    sw.WriteLine(s);
                }
            }
            
        }
    }
}
