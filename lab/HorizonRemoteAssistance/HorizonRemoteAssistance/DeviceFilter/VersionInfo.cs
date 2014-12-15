using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;

namespace DeviceFilter
{
    public static class VersionInfo
    {
        public static readonly string VERSION = "12-15-2014";

        public static void Print()
        {
            Console.WriteLine(VERSION + ", " + Assembly.GetExecutingAssembly().GetName().ToString());
        }
    }
}
