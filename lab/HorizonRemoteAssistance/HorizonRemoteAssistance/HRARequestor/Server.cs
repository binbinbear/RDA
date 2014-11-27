using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Windows.Forms;
using System.IO;

namespace HRARequestor
{
    class Server
    {
        private static volatile bool quit = false;

        public static void Start()
        {
            if (!HttpListener.IsSupported)
            {
                MessageBox.Show("Windows XP SP2 or Server 2003 is required to use the HttpListener class.");
                return;
            }

            // Create a listener.
            using (HttpListener listener = new HttpListener())
            {
                // Add the prefixes. 
                listener.Prefixes.Add("http://*:32121/hra/");
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
                        Console.WriteLine(e);
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

                string verificationCode = request.QueryString.Get("v");
                if (!validate(verificationCode))
                {
                    response.StatusCode = 401;  //unauthorized
                }
                else
                {
                    // Construct a response.
                    string responseString = HraInvitation.create();
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

        private static bool validate(string code)
        {
            if (code == null)
                return false;

            return true;
        }
    }
}
