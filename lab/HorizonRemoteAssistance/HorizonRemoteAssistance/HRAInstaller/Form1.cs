using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Diagnostics;

namespace HRAInstaller
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            CenterToScreen();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            doInstall(Config.INSTALL_FOR_END_USER);
        }

        private void btn_uninstall_Click(object sender, EventArgs e)
        {
            doInstall(Config.UNINSTALL);
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            Shown += delegate(object o, EventArgs ea)
            {
                MessageBox.Show("This is development build and is not published. \r\nContact EUC Technical Enablement team for the formal build.", "Horizon Remote Assistance", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            };

            bool isOnServer = ViewUtil.IsViewConnectionServerInstalled();
            bool isOnViewDesktop = ViewUtil.IsViewAgentInstalled();

            btn_install.Enabled = isOnViewDesktop;
            textBox2.Enabled = isOnViewDesktop;

            btn_installAdmin.Enabled = isOnServer;
            textBox3.Enabled = isOnServer;


            if (!isOnServer && !isOnViewDesktop)
            {
                MessageBox.Show("It seems the current system is neither a View connection server nor a View desktop (View agent not found). This tool must be run on either of them.");
            }
        }

        private void btn_installAdmin_Click(object sender, EventArgs e)
        {
            doInstall(Config.INSTALL_FOR_ADMIN);
        }

        private void doInstall(int type)
        {
            this.Visible = false;
            Form2 frm = new Form2();
            Config.installType = type;
            frm.ShowDialog();
            this.Close();
        }

        private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Process.Start("http://labs.vmware.com/flings/horizon-remote-assistance");
        }

        private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Process.Start("https://wiki.eng.vmware.com/EUC_Technical_Enablement/Development/RemoteAssist");
        }
    }
}
