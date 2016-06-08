using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace ToolboxInstall
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);


            // Whether need configure firewall
            bool bConfigFirewall4RA = true;
            if (args.Length > 0)
            {
                if (args[0].CompareTo("0") == 0)  //
                {
                    bConfigFirewall4RA = false;
                }
            }


            Application.Run(new Form1(bConfigFirewall4RA));
        }
    }
}
