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

        public static void SetDefaultConnectionLimit(int n)
        {
            ServicePointManager.DefaultConnectionLimit = n;
        }

        public static void SetNoProxy()
        {
            WebRequest.DefaultWebProxy = null;
        }

        public static bool Get(string url, string var, string content)
        {
            return Get(url, var, content, null);
        }

        public static bool Get(string url, string var, string content, string expectedResponse)
        {
            using (var wb = new WebClient())
            {
                wb.Proxy = null;

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

        //
        // Batchly get multiple URLs simultaneously, blocks until all requests are done. 
        // Return number of response contains expectedResponse
        public static int BatchGet(string[] url, string var, string content, string expectedResponse)
        {
            CountdownLatch cdl = new CountdownLatch(url.Length);
            object syncLock = new object();
            int success = 0;

            DownloadStringCompletedEventHandler onComplete = (object sender, DownloadStringCompletedEventArgs e) =>
            {
                lock (syncLock)
                {
                    Log.Info("On Complete: " + e.UserState);

                    Exception error = e.Error;
                    if (error != null)
                    {
                        Log.Info("  Error=" + e.Error);
                    }
                    else
                    {
                        string result = e.Result;
                        Log.Info("  Result=" + e.Result);
                        if (expectedResponse == null || (result != null && result.Contains(expectedResponse)))
                            success++;
                    }
                }

                cdl.Signal();
            };

            foreach (string s in url)
            {
                Log.Info("Requesting: " + s);

                using (WebClient wb = new WebClient())
                {
                    wb.Proxy = null;
                    wb.Headers["User-Agent"] = "ETEUtils";
                    wb.QueryString.Add(var, content);
                    try
                    {
                        wb.DownloadStringAsync(new Uri(s), s);
                        wb.DownloadStringCompleted += onComplete;
                    }
                    catch (Exception ex)
                    {
                        Log.Info(ex);
                        cdl.Signal();
                    }
                }
            }

            cdl.Wait();
            Log.Info("BatchGet complete. Success=" + success);
            return success;
        }
    }
}
