using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Security;
using System.ComponentModel;
using Microsoft.Win32.SafeHandles;
using System.IO;
using System.Threading;
using ETEUtils;

namespace CreateRAString
{

    public class UserSessionInfo
    {
        public uint SessionId { get; set; }
        public uint ExplorerPid { get; set; }
    }

    class UserAppLauncher
    {
        public enum TOKEN_INFORMATION_CLASS
        {
            TokenUser = 1,
            TokenGroups,
            TokenPrivileges,
            TokenOwner,
            TokenPrimaryGroup,
            TokenDefaultDacl,
            TokenSource,
            TokenType,
            TokenImpersonationLevel,
            TokenStatistics,
            TokenRestrictedSids,
            TokenSessionId,
            TokenGroupsAndPrivileges,
            TokenSessionReference,
            TokenSandBoxInert,
            TokenAuditPolicy,
            TokenOrigin,
            MaxTokenInfoClass // MaxTokenInfoClass should always be the last enum
        }

        public const int READ_CONTROL = 0x00020000;

        public const int STANDARD_RIGHTS_REQUIRED = 0x000F0000;

        public const int STANDARD_RIGHTS_READ = READ_CONTROL;
        public const int STANDARD_RIGHTS_WRITE = READ_CONTROL;
        public const int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;

        public const int STANDARD_RIGHTS_ALL = 0x001F0000;

        public const int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

        public const int TOKEN_ASSIGN_PRIMARY = 0x0001;
        public const int TOKEN_DUPLICATE = 0x0002;
        public const int TOKEN_IMPERSONATE = 0x0004;
        public const int TOKEN_QUERY = 0x0008;
        public const int TOKEN_QUERY_SOURCE = 0x0010;
        public const int TOKEN_ADJUST_PRIVILEGES = 0x0020;
        public const int TOKEN_ADJUST_GROUPS = 0x0040;
        public const int TOKEN_ADJUST_DEFAULT = 0x0080;
        public const int TOKEN_ADJUST_SESSIONID = 0x0100;

        public const int TOKEN_ALL_ACCESS_P = (STANDARD_RIGHTS_REQUIRED |
                                               TOKEN_ASSIGN_PRIMARY |
                                               TOKEN_DUPLICATE |
                                               TOKEN_IMPERSONATE |
                                               TOKEN_QUERY |
                                               TOKEN_QUERY_SOURCE |
                                               TOKEN_ADJUST_PRIVILEGES |
                                               TOKEN_ADJUST_GROUPS |
                                               TOKEN_ADJUST_DEFAULT);

        public const int TOKEN_ALL_ACCESS = TOKEN_ALL_ACCESS_P | TOKEN_ADJUST_SESSIONID;

        public const int TOKEN_READ = STANDARD_RIGHTS_READ | TOKEN_QUERY;

        public const int TOKEN_WRITE = STANDARD_RIGHTS_WRITE |
                                       TOKEN_ADJUST_PRIVILEGES |
                                       TOKEN_ADJUST_GROUPS |
                                       TOKEN_ADJUST_DEFAULT;

        public const int TOKEN_EXECUTE = STANDARD_RIGHTS_EXECUTE;

        public const uint MAXIMUM_ALLOWED = 0x2000000;

        public const int CREATE_NEW_PROCESS_GROUP = 0x00000200;
        public const int CREATE_UNICODE_ENVIRONMENT = 0x00000400;

        public const int IDLE_PRIORITY_CLASS = 0x40;
        public const int NORMAL_PRIORITY_CLASS = 0x20;
        public const int HIGH_PRIORITY_CLASS = 0x80;
        public const int REALTIME_PRIORITY_CLASS = 0x100;

        public const int CREATE_NEW_CONSOLE = 0x00000010;

        public const string SE_DEBUG_NAME = "SeDebugPrivilege";
        public const string SE_RESTORE_NAME = "SeRestorePrivilege";
        public const string SE_BACKUP_NAME = "SeBackupPrivilege";

        public const int SE_PRIVILEGE_ENABLED = 0x0002;

