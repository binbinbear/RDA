using System;
using System.Collections.Generic;
using System.Text;

namespace CreateRAString
{
    class Log
    {
        internal static void Info(string p)
        {
            Console.WriteLine(p);
        }

        internal static void Error(Exception e)
        {
            Console.WriteLine(e);
        }

        internal static void LogException(string m, Exception e)
        {
            Console.WriteLine(m + e);
        }

        internal static void LogFatalException(Exception e)
        {
            LogException("", e);
        }
    }
}
