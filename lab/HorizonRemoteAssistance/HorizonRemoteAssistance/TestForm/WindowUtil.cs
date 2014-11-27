using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Windows.Forms;
using System.Threading;

namespace TestForm
{
    public class WindowUtil
    {
        public delegate bool EnumWindowProc(IntPtr hWnd, IntPtr parameter);

        [DllImport("user32.dll", SetLastError = true)]
        static extern IntPtr FindWindowEx(IntPtr hwndParent, IntPtr hwndChildAfter, string lpszClass, string lpszWindow);

        [DllImport("user32.dll", SetLastError = true)]
        public static extern IntPtr FindWindowEx(IntPtr parentHandle, IntPtr childAfter, string className, IntPtr windowTitle);

        [DllImport("user32", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        public static extern bool EnumChildWindows(IntPtr window, EnumWindowProc callback, IntPtr i);

        [DllImport("user32", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private extern static bool EnumThreadWindows(int threadId, EnumWindowProc callback, IntPtr lParam);

        [DllImport("user32.dll")]
        static extern IntPtr SendMessage(IntPtr hWnd, uint Msg, long wParam, [MarshalAs(UnmanagedType.LPStr)] StringBuilder lParam);

        [DllImport("user32.dll", CharSet = CharSet.Auto, ExactSpelling = true)]
        public static extern IntPtr GetParent(IntPtr hWnd);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern int GetClassName(IntPtr hWnd, StringBuilder lpClassName, int nMaxCount);

        [DllImport("user32.dll")]
        static extern IntPtr FindWindow(string windowClass, string windowName);

        [DllImport("user32.dll")]
        static extern bool SetWindowText(IntPtr hWnd, string text);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        internal static extern int GetWindowText ( IntPtr hWnd, [Out] StringBuilder lpString, int nMaxCount );

        [DllImport("USER32.DLL")]
        public static extern bool SetForegroundWindow(IntPtr hWnd);

        private const int SW_HIDE = 0;
        private const int SW_SHOWNORMAL = 1;
        private const int SW_SHOWMAXIMIZED = 3;
        private const int SW_RESTORE = 9;
        private const uint WM_SETTEXT = 0x000C;
        private const uint WM_LBUTTONDOWN = 0x201;
        private const uint WM_LBUTTONUP = 0x202;

        [DllImport("user32.dll")]
        private static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern IntPtr SendMessage(HandleRef hWnd, uint Msg, IntPtr wParam, string lParam);

        [DllImport("user32")]
        private extern static int ShowWindow(int hWnd, int nCmdShow);


        public static IntPtr FindWindowInProcess(Process process, Func<string, bool> compareTitle)
        {
            IntPtr windowHandle = IntPtr.Zero;

            foreach (ProcessThread t in process.Threads)
            {
                windowHandle = FindWindowInThread(t.Id, compareTitle);
                if (windowHandle != IntPtr.Zero)
                {
                    break;
                }
            }

            return windowHandle;
        }

        private static IntPtr FindWindowInThread(int threadId, Func<string, bool> compareTitle)
        {
            IntPtr windowHandle = IntPtr.Zero;
            EnumThreadWindows(threadId, (hWnd, lParam) =>
            {
                StringBuilder text = new StringBuilder(200);
                GetWindowText(hWnd, text, 200);
                if (compareTitle(text.ToString()))
                {
                    windowHandle = hWnd;
                    return false;
                }
                else
                {
                    windowHandle = FindChildWindow(hWnd, compareTitle);
                    if (windowHandle != IntPtr.Zero)
                    {
                        return false;
                    }
                }
                return true;
            }, IntPtr.Zero);

            return windowHandle;
        }

        private static IntPtr FindChildWindow(IntPtr hWnd, Func<string, bool> compareTitle)
        {
            IntPtr windowHandle = IntPtr.Zero;
            EnumChildWindows(hWnd, (hChildWnd, lParam) =>
            {
                StringBuilder text = new StringBuilder(200);
                GetWindowText(hChildWnd, text, 200);
                if (compareTitle(text.ToString()))
                {
                    windowHandle = hChildWnd;
                    return false;
                }
                return true;
            }, IntPtr.Zero);

            return windowHandle;
        }


        public static void ForceShowWindow(IntPtr hWnd)
        {
            ShowWindow(hWnd, SW_RESTORE | SW_SHOWNORMAL);
            SetForegroundWindow(hWnd);
        }

        public static void HideWindow(IntPtr hWnd)
        {
            ShowWindow(hWnd, SW_HIDE);
        }

        public static void FillTextField(IntPtr hWnd, string text)
        {
            HandleRef hrefHWndTarget = new HandleRef(null, hWnd);
            SendMessage(hrefHWndTarget, WM_SETTEXT, IntPtr.Zero, text);
        }

        public static void Click(IntPtr hWnd)
        {
            HandleRef hrefHWndTarget = new HandleRef(null, hWnd);
            SendMessage(hrefHWndTarget, WM_LBUTTONDOWN, IntPtr.Zero, null);
            //Thread.Sleep(100);
            SendMessage(hrefHWndTarget, WM_LBUTTONUP, IntPtr.Zero, null);
        }

        public static string GetWindowText(IntPtr hWnd)
        {
            StringBuilder stringBuilder = new StringBuilder(256);

            GetWindowText(hWnd, stringBuilder, stringBuilder.Capacity);
            return stringBuilder.ToString();
        }

        /// <summary>
        /// Returns a list of child windows
        /// </summary>
        /// <param name="parent">Parent of the windows to return</param>
        /// <returns>List of child windows</returns>
        public static HanldesInfo GetChildWindows(IntPtr parent, bool onlyImmediateChilds, String className, String text)
        {
            HanldesInfo result = new HanldesInfo(parent, onlyImmediateChilds, className, text);
            GCHandle listHandle = GCHandle.Alloc(result);
            try
            {
                EnumChildWindows(parent, WindowUtil.EnumWindowAllChildCallBackMethod, GCHandle.ToIntPtr(listHandle));
            }
            finally
            {
                if (listHandle.IsAllocated) { listHandle.Free(); }
            }
            return result;
        }

        /// <summary>
        /// Callback method to be used when enumerating windows.
        /// </summary>
        /// <param name="handle">Handle of the next window</param>
        /// <param name="pointer">Pointer to a GCHandle that holds a reference to the list to fill</param>
        /// <returns>True to continue the enumeration, false to fail</returns>
        private static bool EnumWindowAllChildCallBackMethod(IntPtr handle, IntPtr pointer)
        {
            GCHandle gch = GCHandle.FromIntPtr(pointer);
            HanldesInfo list = gch.Target as HanldesInfo;

            if (list == null) { throw new InvalidCastException("GCHandle Target could not be cast as List<IntPtr>"); }

            if (list.OnlyImmediateChilds && list.ParentHandle != WindowUtil.GetParent(handle)) return true;

            StringBuilder className = new StringBuilder(100);
            WindowUtil.GetClassName(handle, className, className.Capacity);

            if (list.ClassName.Length > 0)
            {
                if (String.Compare(className.ToString().Trim(), list.ClassName, true) != 0) return true;
            }
            string text = GetWindowText(handle);
            list.ChildWindows.Add(new WindowInfo { handle = handle, className = className.ToString(), text = text });

            //  if you want to cancel the operation, then return a null here
            return true;
        }

        public static void BringToFront(IntPtr handle)
        {
            SetForegroundWindow(handle);
        }
    }

    public class WindowInfo
    {
        public IntPtr handle {get;set;}
        public string className { get; set; }
        public string text { get; set; }
    }

    public class HanldesInfo
    {
        public IntPtr ParentHandle { get; private set; }
        public bool OnlyImmediateChilds { get; private set; }
        public String ClassName { get; private set; }
        public String Text { get; private set; }
        public List<WindowInfo> ChildWindows { get; private set; }

        internal HanldesInfo(IntPtr parentHandle, bool onlyImmediateChilds) : this(parentHandle, onlyImmediateChilds, String.Empty, String.Empty) { }
        internal HanldesInfo(IntPtr parentHandle, bool onlyImmediateChilds, String className) : this(parentHandle, onlyImmediateChilds, String.Empty, String.Empty) { }
        internal HanldesInfo(IntPtr parentHandle, bool onlyImmediateChilds, String className, String text)
        {
            this.ParentHandle = parentHandle;
            this.OnlyImmediateChilds = onlyImmediateChilds;
            this.ClassName = (className ?? String.Empty).Trim();
            this.Text = (text ?? String.Empty).Trim();
            this.ChildWindows = new List<WindowInfo>();
        }

        public IntPtr FindWindow(string className, string text)
        {
            foreach (WindowInfo wi in ChildWindows)
            {
                if (wi.className.Equals(className))
                {
                    if (text == null)
                        return wi.handle;

                    if (text.Equals(wi.text))
                        return wi.handle;
                }
            }
            return IntPtr.Zero;
        }

        public IntPtr FindWindow(string className)
        {
            return FindWindow(className, null);
        }
    }
}