        public const int ERROR_NOT_ALL_ASSIGNED = 1300;

        private const uint TH32CS_SNAPPROCESS = 0x00000002;

        public static int INVALID_HANDLE_VALUE = -1;

        static readonly IntPtr INVALID_HANDLE_INTPTR = (IntPtr)(-1);

        static readonly HandleRef NullHandleRef = new HandleRef(null, IntPtr.Zero);

        const int STD_INPUT_HANDLE = -10;
        public const UInt32 Infinite = 0xffffffff;
        const int STARTF_USESTDHANDLES = 0x100;


        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool LookupPrivilegeValue(IntPtr lpSystemName, string lpname,
            [MarshalAs(UnmanagedType.Struct)] ref LUID lpLuid);

        [DllImport("advapi32.dll", EntryPoint = "CreateProcessAsUser", SetLastError = true, CharSet = CharSet.Ansi,
            CallingConvention = CallingConvention.StdCall)]
        public static extern bool CreateProcessAsUser(IntPtr hToken, String lpApplicationName, String lpCommandLine,
            ref SECURITY_ATTRIBUTES lpProcessAttributes,
            ref SECURITY_ATTRIBUTES lpThreadAttributes, bool bInheritHandle, int dwCreationFlags, IntPtr lpEnvironment,
            String lpCurrentDirectory, ref STARTUPINFO lpStartupInfo, out PROCESS_INFORMATION lpProcessInformation);

