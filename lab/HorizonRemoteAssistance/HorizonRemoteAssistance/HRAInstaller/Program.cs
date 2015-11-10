using System;
using System.Collections.Generic;
using System.Windows.Forms;
using Microsoft.Win32;

namespace HRAInstaller
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {

            string[] args = Environment.GetCommandLineArgs();

            if (args.Length == 1)
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);

                Application.Run(new FormMain());
            }
            else if (args.Length == 2)
            {
                string arg = args[1];
                if (arg.Equals("-installForUser", StringComparison.OrdinalIgnoreCase))
                {
                    Install(Config.INSTALL_FOR_END_USER);
                }
                else if (arg.Equals("-installForAdmin", StringComparison.OrdinalIgnoreCase))
                {
                    Install(Config.INSTALL_FOR_ADMIN);
                }
                else if (arg.Equals("-installLauncher", StringComparison.OrdinalIgnoreCase))
                {
                    Install(Config.INSTALL_LAUNCHER);
                }
                else if (arg.Equals("-uninstall", StringComparison.OrdinalIgnoreCase))
                {
                    Install(Config.UNINSTALL);
                }
                else
                {
                    OnInvalidArgument(arg);
                }
            }
            else
            {
                MessageBox.Show("Invalid argument count: " + args.Length);
            }

            Environment.ExitCode = Installer.returnCode;
            Application.Exit();
        }

        private static void OnInvalidArgument(string arg)
        {
            Installer.returnCode = Installer.RET_INVALID_ARG;
            MessageBox.Show("Invalid argument: " + arg);
        }

        private static void Install(int type)
        {
            Config.installType = type;
            Installer.Init(null, msg);

            while (Installer.RunNextStep())
                ;
        }

        private static void msg(string s)
        {
            Console.WriteLine(s);
        }
    }
}
