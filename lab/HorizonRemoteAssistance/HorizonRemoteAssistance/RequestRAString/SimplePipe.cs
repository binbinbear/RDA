using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;
using System.Runtime.InteropServices;
using Microsoft.Win32.SafeHandles;
using System.IO;
using System.Threading;

namespace CreateRAString
{
    /*
     * Simple Pipe Util prior to .NET 3.0.
     * This is specially designed for simple inter-process communication,
     * avoid creation of file. 
     * Normally for simple data exchange, especially single direction, pipe is not
     * needed because stdin/stdout can handle the task. However with specific context
     * (e.g. between system process and user process), there are always error redirecting
     * the stdin/stdout using pipes. Hence this explicit named pipe is created.
     * 
     * Usage example:
     *  refer to Test.TestOne()
     *  
     * 
     *          //server
     *          using (SimplePipe pipe = SimplePipe.CreateServer())
     *          {
     *              //simulate client. designed to happen somewhere else (e.g. sub process).
     *              ThreadPool.QueueUserWorkItem((_) =>
     *              {
     *                  SimplePipe.Write(pipe.Name, "something");
     *              });
     *
     *              //server
     *              string recv = pipe.Read(timeoutMillis);
     *              if (recv == null)
     *              {
     *                  //read failure...
     *              }
     *          }
     * 
     * nanw
     * 
     */
    public class SimplePipe : IDisposable
    {

        private static readonly IntPtr INVALID_HANDLE_INTPTR = (IntPtr)(-1);

        private static readonly HandleRef NullHandleRef = new HandleRef(null, IntPtr.Zero);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern SafeFileHandle CreateNamedPipe(
           String pipeName,
           uint dwOpenMode,
           uint dwPipeMode,
           uint nMaxInstances,
           uint nOutBufferSize,
           uint nInBufferSize,
           uint nDefaultTimeOut,
           IntPtr lpSecurityAttributes);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern int ConnectNamedPipe(
           SafeFileHandle hNamedPipe,
           IntPtr lpOverlapped);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern SafeFileHandle CreateFile(
           String pipeName,
           uint dwDesiredAccess,
           uint dwShareMode,
           IntPtr lpSecurityAttributes,
           uint dwCreationDisposition,
           uint dwFlagsAndAttributes,
           IntPtr hTemplate);

        private const uint GENERIC_READ = (0x80000000);
        private const uint GENERIC_WRITE = (0x40000000);
        private const uint OPEN_EXISTING = 3;

        private const uint PIPE_ACCESS_INBOUND = 0x00000001;
        private const uint PIPE_ACCESS_OUTBOUND = 0x00000002;
        private const uint DUPLEX = (0x00000003);

        private const uint FILE_FLAG_OVERLAPPED = 0x40000000;
        private const uint FILE_FLAG_FIRST_PIPE_INSTANCE = 0x00080000;

        private const int BUFFER_SIZE = 16384;


        private const int PIPE_TYPE_BYTE = 0x00000000;

        private SafeFileHandle handle;
        private volatile string result;
        private ManualResetEvent readCompleteOrError = new ManualResetEvent(false);

        private SimplePipe()
        {
            Name = Guid.NewGuid().ToString();

            string pipeName = @"\\.\pipe\" + Name;

            handle =
                CreateNamedPipe(
                     pipeName,
                     PIPE_ACCESS_INBOUND | FILE_FLAG_FIRST_PIPE_INSTANCE,
                     PIPE_TYPE_BYTE,
                     1, //maxInstance
                     0,
                     BUFFER_SIZE,
                     0,
                     IntPtr.Zero);

            if (handle.IsInvalid)
                throw new Win32Exception();

            //we must start the "listen" function here, and wait for its completion.
            //if the client pipe connects earlier than our "listen", the connection will fail.

            using (ManualResetEvent initialized = new ManualResetEvent(false))
            {

                ThreadPool.QueueUserWorkItem((_) =>
                {
                    try
                    {
                        initialized.Set();

                        int success = ConnectNamedPipe(handle, IntPtr.Zero);

                        //could not connect client
                        if (success == 0)
                            return;

                        var encoding = Console.OutputEncoding;
                        using (var reader =
                            new StreamReader(
                                new FileStream(handle, FileAccess.Read, BUFFER_SIZE, false),
                                encoding, true, BUFFER_SIZE))
                        {
                            result = reader.ReadToEnd();
                        }
                    }
                    finally
                    {
                        readCompleteOrError.Set();
                    }
                });

                //this is the best effort to make sure our sync ConnectNamedPipe happens before CreateFile (called by client).
                //with crazy test, with this initialized.WaitOne, the connection failure happens about 1/10000. Without it,
                //it happens on 1/1000 rate (10 times worse withou this mechanism).
                initialized.WaitOne();
            }
        }

        public static SimplePipe CreateServer()
        {
            return new SimplePipe();
        }

        public string Name
        {
            get;
            set;
        }

