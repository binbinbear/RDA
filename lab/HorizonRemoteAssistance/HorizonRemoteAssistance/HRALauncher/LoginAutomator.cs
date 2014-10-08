using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Diagnostics;

namespace HRALauncher
{
    class LoginAutomator
    {

        internal static bool FillPasswordAndProceed(Process proc)
        {
            Logger.Log("Locating login...");


            IntPtr loginDlgHandle = IntPtr.Zero;
            for (int i = 0; i < 10 * 60; i++)   //60 seconds
            {
                Thread.Sleep(100);

                proc.Refresh();
                if (proc.MainWindowHandle == IntPtr.Zero)
                    continue;

                loginDlgHandle = FindLoginDlg(proc);

                if (loginDlgHandle != IntPtr.Zero)
                    break;
            }

            if (loginDlgHandle == IntPtr.Zero)
                return false;

            Logger.Log("Hide login");
            WindowUtil.HideWindow(loginDlgHandle);

            HandlesInfo windowInfo = WindowUtil.GetChildWindows(loginDlgHandle, false, "", String.Empty);

            for (int i = 0; i < 50; i++)    //retry 5 seconds
            {
                Logger.Log("Locating edit");
                //fill text
                IntPtr h = windowInfo.FindWindow("Edit");
                if (h == IntPtr.Zero)
                    return false;

                Logger.Log("Fill edit");
                WindowUtil.FillTextField(h, "******");

                Logger.Log("Locating confirm");
                //click button
                h = windowInfo.FindWindow("Button", "OK");
                if (h == IntPtr.Zero)
                    h = windowInfo.FindWindow("Button");

                if (h == IntPtr.Zero)
                    return false;

                Logger.Log("Confirming");
                WindowUtil.Click(h);

                Thread.Sleep(100);
            }

            Logger.Log("Force show");
            WindowUtil.ForceShowWindow(loginDlgHandle);
            return true;
        }


        public static IntPtr FindLoginDlg(Process process)
        {
            foreach (ProcessThread t in process.Threads)
            {
                Console.WriteLine("Thread: " + t.Id);
                IntPtr windowHandle = FindWindowInThread(t.Id);
                if (windowHandle != IntPtr.Zero)
                {
                    return windowHandle;
                }
            }

            return IntPtr.Zero;
        }

        private static IntPtr FindWindowInThread(int threadId)
        {
            IntPtr windowHandle = IntPtr.Zero;
            WindowUtil.EnumThreadWindows(threadId, (hWnd, lParam) =>
            {
                //skip the main window
                //if (hwndMain.Equals(hWnd))
                //    return true;

                //skip all non-dialog window
                StringBuilder text = new StringBuilder(200);
                WindowUtil.GetClassName(hWnd, text, 200);
                string className = text.ToString();

                string windowText = WindowUtil.GetWindowText(hWnd);

                className.ToString();
                windowText.ToString();

                if (!className.Equals("#32770"))
                    return true;

                Console.WriteLine("  Window, class=" + className + ", text=" + windowText);
                //this is a dialog. Check whether it's the login window we are going to find

                HandlesInfo children = WindowUtil.GetChildWindows(hWnd, false, "", "");

                int buttonCnt = 0;
                int editCnt = 0;
                foreach (WindowInfo wi in children.ChildWindows)
                {
                    Console.WriteLine("    Child, class=" + wi.className + ", text=" + wi.text);
                    if (wi.className.Equals("Edit"))
                        editCnt++;
                    else if (wi.className.Equals("Button"))
                        buttonCnt++;
                }

                if (editCnt == 1 && buttonCnt == 2)
                {
                    windowHandle = hWnd;
                    return false;
                }
                return true;
            }, IntPtr.Zero);

            return windowHandle;
        }
    }
}
