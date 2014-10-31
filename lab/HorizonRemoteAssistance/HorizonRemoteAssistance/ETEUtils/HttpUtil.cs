using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Reflection;
using System.Net.Security;
using System.Net.Sockets;

namespace ETEUtils
{
    public class HttpUtil
    {
        public static bool Get(string url, string var, string content)
        {
            return Get(url, var, content, null);
        }

        public static bool Get(string url, string var, string content, string expectedResponse)
        {
            using (var wb = new WebClient())
            {
                wb.Headers["User-Agent"] = "ETEUtils";

                wb.QueryString.Add(var, content);

                Log.Info("Requesting: " + url);

                //we should post, however View Connection server has disabled POST.
                //string ret = wb.UploadValues(.UploadString(url, );
                string ret = wb.DownloadString(url);
                Log.Info("Response: " + ret);

                if (expectedResponse != null)
                    return ret.ToUpper().Contains(expectedResponse.ToUpper());

                return true;
            }
        }

        public static void _IgnoreSSL()
        {
            try
            {
                //Change SSL checks so that all checks pass
                ServicePointManager.ServerCertificateValidationCallback =
                    new RemoteCertificateValidationCallback(
                        delegate
                        { return true; }
                    );
            }
            catch (Exception ex)
            {
                Log.Error(ex);
            }
        }

        public static void UriHackFix()
        {
            MethodInfo getSyntax = typeof(UriParser).GetMethod("GetSyntax", System.Reflection.BindingFlags.Static | System.Reflection.BindingFlags.NonPublic);
            FieldInfo flagsField = typeof(UriParser).GetField("m_Flags", System.Reflection.BindingFlags.Instance | System.Reflection.BindingFlags.NonPublic);
            if (getSyntax != null && flagsField != null)
            {
                foreach (string scheme in new[] { "http", "https" })
                {
                    UriParser parser = (UriParser)getSyntax.Invoke(null, new object[] { scheme });
                    if (parser != null)
                    {
                        int flagsValue = (int)flagsField.GetValue(parser);
                        // Clear the CanonicalizeAsFilePath attribute
                        if ((flagsValue & 0x1000000) != 0)
                            flagsField.SetValue(parser, flagsValue & ~0x1000000);
                    }
                }
            }
        }

        public static List<string> resolveIPv4Addrs(string hostname)
        {
            List<string> addrs = new List<string>();

            try
            {
                IPAddress[] addresses = Dns.GetHostAddresses(hostname);
                foreach (IPAddress ipa in addresses)
                {
                    if (ipa.AddressFamily == AddressFamily.InterNetwork)
                        addrs.Add(ipa.ToString());
                }
            }
            catch (Exception)
            {
                //Logger.Log(e);
                //return false;
                addrs.Add(hostname);
            }
            //addrs.Add(hostname);
            return addrs;
        }
    }
}
