using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace HRALauncher
{
    class Logger
    {

        private static string path;

        static Logger()
        {
            path = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
            if (!path.EndsWith("\\"))
                path += "\\";
            path += "VMware\\Horizon Remote Assistance\\";

            if (!Directory.Exists(path))
                Directory.CreateDirectory(path);

            path += "launcher.log";

            if (File.Exists(path))
                File.Delete(path);
        }

        public static void Log(string msg)
        {
            msg = DateTime.Now + " - " + msg + "\r\n";

            try
            {
                File.AppendAllText(path, msg);
            }
            catch (Exception)
            {
            }
        }

        public static void Log(Exception e)
        {
            Log(e.ToString());
        }
    }
}