        [DllImport("advapi32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern bool DuplicateToken(IntPtr ExistingTokenHandle,
            int SECURITY_IMPERSONATION_LEVEL, ref IntPtr DuplicateTokenHandle);

        [DllImport("advapi32.dll", EntryPoint = "DuplicateTokenEx")]
        public static extern bool DuplicateTokenEx(IntPtr ExistingTokenHandle, uint dwDesiredAccess,
            ref SECURITY_ATTRIBUTES lpThreadAttributes, int TokenType,
            int ImpersonationLevel, ref IntPtr DuplicateTokenHandle);

        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool AdjustTokenPrivileges(IntPtr TokenHandle, bool DisableAllPrivileges,
            ref TOKEN_PRIVILEGES NewState, int BufferLength, IntPtr PreviousState, IntPtr ReturnLength);

        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool SetTokenInformation(IntPtr TokenHandle, TOKEN_INFORMATION_CLASS TokenInformationClass,
            ref uint TokenInformation, uint TokenInformationLength);

        [DllImport("userenv.dll", SetLastError = true)]
        public static extern bool CreateEnvironmentBlock(ref IntPtr lpEnvironment, IntPtr hToken, bool bInherit);


        public static UserSessionInfo FindUserSession(string user)
        {
            // Find the winlogon process
            var procEntry = new PROCESSENTRY32();

            uint hSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
            if (hSnap == INVALID_HANDLE_VALUE)
            {
                return null;
            }

            procEntry.dwSize = (uint)Marshal.SizeOf(procEntry); //sizeof(PROCESSENTRY32);

            if (Process32First(hSnap, ref procEntry) == 0)
            {
                return null;
            }

            String strCmp = "explorer.exe";
            do
            {
                if (strCmp.IndexOf(procEntry.szExeFile) == 0)
                {
                    // We found a winlogon process...make sure it's running in the console session
                    uint sessId = 0;
                    bool success = ProcessIdToSessionId(procEntry.th32ProcessID, ref sessId);
                    if (success)
                    {
                        return new UserSessionInfo() { SessionId = sessId, ExplorerPid = procEntry.th32ProcessID };
                    }
                }
            }
            while (Process32Next(hSnap, ref procEntry) != 0);

            return null;
        }

        private static object[] DupUserToken(UserSessionInfo sessionInfo, bool bElevate)
        {

            IntPtr hUserTokenDup = IntPtr.Zero;
            IntPtr hPToken = IntPtr.Zero;
            IntPtr hProcess = IntPtr.Zero;

            Debug.Print("CreateProcessInConsoleSession");

            uint dwSessionId = sessionInfo.SessionId; //WTSGetActiveConsoleSessionId();


            hProcess = OpenProcess(MAXIMUM_ALLOWED, false, sessionInfo.ExplorerPid);

            if (
                !OpenProcessToken(hProcess,
                    TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY | TOKEN_DUPLICATE | TOKEN_ASSIGN_PRIMARY
                    | TOKEN_ADJUST_SESSIONID | TOKEN_READ | TOKEN_WRITE, ref hPToken))
            {
                Debug.Print(String.Format("CreateProcessInConsoleSession OpenProcessToken error: {0}",
                    Marshal.GetLastWin32Error()));
            }

            var luid = new LUID();
            if (!LookupPrivilegeValue(IntPtr.Zero, SE_DEBUG_NAME, ref luid))
            {
                Debug.Print(String.Format("CreateProcessInConsoleSession LookupPrivilegeValue error: {0}",
                    Marshal.GetLastWin32Error()));
            }

            var sa = new SECURITY_ATTRIBUTES();
            sa.Length = Marshal.SizeOf(sa);

            if (!DuplicateTokenEx(hPToken, MAXIMUM_ALLOWED, ref sa,
                    (int)SECURITY_IMPERSONATION_LEVEL.SecurityIdentification, (int)TOKEN_TYPE.TokenPrimary,
                    ref hUserTokenDup))
            {
                Debug.Print(
                    String.Format(
                        "CreateProcessInConsoleSession DuplicateTokenEx error: {0} Token does not have the privilege.",
                        Marshal.GetLastWin32Error()));
                CloseHandle(hProcess);
                CloseHandle(hPToken);
                return null;
            }

            if (bElevate)
            {
                //tp.Privileges[0].Luid = luid;
                //tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
                var tp = new TOKEN_PRIVILEGES();
                tp.PrivilegeCount = 1;
                tp.Privileges = new int[3];
                tp.Privileges[2] = SE_PRIVILEGE_ENABLED;
                tp.Privileges[1] = luid.HighPart;
                tp.Privileges[0] = luid.LowPart;

                //Adjust Token privilege
                if (
                    !SetTokenInformation(hUserTokenDup, TOKEN_INFORMATION_CLASS.TokenSessionId, ref dwSessionId,
                        (uint)IntPtr.Size))
                {
                    Debug.Print(
                        String.Format(
                            "CreateProcessInConsoleSession SetTokenInformation error: {0} Token does not have the privilege.",
                            Marshal.GetLastWin32Error()));
                    //CloseHandle(hProcess);
                    //CloseHandle(hUserToken);
                    //CloseHandle(hPToken);
                    //CloseHandle(hUserTokenDup);
                    //return false;
                }
                if (
                    !AdjustTokenPrivileges(hUserTokenDup, false, ref tp, Marshal.SizeOf(tp), /*(PTOKEN_PRIVILEGES)*/
                        IntPtr.Zero, IntPtr.Zero))
                {
                    int nErr = Marshal.GetLastWin32Error();

                    if (nErr == ERROR_NOT_ALL_ASSIGNED)
                    {
                        Debug.Print(
                            String.Format(
                                "CreateProcessInConsoleSession AdjustTokenPrivileges error: {0} Token does not have the privilege.",
                                nErr));
                    }
                    else
                    {
                        Debug.Print(String.Format("CreateProcessInConsoleSession AdjustTokenPrivileges error: {0}", nErr));
                    }
                }
            }

            CloseHandle(hPToken);
            CloseHandle(hProcess);

            return new object[] {hUserTokenDup, sa};
        }

        public static bool CreateProcessInConsoleSession(string user, string commandLine, bool bElevate)
        {
            UserSessionInfo sessionInfo = FindUserSession(user);
            if (sessionInfo == null)
                return false;

            return CreateProcessInConsoleSession(sessionInfo, commandLine, bElevate);
        }

        public static bool CreateProcessInConsoleSession(string commandLine, bool bElevate)
        {
            return CreateProcessInConsoleSession((string)null, commandLine, bElevate);
        }

        public static string LastOutput { get; set;}

        private static bool CreateProcessInConsoleSession(UserSessionInfo sessionInfo, string commandLine, bool bElevate)
        {
            LastOutput = null;

            object[] ret = DupUserToken(sessionInfo, bElevate);
            if (ret == null)
                return false;

            IntPtr hUserTokenDup = (IntPtr) ret[0];
            SECURITY_ATTRIBUTES sa = (SECURITY_ATTRIBUTES) ret[1];

            if (IntPtr.Zero.Equals(hUserTokenDup))
                return false;

            uint dwCreationFlags = NORMAL_PRIORITY_CLASS;   // | CREATE_NEW_CONSOLE;
            IntPtr pEnv = IntPtr.Zero;
            if (CreateEnvironmentBlock(ref pEnv, hUserTokenDup, true))
            {
                dwCreationFlags |= CREATE_UNICODE_ENVIRONMENT;
            }
            else
            {
                pEnv = IntPtr.Zero;
            }


            // Launch the process in the client's logon session.
            


            var startupInfo = new STARTUPINFO();
            startupInfo.cb = Marshal.SizeOf(startupInfo);
            startupInfo.lpDesktop = "";  //"winsta0\\default";
            startupInfo.dwFlags = STARTF_USESTDHANDLES;

            //SafeFileHandle inputHandle = null;
//            SafeFileHandle outputHandle = null;
            //SafeFileHandle errorHandle = null;
            //SafeFileHandle stdInput = null;
//            SafeFileHandle stdOutput = null;
            //SafeFileHandle stdError = null;
            //CreatePipe2(out inputHandle, out stdInput, true);
//            CreatePipe2(out outputHandle, out stdOutput, false);
            //CreatePipe2(out errorHandle, out stdError, false);
            //startupInfo.hStdInput = stdInput.DangerousGetHandle();
//            startupInfo.hStdOutput = stdOutput.DangerousGetHandle();
            //startupInfo.hStdError = stdError.DangerousGetHandle();


            //*
            //IntPtr stdoutReadHandle;
            //IntPtr stdoutWriteHandle = CreateFile("c:\\ooo.txt", 3, 0, IntPtr.Zero, 0, 0, NullHandleRef);
            //IntPtr stdinHandle = GetStdHandle(STD_INPUT_HANDLE);
            //CreatePipe(out stdoutReadHandle, out stdoutWriteHandle, false);
            
            //SetHandleInformation(stdoutReadHandle, HANDLE_FLAGS.INHERIT, HANDLE_FLAGS.INHERIT);

            //SetHandleInformation(stdoutReadHandle, HANDLE_FLAGS.INHERIT, 0);
            //SetHandleInformation(stdoutWriteHandle, HANDLE_FLAGS.INHERIT, 0);

            //startupInfo.hStdOutput = stdoutWriteHandle;
            //*/

            

            PROCESS_INFORMATION pi;

            bool bResult = CreateProcessAsUser(hUserTokenDup, // client's access token
                null, // file to execute
                commandLine, // command line
                ref sa, // pointer to process SECURITY_ATTRIBUTES
                ref sa, // pointer to thread SECURITY_ATTRIBUTES
                false, // handles are not inheritable
                (int)dwCreationFlags, // creation flags
                pEnv, // pointer to new environment block
                null, // name of current directory
                ref startupInfo, // pointer to STARTUPINFO structure
                out pi // receives information about new process
                );
            // End impersonation of client.

            //GetLastError should be 0
            int iResultOfCreateProcessAsUser = Marshal.GetLastWin32Error();


            /*
            Thread t = new Thread(new ThreadStart(() => {
                var safeHandle = new SafeFileHandle(stdoutReadHandle, true);
                //var safeHandle = outputHandle;
                try
                {
                    //var encoding = Encoding.GetEncoding(GetConsoleOutputCP());
                    var encoding = Console.OutputEncoding;
                    using (var reader =
                        new StreamReader(
                            new FileStream(safeHandle, FileAccess.Read, 0x1000, true),
                            encoding, true, 0x1000))
                    {
   
                        LastOutput = reader.ReadToEnd();
                    }
                }
                finally
                {
                    if (!safeHandle.IsClosed)
                    {
                        safeHandle.Close();
                    }
                }
            }));
            t.Start();
            */         
            



            UInt32 exitCode = 123456;
            if (pi.hProcess != IntPtr.Zero)
            {
                WaitForSingleObject(pi.hProcess, 180000);
                GetExitCodeProcess(pi.hProcess, ref exitCode);
            }

            var lastException = new Win32Exception();

            //CloseHandle(stdoutWriteHandle);

            //inputHandle.Close();
            //outputHandle.Close();
            //errorHandle.Close();


            //stdInput.Close();
            //stdOutput.Close();
            //stdError.Close();
            //CloseHandle(stdoutWriteHandle);

            CloseHandle(pi.hProcess);
            CloseHandle(pi.hThread);


            if (ret == null || exitCode > 0)
            {
                Log.Error(
                    "Start import conversion: CreateProcessAsUser failed with " + lastException.Message + " => Exitcode: " + exitCode + " => Output: " + (string.IsNullOrEmpty(LastOutput) ? string.Empty : LastOutput));
                return false;
            }

            //Close handles task
            CloseHandle(hUserTokenDup);

            return (iResultOfCreateProcessAsUser == 0) ? true : false;
        }

        public static void CreatePipe2(out SafeFileHandle parentHandle, out SafeFileHandle childHandle, bool parentInputs)
        {
            SECURITY_ATTRIBUTES lpPipeAttributes = new SECURITY_ATTRIBUTES();
            lpPipeAttributes.bInheritHandle = true;
            SafeFileHandle hWritePipe = null;
            try
            {
                if (parentInputs)
                    CreatePipeWithSecurityAttributes(out childHandle, out hWritePipe, lpPipeAttributes, 0);
                else
                    CreatePipeWithSecurityAttributes(out hWritePipe, out childHandle, lpPipeAttributes, 0);
                if (!DuplicateHandle(GetCurrentProcess(), hWritePipe, GetCurrentProcess(), out parentHandle, 0, false, 2))
                    throw new Exception();
            }
            finally
            {
                if ((hWritePipe != null) && !hWritePipe.IsInvalid)
                {
                    hWritePipe.Close();
                }
            }
        }

        private static void CreatePipeWithSecurityAttributes(out SafeFileHandle hReadPipe, out SafeFileHandle hWritePipe,
            SECURITY_ATTRIBUTES lpPipeAttributes, int nSize)
        {
            hReadPipe = null;
            if ((!CreatePipe(out hReadPipe, out hWritePipe, lpPipeAttributes, nSize) || hReadPipe.IsInvalid) || hWritePipe.IsInvalid)
                throw new Exception();
        }

        private static void CreatePipe(out IntPtr parentHandle, out IntPtr childHandle, bool parentInputs)
        {
            string pipename = @"\\.\pipe\" + Guid.NewGuid().ToString();

            parentHandle = CreateNamedPipe(pipename, 0x40000003, 0, 0xff, 0x1000, 0x1000, 0, IntPtr.Zero);
            if (parentHandle == INVALID_HANDLE_INTPTR)
            {
                throw new Win32Exception();
            }

            int childAcc = 0x40000000;
            if (parentInputs)
            {
                childAcc = -2147483648;
            }
            childHandle = CreateFile(pipename, childAcc, 3, IntPtr.Zero, 3, 0x40000080, NullHandleRef);
            if (childHandle == INVALID_HANDLE_INTPTR)
            {
                throw new Win32Exception();
            }
        }

        [Flags]
        enum HANDLE_FLAGS
        {
            INHERIT = 1,
        }



        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern bool CreatePipe(out SafeFileHandle hReadPipe, out SafeFileHandle hWritePipe,
            SECURITY_ATTRIBUTES lpPipeAttributes, int nSize);
        [DllImport("kernel32.dll", CharSet = CharSet.Ansi, SetLastError = true)]
        public static extern bool DuplicateHandle(IntPtr hSourceProcessHandle, SafeHandle hSourceHandle,
            IntPtr hTargetProcess, out SafeFileHandle targetHandle, int dwDesiredAccess,
            bool bInheritHandle, int dwOptions);
        [DllImport("kernel32.dll", CharSet = CharSet.Ansi, SetLastError = true)]
        public static extern IntPtr GetCurrentProcess();

        [DllImport("kernel32.dll")]
        private static extern int Process32First(uint hSnapshot, ref PROCESSENTRY32 lppe);

        [DllImport("kernel32.dll")]
        private static extern int Process32Next(uint hSnapshot, ref PROCESSENTRY32 lppe);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern uint CreateToolhelp32Snapshot(uint dwFlags, uint th32ProcessID);


        [DllImport("kernel32.dll")]
        public static extern uint WTSGetActiveConsoleSessionId();

        [DllImport("Wtsapi32.dll")]
        private static extern uint WTSQueryUserToken(uint SessionId, ref IntPtr phToken);

        [DllImport("kernel32.dll")]
        private static extern bool ProcessIdToSessionId(uint dwProcessId, ref uint pSessionId);

        [DllImport("kernel32.dll")]
        private static extern IntPtr OpenProcess(uint dwDesiredAccess, bool bInheritHandle, uint dwProcessId);


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

        [DllImport("advapi32", SetLastError = true)]
        [SuppressUnmanagedCodeSecurity]
        private static extern bool OpenProcessToken(IntPtr ProcessHandle, // handle to process
            int DesiredAccess, // desired access to process
            ref IntPtr TokenHandle);


        #region Nested type: LUID

        [StructLayout(LayoutKind.Sequential)]
        internal struct LUID
        {
            public int LowPart;
            public int HighPart;
        }

        #endregion

        //end struct

        #region Nested type: LUID_AND_ATRIBUTES

        [StructLayout(LayoutKind.Sequential)]
        internal struct LUID_AND_ATRIBUTES
        {
            public LUID Luid;
            public int Attributes;
        }

        #endregion

        #region Nested type: PROCESSENTRY32

        [StructLayout(LayoutKind.Sequential)]
        private struct PROCESSENTRY32
        {
            public uint dwSize;
            public readonly uint cntUsage;
            public readonly uint th32ProcessID;
            public readonly IntPtr th32DefaultHeapID;
            public readonly uint th32ModuleID;
            public readonly uint cntThreads;
            public readonly uint th32ParentProcessID;
            public readonly int pcPriClassBase;
            public readonly uint dwFlags;

            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 260)]
            public readonly string szExeFile;
        }

