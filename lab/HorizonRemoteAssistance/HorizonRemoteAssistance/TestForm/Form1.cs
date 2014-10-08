using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Diagnostics;
using System.Threading;
using System.Runtime.InteropServices;

namespace TestForm
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {
            
        }

        private void button1_Click(object sender, EventArgs e)
        {

        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            MessageBox.Show(textBox1.Text);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Process proc = new Process();
            proc.StartInfo.FileName = @"msra.exe";
            proc.StartInfo.Arguments = "/openfile c:\\1.a";
            proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

            proc.Start();

            IntPtr windowHandle;
            while (true)
            {
                Thread.Sleep(100);

                proc.Refresh();
                //windowHandle = proc.MainWindowHandle;

                windowHandle = WindowUtil.FindWindowInProcess(proc, s => s.Equals("Remote Assistance"));
                if (!IntPtr.Zero.Equals(windowHandle))
                    break;
            }

            WriteTextUsingHandle(windowHandle, "******");
        }


        public static bool WriteTextUsingHandle(IntPtr hWnd, String text)
        {
            WindowUtil.HideWindow(hWnd);

            HanldesInfo windowInfo = WindowUtil.GetChildWindows(hWnd, false, "", String.Empty);

            //fill text
            IntPtr h = windowInfo.FindWindow("Edit");
            if (h == IntPtr.Zero)
                return false;

            WindowUtil.FillTextField(h, text);

            Thread.Sleep(1000);

            //click button
            h = windowInfo.FindWindow("Button", "OK");
            if (h == IntPtr.Zero)
                h = windowInfo.FindWindow("Button");

            if (h == IntPtr.Zero)
                return false;

            WindowUtil.Click(h);

            WindowUtil.ForceShowWindow(hWnd);

            return true;
        }

        private void button3_Click(object sender, EventArgs e)
        {
        }

    }
}
