using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;
using System.Globalization;
using System.IO;
using System.Diagnostics;
using System.Xml;

namespace CreateRAString
{
    /**
     * This class generates ticket file for remote assistance (MSRA).
     * 
     */
 
    class RATicketGenerator
    {

        private static readonly Guid raSrvClsId = new Guid("3C3A70A7-A468-49B9-8ADA-28E11FCCAD5D");

        [ComImport, Guid("F120A684-B926-447F-9DF4-C966CB785648"), CoClass(typeof(CreateRAString.RATicketGenerator.RASrv))]
        private interface IRASrv
        {
            [DispId(1)]
            void GetNoviceUserInfo([MarshalAs(UnmanagedType.LPWStr)] ref string szName);
            [DispId(2)]
            void GetSessionInfo(ref string[] userNames, ref int count);
        }

        [ComImport, Guid("3C3A70A7-A468-49B9-8ADA-28E11FCCAD5D")]
        private class RASrv
        {
        }

        private static string GenerateIncidentFileContents(string username, string ticket)
        {
            return string.Format(CultureInfo.InvariantCulture, "<?xml version=\"1.0\" ?><UPLOADINFO TYPE=\"Escalated\"><UPLOADDATA USERNAME=\"{0}\" RCTICKET=\"{1}\" RCTICKETENCRYPTED=\"0\" PassStub=\"\" L=\"0\" /></UPLOADINFO>", new object[] { username, ticket });
        }

        private static bool IsIpV4Addr(string ipAddr)
        {
            string pattern = @"^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
            Regex regex = new Regex(pattern, RegexOptions.Singleline | RegexOptions.ExplicitCapture);
            return regex.IsMatch(ipAddr);
        }

        private static string parseRAXMLResponse(string buf)
        {
            string str = "";
            string str2 = null;
            string str3 = null;
            using (StringReader input = new StringReader(buf))
            {
                using (XmlReader reader2 = XmlReader.Create(input))
                {
                    while (reader2.Read())
                    {
                        switch (reader2.Name.ToUpper(CultureInfo.InvariantCulture))
                        {
                            case "E":
                            case "C":
                            case "T":
                            case "":
                                {
                                    continue;
                                }
                            case "A":
                                {
                                    reader2.MoveToAttribute("KH");
                                    str3 = reader2.Value;
                                    reader2.MoveToAttribute("ID");
                                    str2 = reader2.Value;
                                    continue;
                                }
                            case "L":
                                {
                                    reader2.MoveToAttribute("N");
                                    if (IsIpV4Addr(reader2.Value))
                                    {
                                        if (str.Length > 0)
                                        {
                                            str = str + ";";
                                        }
                                        str += reader2.Value;
                                        reader2.MoveToAttribute("P");
                                        str += ":" + reader2.Value;
                                    }
                                    continue;
                                }
                        }
                    }
                }
            }

            if (((str == null) || (str2 == null)) || (str3 == null))
            {
            }
            return string.Format(CultureInfo.InvariantCulture, "{0},{1},{2},*,{3},*,*,{4}", new object[] { "65538", "1", str, str2, str3 });
        }

        public static string RequestRATicket(string host)
        {
            System.Type t = Type.GetTypeFromCLSID(raSrvClsId, host, true);

            IRASrv COMobject = (IRASrv)System.Activator.CreateInstance(t);

            int count = 50;
            string[] names = new String[50];
            COMobject.GetSessionInfo(ref names, ref count);
            string userName = names[0];
            string szName = "";

            COMobject.GetNoviceUserInfo(ref szName);
            szName = parseRAXMLResponse(szName);

            return GenerateIncidentFileContents(userName, szName);
        }

        public static void KillAllMsraProcesses()
        {
            Process[] processes = Process.GetProcessesByName("msra");
            foreach (Process p in processes)
            {
                try
                {
                    if (p.MainModule.FileVersionInfo.CompanyName.Equals("Microsoft Corporation"))
                    {
                        //Log.Info("Kill existing MSRA processes: " + p.Id);
                        p.Kill();
                    }
                }
                catch (Exception)
                {
                    //Log.Error(e);
                }
            }
        }
    }
}
