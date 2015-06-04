using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Windows.Forms;
using System.IO;
using ETEUtils;
using Microsoft.Win32;

namespace HRARequestor
{
    class Server
    {
        private static volatile bool quit = false;
        private static string serverKey;

        public static void Start(Config conf)
        {
            if (!HttpListener.IsSupported)
            {
                MessageBox.Show("Windows XP SP2 or Server 2003 is required to use the HttpListener class.");
                return;
            }

            Server.serverKey = conf.ServerKey;

            // Create a listener.
            using (HttpListener listener = new HttpListener())
            {
                // Add the prefixes. 
                string protocol = conf.UseSSL ? "https" : "http";
                listener.Prefixes.Add(protocol + "://*:" + conf.Port + "/hra/");
                listener.Start();

                Console.WriteLine("Listening...");

                while (!quit)
                {
                    try
                    {
                        handleRequest(listener);
                    }
                    catch (Exception e)
                    {
                        Log.Info(e);
                    }
                }

                listener.Stop();
            }
        }

        private static void handleRequest(HttpListener listener)
        {
            // Note: The GetContext method blocks while waiting for a request. 
            HttpListenerContext context = listener.GetContext();
            HttpListenerRequest request = context.Request;

            using (HttpListenerResponse response = context.Response)
            {
                if (!validate(request))
                {
                    response.StatusCode = 401;  //unauthorized
                }
                else
                {
                    // Construct a response.
                    HraInvitation inv = HraInvitation.create();
                    string responseString = inv.ToJson();
                    byte[] buffer = System.Text.Encoding.UTF8.GetBytes(responseString);
                    // Get a response stream and write the response to it.
                    response.ContentLength64 = buffer.Length;
                    using (Stream output = response.OutputStream)
                    {
                        output.Write(buffer, 0, buffer.Length);
                    }
                }
            }
        }

        private static bool validate(HttpListenerRequest request)
        {
            string verificationCode = request.QueryString.Get("v");
            if (!validate(verificationCode))
                return false;

            //verify the requestor is a broker server
            //TODO
            //request.RemoteEndPoint.Address.ToString()

            return true;
        }

        
        private static bool validate(string code)
        {
            if (code == null)
                return false;

            if ("*".Equals(serverKey) || code.Equals(serverKey, StringComparison.OrdinalIgnoreCase))
                return true;
            return false;
        }
    }
}
