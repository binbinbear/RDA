using System;
using System.Text;
using System.Xml;
using System.IO;
using System.Globalization;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Runtime.CompilerServices;
using System.Text.RegularExpressions;
using System.Security.Principal;
using System.Reflection;
using System.Threading;
using Microsoft.Win32.SafeHandles;
using System.ComponentModel;

namespace CreateRAString
{
    class Program
    {

        static void Main(string[] args)
        {
            try
            {
                if (args.Length == 0)
                {
                    Console.WriteLine("WTSGetActiveConsoleSessionId=" + UserAppLauncher.WTSGetActiveConsoleSessionId());
                    Console.WriteLine("Assembly.GetExecutingAssembly().Location=" + Assembly.GetExecutingAssembly().Location);
                    Console.WriteLine("Environment.UserName=" + Environment.UserName);
                    return;
                }

                string param = args[0];
                if ("ticket".Equals(param))
                {
                    RATicketGenerator.KillAllMsraProcesses();

                    string ticket = RATicketGenerator.RequestRATicket("127.0.0.1");
                    //File.WriteAllText("z:\\temp\\1.msrcincident", ticket);
                    /*
                    Console.WriteLine(ticket);
                    Console.WriteLine("ticket exit");
                    Thread.Sleep(3000);
                    Console.WriteLine("ticket readkey");
                    Console.Out.Flush();
                    Console.ReadKey();
                     */
                    string pipeName = args[1];
                    if (!SimplePipe.Write(pipeName, ticket))
                    {
                        //File.WriteAllText("z:\\temp\\error.txt", new Win32Exception().ToString());
                    }
                }
                else if ("notepad".Equals(param))
                {
                    UserAppLauncher.CreateProcessInConsoleSession("notepad.exe", false);
                }
                else if ("system".Equals(param))
                {
                    SimplePipe pipe = SimplePipe.CreateServer();
                    string commandLine = Assembly.GetExecutingAssembly().Location + " ticket " + pipe.Name;
                    UserAppLauncher.CreateProcessInConsoleSession(commandLine, false);
                    string ticket = pipe.Read(15000);
                    Console.WriteLine("Pipe Output: " + ticket);
					
					//add by wx 9-15
					string str = System.Environment.GetEnvironmentVariable("windir");
					str = str.Substring(0, str.IndexOf(":"));
                    File.WriteAllText(str + ":\\2.msrcincident", ticket);
					//File.WriteAllText("c:\\2.msrcincident", ticket);
                }
                else
                {
                    Console.WriteLine("unknown: " + param);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }

            Thread.Sleep(5000);
        }
    }
}

