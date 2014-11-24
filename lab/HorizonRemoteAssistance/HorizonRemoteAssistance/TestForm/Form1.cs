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
            string sysDir = Environment.GetFolderPath(Environment.SpecialFolder.System);
            if (!sysDir.EndsWith("\\"))
                sysDir += "\\";
            string msraPath = sysDir + "msra.exe";
            MessageBox.Show(sysDir);
        }

        private void button2_Click(object sender, EventArgs e)
        {
        }

    }
}
