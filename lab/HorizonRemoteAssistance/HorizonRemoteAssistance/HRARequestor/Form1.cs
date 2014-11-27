using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net;
using System.Runtime.InteropServices;
using ETEUtils;

namespace HRARequestor
{
    public partial class Form1 : Form
    {


        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
        }


        private void button2_Click(object sender, EventArgs e)
        {
            using (var wb = new WebClient())
            {
                string text = HraInvitation.create();
                string address = "http://10.112.119.165:8080/admin_ex/hra";
                try
                {
                    wb.QueryString.Add("inv", text);
                    string ret = wb.DownloadString(address);
                    MessageBox.Show("Remote Assistant request sent successfully. " + ret);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("An error occured posting remote assistance data. Details:\n" + ex.ToString());
                }
            }
        }


        private void button3_Click(object sender, EventArgs e)
        {
            //string s = RegUtil.ReadLocalMachine(@"SOFTWARE\VMware, Inc.\VMware Drivers", "vsockDll.status");
            string s = RegUtil.ReadLocalMachine(@"SOFTWARE\VMware, Inc.\VMware VDM\Agent\Configuration", "Broker");
            MessageBox.Show(s);
        }

        private void button4_Click(object sender, EventArgs e)
        {
        }

        private void button5_Click(object sender, EventArgs e)
        {
            HttpUtil._IgnoreSSL();

            using (var wb = new WebClient())
            {

                //string address = "http://10.112.119.165:8080/admin_ex/hra";
                string address = "https://10.117.160.101/admin_ex/hra";
                try
                {
                    //we should post, however View Connection server has disabled POST.
                    wb.Headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

                    //wb.QueryString.Add("inv", text);

                    string ret = wb.DownloadString(address);
                    MessageBox.Show(ret);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Error occured: " + ex + ". AdminEx Addr=" + address);
                    return;
                }
            }
        }



    }
}
