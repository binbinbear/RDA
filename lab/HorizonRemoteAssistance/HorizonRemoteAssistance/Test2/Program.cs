using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Management;
using System.Net;
using System.Diagnostics;
using System.Threading;
using Microsoft.Win32;
using System.Reflection;
using ETEUtils;

namespace Test2
{
    class Program
    {
        static void Main(string[] args)
        {
            string[] url = 
            {
                "https://192.168.0.202/toolbox/remoteassist/upload",
                "https://192.168.0.11/toolbox/remoteassist/upload",
                "https://192.168.0.11/toolbox/remoteassist/upload",
                "https://192.168.0.201/toolbox/remoteassist/upload",
                "https://192.168.0.201/toolbox/remoteassist/upload"
            };

            HttpUtil._IgnoreSSL();

            int success = HttpUtil.BatchGet(url, "aa", "bb", "cc");
            Console.WriteLine("Done: " + success);
            Console.ReadKey();
            
        }

        private static void HttpGet(string url, DownloadStringCompletedEventHandler onComplete)
        {
            using (WebClient wb = new WebClient())
            {
                wb.Proxy = null;

                try
                {
                    wb.DownloadStringAsync(new Uri(url));
                    wb.DownloadStringCompleted += onComplete;
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex);
                }
            }
        }
    }
}