        /*
        private string Read()
        {
            int success = ConnectNamedPipe(handle, IntPtr.Zero);

            //could not connect client
            if (success == 0)
            {
                return null;
            }

            var encoding = Console.OutputEncoding;
            using (var reader =
                new StreamReader(
                    new FileStream(handle, FileAccess.Read, BUFFER_SIZE, false),
                    encoding, true, BUFFER_SIZE))
            {
                return reader.ReadToEnd();
            }
        }
        */

        /**
         * Read all text from the pipe.
         * Return null if timeout.
         */
        public string Read(int timeoutMillis)
        {
            //if we start a new thread to do the Read, 90% cases that the Connect to named pipe method fails immediately. This is strange.
            //so change to start a timed thread to "cancel" the current on-thread Read, if fails.
            /*
            Thread t = new Thread(new ThreadStart(() => Read()));
            t.Start();
            if (!t.Join(timeoutMillis))
            {
                //this happens when the other end of the pipe fails.
                //we must asynchronously cancel the blocking receive, by a trick: we write to the pipe by ourselves.
                Write(Name, "");

                handle.Close();
                return null;
            }
            return receivedText;
             */

            //this does not work, too. We MUST start the read (wait for connection, like listen on socket) prior to starting client/sub process.
            //the case of failure is rare, but indeed possible. (about 6/4000)
            /*
            bool timeout = false;
            Thread t = new Thread(new ThreadStart(() =>
            {
                try
                {
                    Thread.Sleep(timeoutMillis);
                    timeout = true;
                    Write(Name, null);
                }
                catch (Exception)
                {
                }
            }));
            t.Start();

            string ret = Read();

            //cancel the thread if we finish earlier.
            if (t.IsAlive)
                t.Interrupt();

            //This is not a must. Close earlier to release resource earlier.
            handle.Close();

            if (timeout && "".Equals(ret))
                return null;

            return ret;
             */

            if (!readCompleteOrError.WaitOne(timeoutMillis))
            {
                //timeout
                //trick to async cancel the read, by writing to it by myself.
                Write(Name, null);
                return null;
            }
            else
            {
                return result;
            }
        }

        public static bool Write(string name, string text)
        {
            string pipeName = @"\\.\pipe\" + name;

            using (SafeFileHandle handle =
               CreateFile(
                  pipeName,
                  GENERIC_WRITE,
                  0,
                  IntPtr.Zero,
                  OPEN_EXISTING,
                  FILE_FLAG_FIRST_PIPE_INSTANCE,
                  IntPtr.Zero))
            {

                //could not create handle - server probably not running
                if (handle.IsInvalid)
                    //throw new Win32Exception();
                    return false;   //this could happen when the server timeout and closes the pipe (by writing to it).

                var encoding = Console.OutputEncoding;
                using (var writer =
                    new StreamWriter(
                        new FileStream(handle, FileAccess.Write, BUFFER_SIZE, false),
                        encoding, BUFFER_SIZE))
                {

                    writer.Write(text);
                    return true;
                }
            }
        }

        #region IDisposable Members

        void IDisposable.Dispose()
        {
            handle.Close();
            readCompleteOrError.Close();
        }

        #endregion

        /// <summary>
        /// ////////////////////////////////////////////////////////////////////
        /// </summary>

        public class Test
        {

            static string[] txt = new string[1000];
            static int i = 0;
            static Random rand = new Random();
            static int errCount;

            public static void TestOne()
            {
                string recv;

                //server
                using (SimplePipe pipe = SimplePipe.CreateServer())
                {

                    //simulate client. designed to happen somewhere else (e.g. sub process).
                    ThreadPool.QueueUserWorkItem((_) =>
                    {
                        string s = "* " + (i++) + txt[rand.Next(0, txt.Length - 1)];
                        SimplePipe.Write(pipe.Name, s);
                    });

                    //server
                    recv = pipe.Read(1000);
                }

                if (recv == null || !recv.StartsWith("*"))
                    errCount++;
                if (i % 1000 == 0)
                {
                    Console.WriteLine("Error/Total: " + errCount + "/" + i);
                    Console.WriteLine("r: " + recv);
                }

            }

            public static void TestSubProcessFailure()
            {
                string recv;

                //server
                using (SimplePipe pipe = SimplePipe.CreateServer())
                {

                    //no client. simulate sub process failure

                    //server
                    recv = pipe.Read(1000);
                }

                if (recv == null)
                {
                    Console.WriteLine("Read timeout works correctly: null returned.");
                }
                else
                {
                    throw new Exception("how could this be...");
                }
                Console.WriteLine("Done.");
            }

            public static void TestLoop()
            {
                for (int i = 0; i < txt.Length; i++)
                {
                    string s = "";
                    for (int j = 0; j < i; j++)
                        s += "a";

                    txt[i] = s;
                }

                while (true)
                {
                    TestOne();
                }
            }
        }

    }
}
