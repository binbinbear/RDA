using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Diagnostics;

namespace HRAInstaller
{
    public partial class FormMain : Form
    {
        public FormMain()
        {
            InitializeComponent();
            CenterToScreen();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (!ViewUtil.IsViewAgentInstalled())
            {
                DialogResult ret = MessageBox.Show(this, "View Agent installation not found. Horizon Remote Assistance could not work without View Agent. Are you sure to continue?", "Horizon Remote Assistance Installer", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
                if (ret != DialogResult.Yes)
                    return;
            }

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
                //MessageBox.Show("This is development build and is not published. \r\nContact EUC Technical Enablement team for the formal build.", "Horizon Remote Assistance", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            };

            //bool isOnServer = ViewUtil.IsViewConnectionServerInstalled();
            //bool isOnServer = true;

            //bool isOnViewDesktop = ViewUtil.IsViewAgentInstalled();

            //btn_install.Enabled = isOnViewDesktop;
            //textBox2.Enabled = isOnViewDesktop;
            
            //btn_installAdmin.Enabled = true;    // isOnServer;
            //textBox3.Enabled = true;    // isOnServer;


            //if (!isOnServer && !isOnViewDesktop)
            //{
            //    MessageBox.Show("It seems the current system is neither a View connection server nor a View desktop (View agent not found). This tool must be run on either of them.");
            //}
        }

        private void btn_installAdmin_Click(object sender, EventArgs e)
        {
            doInstall(Config.INSTALL_FOR_ADMIN);
        }

        private void doInstall(int type)
        {
            this.Visible = false;
            FormSteps frm = new FormSteps();
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

        private void textBox2_TextChanged(object sender, EventArgs e)
        {

        }
    }
}
