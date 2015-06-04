using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;
using ETEUtils;
using Microsoft.Win32;

namespace Test2
{
    class Server
    {
        private static volatile bool quit = false;
        private static string serverKey;

        public static void Start(int port, string serverKey)
        {
            if (!HttpListener.IsSupported)
            {
                return;
            }

            Server.serverKey = serverKey;

            // Create a listener.
            using (HttpListener listener = new HttpListener())
            {
                // Add the prefixes. 
                listener.Prefixes.Add("http://*:" + port + "/hra/");
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
                    string responseString = CreateTestInv();
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

        private static string CreateTestInv()
        {
            return "{machine:'mm',user:'uu',domain:'dd',os:'oo',inv:'ii',code:'cc',nonce:'" + DateTime.Now + "'}";
        }

        private static bool validate(HttpListenerRequest request)
        {
            string verificationCode = request.QueryString.Get("v");
            if (!validate(verificationCode))
                return false;

            //verify the requestor is a broker server

            Console.WriteLine(request.RemoteEndPoint.Address.ToString());

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