        #endregion

        #region Nested type: PROCESS_INFORMATION

        [StructLayout(LayoutKind.Sequential)]
        public struct PROCESS_INFORMATION
        {
            public IntPtr hProcess;
            public IntPtr hThread;
            public uint dwProcessId;
            public uint dwThreadId;
        }

        #endregion

        #region Nested type: SECURITY_ATTRIBUTES

        [StructLayout(LayoutKind.Sequential)]
        public struct SECURITY_ATTRIBUTES
        {
            public int Length;
            public IntPtr lpSecurityDescriptor;
            public bool bInheritHandle;
        }

        #endregion

        #region Nested type: SECURITY_IMPERSONATION_LEVEL

        private enum SECURITY_IMPERSONATION_LEVEL
        {
            SecurityAnonymous = 0,
            SecurityIdentification = 1,
            SecurityImpersonation = 2,
            SecurityDelegation = 3,
        }

        #endregion

        #region Nested type: STARTUPINFO

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

        #endregion

        #region Nested type: TOKEN_PRIVILEGES

        [StructLayout(LayoutKind.Sequential)]
        internal struct TOKEN_PRIVILEGES
        {
            internal int PrivilegeCount;
            //LUID_AND_ATRIBUTES
            [MarshalAs(UnmanagedType.ByValArray, SizeConst = 3)]
            internal int[] Privileges;
        }

        #endregion

        #region Nested type: TOKEN_TYPE

        private enum TOKEN_TYPE
        {
            TokenPrimary = 1,
            TokenImpersonation = 2
        }

        #endregion

        // handle to open access token
    }
}
