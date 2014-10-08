using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Net;
using System.Diagnostics;
using System.Threading;

namespace HRAInstaller
{
    public partial class Form2 : Form
    {

        static BackgroundWorker backgroundWorker;


        public Form2()
        {
            InitializeComponent();
            CenterToScreen();

        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (checkBox1.Checked)
            {
                //Launch AdminEx
                Process.Start(Config.AdminExUrl);
            }

            Close();
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            if (Config.installType == Config.INSTALL_FOR_ADMIN)
            {
                checkBox1.Visible = true;
                checkBox1.Checked = true;
            }
            else
            {
                checkBox1.Visible = false;
                checkBox1.Checked = false;
            }
         
            this.FormClosing += new FormClosingEventHandler(Form2_FormClosing);

            backgroundWorker = new BackgroundWorker();
            backgroundWorker.DoWork += new DoWorkEventHandler(backgroundWorker_DoWork);
            backgroundWorker.ProgressChanged += new ProgressChangedEventHandler(backgroundWorker_ProgressChanged);
            backgroundWorker.RunWorkerCompleted += new RunWorkerCompletedEventHandler(backgroundWorker_RunWorkerCompleted);
            backgroundWorker.WorkerReportsProgress = true;
            backgroundWorker.WorkerSupportsCancellation = true;
            backgroundWorker.RunWorkerAsync();
        }

        void Form2_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (backgroundWorker != null)
            {
                try
                {
                    backgroundWorker.CancelAsync();
                }
                catch (Exception)
                {
                }
                backgroundWorker = null;
            }
        }

        void backgroundWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            button1.Enabled = true;
        }

        void backgroundWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            BackgroundWorker worker = (BackgroundWorker)sender;

            Installer.Init(msg);

            while (true)
            {
                if ((worker.CancellationPending == true))
                {
                    e.Cancel = true;
                    break;
                }
                else
                {
                    if (!Installer.RunNextStep())
                        break;
                }
            }

            msg("");

            string completionMsg;
            if (e.Cancel)
            {
                completionMsg = "Canceled.";
            }
            else if (Installer.HasError())
            {
                completionMsg = "Failed.";
            }
            else
            {
                completionMsg = "Complete.";
            }
            msg(completionMsg);
        }

        private static void msg(string s)
        {
            backgroundWorker.ReportProgress(0, s);
        }

        void backgroundWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            string msg = (string)e.UserState;
            msgGuiThread(msg);
        }

        private void msgGuiThread(string s)
        {
            string content = textBox1.Text;
            content += s + "\r\n";
            this.textBox1.Text = content;
            int len = content.Length;
            textBox1.Select(len, 0);
        }


        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void button2_Click(object sender, EventArgs e)
        {
        }
    }
}
