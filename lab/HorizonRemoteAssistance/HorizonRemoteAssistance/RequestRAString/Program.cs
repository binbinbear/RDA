using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.ComponentModel;
using System.IO;
using ETEUtils;

namespace CreateRAString
{
    class Program
    {

        static void Main(string[] args)
        {
            if (!InitLog())
                return;

            LogEnvironment();

            try
            {
                if (args.Length == 2 && "ticket".Equals(args[0], StringComparison.OrdinalIgnoreCase))
                {
                    CreateRATicket(args[1]);
                }
                else
                {
                    Log.Info("Unknown app caller. App caller must be HA agent service.");
                }
            }
            catch (Exception e)
            {
                Log.Error(e);
            }

            Log.Info("Exit.");

            //try
            //{
            //    if (args.Length == 0)
            //    {
            //        Console.WriteLine("WTSGetActiveConsoleSessionId=" + UserAppLauncher.WTSGetActiveConsoleSessionId());
            //        Console.WriteLine("Assembly.GetExecutingAssembly().Location=" + Assembly.GetExecutingAssembly().Location);
            //        Console.WriteLine("Environment.UserName=" + Environment.UserName);
            //        return;
            //    }

            //    string param = args[0];
            //    if ("ticket".Equals(param))
            //    {
            //        RATicketGenerator.KillAllMsraProcesses();

            //        string ticket = RATicketGenerator.RequestRATicket("127.0.0.1");
            //        //File.WriteAllText("z:\\temp\\1.msrcincident", ticket);
            //        /*
            //        Console.WriteLine(ticket);
            //        Console.WriteLine("ticket exit");
            //        Thread.Sleep(3000);
            //        Console.WriteLine("ticket readkey");
            //        Console.Out.Flush();
            //        Console.ReadKey();
            //         */
            //        string pipeName = args[1];
            //        if (!SimplePipe.Write(pipeName, ticket))
            //        {
            //            //File.WriteAllText("z:\\temp\\error.txt", new Win32Exception().ToString());
            //        }
            //    }
            //    else if ("notepad".Equals(param))
            //    {
            //        UserAppLauncher.CreateProcessInConsoleSession("notepad.exe", false);
            //    }
            //    else if ("system".Equals(param))
            //    {
            //        SimplePipe pipe = SimplePipe.CreateServer();
            //        string commandLine = Assembly.GetExecutingAssembly().Location + " ticket " + pipe.Name;
            //        UserAppLauncher.CreateProcessInConsoleSession(commandLine, false);
            //        string ticket = pipe.Read(15000);
            //        Console.WriteLine("Pipe Output: " + ticket);
            //        File.WriteAllText("c:\\2.msrcincident", ticket);
            //    }
            //    else
            //    {
            //        Console.WriteLine("unknown: " + param);
            //    }
            //}
            //catch (Exception e)
            //{
            //    Console.WriteLine(e);
            //}

            //Thread.Sleep(5000);
        }

        private static void CreateRATicket(string pipeName)
        {
            Log.Info("Clean up RA...");
            RATicketGenerator.KillAllMsraProcesses();

            Log.Info("Requesting RA ticket...");
            string ticket = RATicketGenerator.RequestRATicket("127.0.0.1");

            Log.Info("Writing back to service. Ticket length=" + ticket.Length);

            if ("stdout".Equals(pipeName, StringComparison.OrdinalIgnoreCase))
            {
                Console.WriteLine(ticket);
            }
            else
            {
				//use temp file, instead of pipe. On *SOME* Windows 2012, without admin previlege, 
				//there's issue accessing pipe (created in user session) by system process

                //if (!SimplePipe.Write(pipeName, ticket))
                //    Log.Info("Write failed. Last error=" + new Win32Exception().Message);

                string fileName = GetProgramDataPath() + '/' + pipeName;
                File.WriteAllText(fileName, ticket);
            }
        }

        private static string GetProgramDataPath()
        {
            string path = Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData);
            path += "/VMware/Horizon DaaS Fling/";
            return path;
        }

        private static bool InitLog()
        {
            string path = GetProgramDataPath();

            try
            {
                Log.Init(path, "crtras", "log", false);
                return true;
            }
            catch (Exception)
            {
            }
            return false;
        }

        private static void LogEnvironment()
        {
            Log.Info(Assembly.GetExecutingAssembly().GetName().ToString());
            Log.Info("Time: " + DateTime.Now);
            Log.Info("Current Directory: " + Environment.CurrentDirectory);
            Log.Info("Machine Name: " + Environment.MachineName);
            Log.Info("OS Version: " + Environment.OSVersion);
            Log.Info("User Name: " + Environment.UserName);
            Log.Info("Version: " + Environment.Version);
        }
    }
}

