using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security.Principal;
using System.Diagnostics;
using System.ComponentModel;
using Microsoft.Win32.SafeHandles;
using System.IO;

namespace CreateRAString
{
    public static class ProcessUtil
    {
        static readonly IntPtr INVALID_HANDLE_VALUE = (IntPtr)(-1);
        static readonly HandleRef NullHandleRef = new HandleRef(null, IntPtr.Zero);

        const int STD_INPUT_HANDLE = -10;
        public const UInt32 Infinite = 0xffffffff;
        const int STARTF_USESTDHANDLES = 0x100;

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool SetHandleInformation(IntPtr hObject, HANDLE_FLAGS dwMask, HANDLE_FLAGS dwFlags);

        [DllImport("kernel32.dll", EntryPoint = "CloseHandle", SetLastError = true, CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
        public static extern bool CloseHandle(IntPtr handle);

        [DllImport("kernel32.dll", SetLastError = true)]
        public static extern UInt32 WaitForSingleObject(IntPtr handle, UInt32 milliseconds);

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern int GetConsoleOutputCP();

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern IntPtr CreateNamedPipe(string name, int openMode, int pipeMode, int maxInstances, int outBufSize, int inBufSize, int timeout, IntPtr lpPipeAttributes);

        [DllImport("kernel32.dll", CharSet = CharSet.Auto)]
        public static extern IntPtr CreateFile(string lpFileName, int dwDesiredAccess, int dwShareMode, IntPtr lpSecurityAttributes, int dwCreationDisposition, int dwFlagsAndAttributes, HandleRef hTemplateFile);

        [DllImport("kernel32.dll", CharSet = CharSet.Ansi, SetLastError = true)]
        public static extern IntPtr GetStdHandle(int whichHandle);

        [DllImport("kernel32.dll", SetLastError = true)]
        public static extern bool GetExitCodeProcess(IntPtr process, ref UInt32 exitCode);

        [DllImport("advapi32.dll", EntryPoint = "CreateProcessAsUser", SetLastError = true, CharSet = CharSet.Ansi,
            CallingConvention = CallingConvention.StdCall)]
        public static extern bool CreateProcessAsUser(IntPtr hToken, String lpApplicationName, String lpCommandLine,
                                                      ref SECURITY_ATTRIBUTES lpProcessAttributes,
                                                      ref SECURITY_ATTRIBUTES lpThreadAttributes, bool bInheritHandle,
                                                      int dwCreationFlags, IntPtr lpEnvironment,
                                                      String lpCurrentDirectory, ref STARTUPINFO lpStartupInfo,
                                                      out PROCESS_INFORMATION lpProcessInformation);

        [DllImport("advapi32.dll", EntryPoint = "DuplicateTokenEx")]
        public static extern bool DuplicateTokenEx(IntPtr ExistingTokenHandle, uint dwDesiredAccess,
                                                   ref SECURITY_ATTRIBUTES lpThreadAttributes, int TokenType,
                                                   int ImpersonationLevel, ref IntPtr DuplicateTokenHandle);


        /// <summary>
        /// This is class is designed to operate inside an ASP.NET web application.
        /// The assumption is that the calling thread is operating with an impersonated security token.
        /// This class will change the imperonated security token to a primary token, and call CreateProcessAsUser.
        /// To use this function, the following security priviliges need to be set for the ASPNET account 
        /// using the local security policy MMC snap-in. CreateProcessAsUser requirement.
        /// "Replace a process level token"/SE_ASSIGNPRIMARYTOKEN_NAME/SeAssignPrimaryTokenPrivilege
        /// "Adjust memory quotas for a process"/SE_INCREASE_QUOTA_NAME/SeIncreaseQuotaPrivilege
        /// </summary>
        public static bool Start(string command, string workingDirectory)
        {
            bool ret;
            try
            {

                var identity = WindowsIdentity.GetCurrent();
                if (identity == null)
                {
                    Log.LogException("Start import conversion:  Get current identity token failed", null);
                    return false;
                }

                IntPtr Token = identity.Token;

                const uint GENERIC_ALL = 0x10000000;

                const int SecurityImpersonation = 2;
                const int TokenType = 1;

                var DupedToken = new IntPtr(0);

                var sa = new SECURITY_ATTRIBUTES { bInheritHandle = false };
                sa.nLength = Marshal.SizeOf(sa);
                sa.lpSecurityDescriptor = (IntPtr)0;

                ret = DuplicateTokenEx(Token, GENERIC_ALL, ref sa, SecurityImpersonation, TokenType, ref DupedToken);
                if (ret == false)
                {
                    Log.LogException(
                        "Start import conversion: DuplicateTokenEx failed with " + new Win32Exception().Message, null);
                    return false;
                }


                IntPtr stdoutReadHandle;
                IntPtr stdoutWriteHandle;
                IntPtr stdinHandle = GetStdHandle(STD_INPUT_HANDLE);
                CreatePipe(out stdoutReadHandle, out stdoutWriteHandle, false);
                SetHandleInformation(stdoutReadHandle, HANDLE_FLAGS.INHERIT, HANDLE_FLAGS.INHERIT);

                var si = new STARTUPINFO();
                si.cb = Marshal.SizeOf(si);
                si.lpDesktop = "";
                si.dwFlags = STARTF_USESTDHANDLES;
                si.hStdInput = stdinHandle;
                si.hStdOutput = stdoutWriteHandle;
                si.hStdError = stdoutWriteHandle;

                PROCESS_INFORMATION pi;
                ret = CreateProcessAsUser(DupedToken, null, command, ref sa, ref sa, true, 0, (IntPtr)0, workingDirectory,
                                          ref si, out pi);
                UInt32 exitCode = 123456;
                if (pi.hProcess != IntPtr.Zero)
                {
                    WaitForSingleObject(pi.hProcess, 180000);
                    GetExitCodeProcess(pi.hProcess, ref exitCode);
                }

                var lastException = new Win32Exception();

                CloseHandle(pi.hProcess);
                CloseHandle(pi.hThread);
                CloseHandle(stdoutWriteHandle);
                CloseHandle(stdinHandle);

                var safeHandle = new SafeFileHandle(stdoutReadHandle, true);
                string result;
                try
                {
                    var encoding = Encoding.GetEncoding(GetConsoleOutputCP());
                    var reader =
                        new StreamReader(
                            new FileStream(safeHandle, FileAccess.Read, 0x1000, true),
                            encoding);

                    result = reader.ReadToEnd();
                    reader.Close();
                }
                finally
                {
                    if (!safeHandle.IsClosed)
                    {
                        safeHandle.Close();
                    }
                }

                if (ret == false || exitCode > 0)
                {
                    Log.LogException(
                        "Start import conversion: CreateProcessAsUser failed with " + lastException.Message + " => Exitcode: " + exitCode + " => Output: " + (string.IsNullOrEmpty(result) ? string.Empty : result), null);
                    return false;
                }


                ret = CloseHandle(DupedToken);

                if (ret == false)
                {
                    Log.LogException("Start import conversion: Closing token failed with " + new Win32Exception().Message,
                                       null);
                }

            }
            catch (Exception e)
            {
                ret = false;
                Log.LogFatalException(e);
            }
            return ret;
        }

        private static void CreatePipe(out IntPtr parentHandle, out IntPtr childHandle, bool parentInputs)
        {
            string pipename = @"\\.\pipe\" + Guid.NewGuid().ToString();

            parentHandle = CreateNamedPipe(pipename, 0x40000003, 0, 0xff, 0x1000, 0x1000, 0, IntPtr.Zero);
            if (parentHandle == INVALID_HANDLE_VALUE)
            {
                throw new Win32Exception();
            }

            int childAcc = 0x40000000;
            if (parentInputs)
            {
                childAcc = -2147483648;
            }
            childHandle = CreateFile(pipename, childAcc, 3, IntPtr.Zero, 3, 0x40000080, NullHandleRef);
            if (childHandle == INVALID_HANDLE_VALUE)
            {
                throw new Win32Exception();
            }
        }

        [Flags]
        enum HANDLE_FLAGS
        {
            INHERIT = 1,
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct STARTUPINFO
        {
            public int cb;
            public String lpReserved;
            public String lpDesktop;
            public String lpTitle;
            public uint dwX;
            public uint dwY;
            public uint dwXSize;
            public uint dwYSize;
            public uint dwXCountChars;
            public uint dwYCountChars;
            public uint dwFillAttribute;
            public uint dwFlags;
            public short wShowWindow;
            public short cbReserved2;
            public IntPtr lpReserved2;
            public IntPtr hStdInput;
            public IntPtr hStdOutput;
            public IntPtr hStdError;
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct PROCESS_INFORMATION
        {
            public IntPtr hProcess;
            public IntPtr hThread;
            public uint dwProcessId;
            public uint dwThreadId;
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct SECURITY_ATTRIBUTES
        {
            public int nLength;
            public IntPtr lpSecurityDescriptor;
            public bool bInheritHandle;
        }
    }
}
