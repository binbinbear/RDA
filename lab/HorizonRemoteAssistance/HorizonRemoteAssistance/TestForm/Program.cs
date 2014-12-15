using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Threading;

namespace TestForm
{
    static class Program
    {
        public static volatile bool quit;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            

            Application.ApplicationExit += (sender, e) =>
            {
                //quit = true;
            };

            while (!quit)
            {
                Application.DoEvents();
                Thread.Sleep(1000);
            }

            Application.Run(new Form1());
        }
    }
}
