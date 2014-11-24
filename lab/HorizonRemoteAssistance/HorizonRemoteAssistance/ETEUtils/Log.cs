using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace ETEUtils
{
    public class Log
    {
        private static string logFilePath;

        static Log()
        {
        }

        public static void InitInLocalAppData(string relativePath, string logFileName)
        {
            InitInLocalAppData(relativePath, logFileName, null, false);
        }

        public static void InitInLocalAppData(string productName, string logFileName, string ext, bool addTimeStamp)
        {
            string path = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
            if (!path.EndsWith("\\"))
                path += "\\";
            path += productName;
            Init(path, logFileName, ext, addTimeStamp);
        }

        public static void Init(string path, string logFileName, string ext, bool addTimeStamp)
        {
            if (logFilePath != null)
                throw new Exception("Logger already initialized");

            if (!Directory.Exists(path) && !File.Exists(path))
                Directory.CreateDirectory(path);

            string fileName = logFileName;
            if (addTimeStamp)
            {
                DateTime dt = DateTime.Now;
                string timestamp = String.Format("_{0:MM}{1:dd}{2:yy}_{3:HH}{4:mm}{5:ss}_{6:ffff}", dt, dt, dt, dt, dt, dt, dt);
                fileName += timestamp;
            }

            if (ext != null)
                fileName += "." + ext;

            if (!path.EndsWith("\\"))
                path += "\\";

            path += fileName;

            logFilePath = path;

            if (File.Exists(path))
                File.Delete(path);
        }

        public static void Init()
        {
            Init(".\\", "log.txt", null, false);
        }

        private static void _log(object o)
        {
            string msg = DateTime.Now + " - " + o + "\r\n";

            try
            {
                Console.WriteLine(msg);
                File.AppendAllText(logFilePath, msg);
            }
            catch (Exception)
            {
            }
        }

        public static void Error(object o)
        {
            _log(o);
        }

        public static void Info(object o)
        {
            _log(o);   
        }
    }
}
