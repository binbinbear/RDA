using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using ICSharpCode.SharpZipLib;
using ICSharpCode.SharpZipLib.Zip;
using ICSharpCode.SharpZipLib.Checksums;
using System.Diagnostics;
using System.Security.Permissions;
using Microsoft.Win32;
using System.IO.Compression;
using System.ServiceProcess;
using System.Management;
using System.Threading;
using System.Net;

namespace ToolboxInstall
{
    public partial class Form1 : Form
    {
        public const int waitTime = 40000;
        //status of tomcat
        public const int None = 0;
        public const int Stopped = 1;
        public const int Running = 2;
        public const string ServiceName = "Tomcat8";
        public int rate = 0;
        public bool configureRAFireWall = true;

        public Form1(bool bAutoConfigRA)
        {
            InitializeComponent();
            configureRAFireWall = bAutoConfigRA;
            timer1.Enabled = true;
            progressBar1.Value = 0;
            label1.Text = progressBar1.Value + "%";
            progressBar1.Minimum = 0;
            progressBar1.Maximum = 100;
            output("Start configuring toolbox...");
            //ConfigureToolBox();
            Thread td = new Thread(new ThreadStart(ConfigureToolBox));
            td.IsBackground = true;
            td.Start();

        }


        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void progressBar1_Click(object sender, EventArgs e)
        {

        }


        public void output(string log)
        {
            if (richTextBox1.GetLineFromCharIndex(richTextBox1.Text.Length) > 100)
                richTextBox1.Text = "";
            richTextBox1.AppendText(DateTime.Now.ToString("HH:m:ss  ") + log + "\r\n");
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="str"></param>
        private void UpdateprogressBar1(int num)
        {
            if (progressBar1.InvokeRequired)
            {
                Action<int> actionDelegate = (x) => { this.progressBar1.Value = num; };
                this.progressBar1.Invoke(actionDelegate, num);
            }
            else
            {
                this.progressBar1.Value = num;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="str"></param>
        private void UpdaterichTextBox1(object str)
        {
            if (richTextBox1.InvokeRequired)
            {
                Action<string> actionDelegate = (x) => { this.richTextBox1.AppendText(DateTime.Now.ToString("HH:m:ss  ") + x.ToString() + '\n'); };
                this.richTextBox1.Invoke(actionDelegate, str);
            }
            else
            {
                this.richTextBox1.AppendText(DateTime.Now.ToString("HH:m:ss  ") + str.ToString() + '\n');
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="str"></param>
        private void Updatelabel1(object str)
        {
            if (label1.InvokeRequired)
            {
                Action<string> actionDelegate = (x) => { this.label1.Text = str.ToString(); };
                this.label1.Invoke(actionDelegate, str);
            }
            else
            {
                this.label1.Text = str.ToString();
            }
        }




        /// <summary>
        /// select window
        /// </summary>
        /// <param name="msg"></param>
        /// <returns></returns>
        public bool ShowQuestionYesNo(string msg)
        {
            DialogResult dr = MessageBox.Show(msg, "Hit", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (dr == DialogResult.Yes)
            {
                return true;
            }
            else
            {
                return false;
            }
        }


        /// <summary>
        /// procedure to install the toolbox
        /// </summary>
        public void ConfigureToolBox()
        {
            // Check Java home
            bool bNeedRegularJre = false;
            if (ToolboxEnvCheck.NeedVerifyJavaVersion())
            {
                bNeedRegularJre = true;
                if (ToolboxEnvCheck.VerifyJavaVersion() == false)
                {
                    String errorInfo = @"Error: Cannot find the matched Java Version, please install JRE8(x64 server version) or later. Otherwise, upgrade Horizon to verion 6.2 or later.";
                    ExceptionOccur(errorInfo);

                    MessageBox.Show(errorInfo);
                    Application.Exit();
                    return;
                }
            }

            //Application.DoEvents();
            //set JRE_HOME

            String processToKill = "tomcat8.exe";
            UpdaterichTextBox1("Checking if Horizon View Connection Server has been installed on this computer...");
            //output("Checking if connection server has been installed on this computer...");
            string jre_path = string.Empty;

            if (bNeedRegularJre)
            {
                jre_path = ToolboxEnvCheck.GetRegularJrePath();
                UpdaterichTextBox1("regular jre " + jre_path);
            }

            if (bNeedRegularJre && (jre_path != null))
            {
                ;
            }
            else
            {
                try
                {
                    jre_path = Registry.GetValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\wsnm.exe", "Path", null).ToString();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.ToString());
                }

                if (string.Compare(jre_path, string.Empty) == 0 || jre_path.IndexOf("Server") == -1)
                {
                    String errorInfo = @"JRE path is invalid. Please install Horizon Toolbox on a Horizon Connection Server!";
                    ExceptionOccur(errorInfo);
                    MessageBox.Show(@"Error: Please install Horizon Toolbox on a Horizon Connection Server!");
                    return;
                }
                UpdaterichTextBox1("Connection server is found on this computer.");
                UpdaterichTextBox1("Start to set environment variables...");

                //get rid of the last "\"
                if (jre_path[jre_path.Length - 1] == '\\')
                {
                    jre_path = jre_path.Substring(0, jre_path.Length - 1);
                }
                jre_path = Directory.GetParent(jre_path) + @"\jre\";
                string oriJrePath = string.Empty;
                try
                {
                    oriJrePath = Registry.GetValue(@"HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment", "JRE_HOME", null).ToString();

                }
                catch
                {
                }
                string overlay = "YES";
                //timer1.Start();
                if (oriJrePath.CompareTo(string.Empty) != 0 && oriJrePath.CompareTo(jre_path) != 0)
                {
                    timer1.Enabled = false;
                    string msg = @"This computer already has the ""JRE_HOME"", would you like to override it with the value: " + jre_path + "?";
                    bool ans = ShowQuestionYesNo(msg);
                    this.Invoke(
                             (MethodInvoker)delegate
                             {
                                 timer1.Start();
                             });
                    if (ans)
                    {
                        overlay = "YES";
                    }
                    else
                    {
                        overlay = "NO";
                    }

                }
                if (overlay.CompareTo("YES") == 0)
                {
                    //Registry.SetValue(@"HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment", "JRE_HOME", jre_path);
                    Environment.SetEnvironmentVariable(@"JRE_HOME", jre_path, EnvironmentVariableTarget.Machine);
                }
                else
                {
                    jre_path = oriJrePath;
                }

                UpdaterichTextBox1("custom jre " + jre_path);
            }

            UpdaterichTextBox1("Environment variable has been successfully set.");
            UpdaterichTextBox1("Start to remove useless files...");

            //check the install
            string path = string.Empty;
            try
            {
                path = Registry.GetValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\HorizonToolbox.exe", "path", null).ToString();
            }
            catch
            {
                String errorInfo = @"Fail installing the HorizonToolBox, please reinstall it!";
                ExceptionOccur(errorInfo);
               // Console.WriteLine(@"Fail installing the HorizonToolBox, please reinstall it!");
              //  Console.Write("Please press 'Enter' key to continue ... ");
               // Console.ReadLine();
                return;
            }


            Process p1 = new Process();
            //unload last version tomcat
            using (p1)
            {
                if (SvrStatus(ServiceName) != None)
                {
                    string svrPath = GetSvrPath(ServiceName);
                    if (svrPath.CompareTo(string.Empty) != 0)
                    {
                        UpdaterichTextBox1("Looking for Tomcat...");
                        p1.StartInfo.FileName = "cmd.exe";
                        p1.StartInfo.UseShellExecute = false;
                        p1.StartInfo.RedirectStandardInput = true;
                        p1.StartInfo.RedirectStandardOutput = true;
                        p1.StartInfo.RedirectStandardError = true;
                        p1.StartInfo.CreateNoWindow = true;
                        p1.Start();
                        p1.StandardInput.WriteLine(@"cd " + svrPath.Substring(0, svrPath.IndexOf(@"\")));
                        p1.StandardInput.WriteLine(svrPath.Substring(0, svrPath.IndexOf(@"\")));
                       // p1.StandardInput.WriteLine(@"ping www.baidu.com");
                        p1.StandardInput.WriteLine(@"cd " + svrPath);
                        p1.StandardInput.WriteLine(@"set JRE_HOME=" + jre_path);
                        p1.StandardInput.WriteLine(@"set PROCESSKILL=" + processToKill);
                        p1.StandardInput.WriteLine(@"TASKKILL /F /IM %PROCESSKILL%");
                        p1.StandardInput.WriteLine(@"service.bat remove");
                        p1.StandardInput.WriteLine(@"cd " + svrPath.Substring(0, svrPath.IndexOf(@"\") + 1));
                        p1.StandardInput.WriteLine(@"exit");
                        p1.WaitForExit(waitTime);
                    }
                }
            }
            //p1.Close();
            if (SvrStatus(ServiceName) != None)
            {
                String errorInfo = @"Fail removing the Tomcat, please uninstall it manually, and rerun this program!";
                ExceptionOccur(errorInfo);
                MessageBox.Show(errorInfo);
                //Console.ReadLine();
                return;
            }
            UpdaterichTextBox1("The old files have been removed.");
            UpdaterichTextBox1("Get the latest files...");
            Thread.Sleep(1000);
            //unzip the latest HorizonToolbox
            string direcOfToolbox = GetToolBoxPath(path, false);
            string pathOfZip = GetToolBoxPath(path, true);
            if (direcOfToolbox.CompareTo(pathOfZip.Substring(0, pathOfZip.LastIndexOf(@"."))) != 0)
            {
                bool isDelete = true;
                //Delete last version
                if (Directory.Exists(direcOfToolbox))
                {
                    try
                    {
                        Directory.Delete(direcOfToolbox, true);
                    }
                    catch (Exception e)
                    {
                       // Console.WriteLine(e.ToString());
                        isDelete = false;
                    }
                }
                //If the file or folder is processed, then it cannot be deleted
                if (!isDelete)
                {
                    String errorInfo = @"The files of HorizonToolBox are being used by some process, please stop Toolbox and run this program again!";
                    ExceptionOccur(errorInfo);
                    MessageBox.Show(errorInfo);
                   // Console.WriteLine(@"The file or files of HorizonToolBox is occupied by some process, please release it and run this program again!");
                  //  Console.Write("Please press 'Enter' key to continue ... ");
                    //Console.ReadLine();
                    return;
                }

                bool sucUnZip = UnZip(pathOfZip, path);
                if (sucUnZip == false)
                {
                    String errorInfo = @"Fail unzipping the file, please unzip the file : " + pathOfZip + @"  manually and run this program again!";
                    ExceptionOccur(errorInfo);
                    MessageBox.Show(errorInfo);
                    //Console.WriteLine(@"Fail to unzip the file, please unzip the file : " + pathOfZip + @"  by manual and run this program again!");
                    //Console.Write("Please press 'Enter' key to continue ... ");
                    //Console.ReadLine();
                    return;
                }
                //delete zipFile
                //File.Delete(pathOfZip);


                
            }
            UpdaterichTextBox1("Succeed getting files.");
            Thread.Sleep(3000);

            //cmd,  install tomcat
            //path = Registry.GetValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\HorizonToolbox.exe", "path", null).ToString();
            Process p = new Process();
            using (p)
            {
                UpdaterichTextBox1("Start to configure the latest evironment...");
                p.StartInfo.FileName = "cmd.exe";
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardInput = true;
                p.StartInfo.RedirectStandardOutput = true;
                p.StartInfo.RedirectStandardError = true;
                p.StartInfo.CreateNoWindow = true;
                p.Start();
                string zip = @".zip";
                pathOfZip = pathOfZip.Substring(0, pathOfZip.Length - zip.Length);
                p.StandardInput.WriteLine(@"cd " + pathOfZip.Substring(0, pathOfZip.IndexOf(@"\")));
                p.StandardInput.WriteLine(pathOfZip.Substring(0, pathOfZip.IndexOf(@"\")));
                p.StandardInput.WriteLine(@"cd " + pathOfZip + @"\bin");
                UpdaterichTextBox1(@"cd " + pathOfZip + @"\bin");
                p.StandardInput.WriteLine(@"set JRE_HOME=" + jre_path);
                p.StandardInput.WriteLine(@"service.bat install");
                p.StandardInput.WriteLine(@"exit");
                p.WaitForExit(waitTime);
            }
            UpdaterichTextBox1("Succeed setting the environment.");

            Thread.Sleep(3000);
            if (SvrStatus(ServiceName) == None)
            {
                String errorInfo = @"Fail installing Tomcat. Please restart this program again or install the tomcat manually.";
                ExceptionOccur(errorInfo);
                MessageBox.Show(errorInfo);
                return;
            }

            if (SvrStatus(ServiceName) != Running)
            {
                UpdaterichTextBox1("Starting service...");
                StartService(ServiceName);
            }

            //Create uninstall.bat to remove Tomcat when uninstall HorizonToolbox
            string tomcatPath = GetToolBoxPath(path, true);
            string tmp = tomcatPath;
            tomcatPath = tomcatPath.Substring(0, tomcatPath.LastIndexOf(".zip"));
            string shortCutPath = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory) + @"\HorzionToolbox.lnk";
            string[] content = { "cd " + tomcatPath + @"\bin", @"set PROCESSKILL=" + processToKill, @"TASKKILL /F /IM %PROCESSKILL%", "call service.bat remove", "cd ..", "cd ..", @"rd/s/q .\" + tomcatPath.Substring(tomcatPath.LastIndexOf(@"\") + 1), @"del /f /q " + shortCutPath };
            File.WriteAllLines(path + "uninstall.bat", content);
            File.Delete(tmp);

            // enable remote assistance
            EnableWindowsRAFeature();

            if (configureRAFireWall)
            {
                ConfigFirewallRuleForRemoteAssistance();
            }
            else
            {
                UpdaterichTextBox1("skip to configure firewall for ra.");
            }

            UpdaterichTextBox1("Completed...");
            Updatelabel1("100%");
            UpdateprogressBar1(progressBar1.Maximum);

            UpdaterichTextBox1(@"Congratulations! Your Horizon Toolbox has been installed correctly!" );
            UpdaterichTextBox1("You can close this program now.");


            //Create Shortcut to tomcat8w.exe
            if (SvrStatus(ServiceName) != None)
            {
                IWshRuntimeLibrary.WshShell shell = new IWshRuntimeLibrary.WshShellClass();
                string dir = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory);
                IWshRuntimeLibrary.IWshShortcut shortcut = (IWshRuntimeLibrary.IWshShortcut)shell.CreateShortcut(dir + @"\HorzionToolbox.lnk");
                shortcut.TargetPath = tomcatPath + @"\bin\tomcat8w.exe";
                shortcut.IconLocation = tomcatPath.Substring(0, tomcatPath.LastIndexOf("HorizonToolbox")) + @"Images\toolbox.ico";
                shortcut.Save();
            }

        }



        /// <summary>
        /// get the path of the service
        /// </summary>
        /// <param name="svrName"></param>
        /// <returns>the path where the service is</returns>
        private string GetSvrPath(string svrName)
        {
            string svrPath = string.Empty;
            try
            {
                svrPath = Registry.GetValue("HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\services\\Tomcat8", "ImagePath", null).ToString();
            }
            catch
            {
                return svrPath;
            }
            svrPath = svrPath.Substring(svrPath.IndexOf(@"""") + 1, svrPath.LastIndexOf("tomcat8.exe") - 1);
            return svrPath;
        }



        /// <summary>
        /// get the path of the file or folder
        /// </summary>
        /// <param name="path">Installationdirectory</param>
        /// <param name="zipFile">true: get HorizonToolbox.zip; false : get HorizonToolbox folder, then delete it</param>
        /// <returns></returns>
        private string GetToolBoxPath(string path, bool zipFile)
        {
            string[] names;
            if (zipFile)
            {
                names = Directory.GetFiles(path);
            }
            else
            {
                names = Directory.GetDirectories(path);
            }
            for (int i = 0; i < names.Length; i++)
            {
                if (!zipFile)
                {
                    int startIndex = names[i].LastIndexOf(@"\");
                    if (names[i].Substring(startIndex + 1).StartsWith("HorizonToolbox"))
                    {
                        return names[i];
                    }
                }
                else
                {
                    if (names[i].EndsWith(".zip"))
                    {
                        return names[i];
                    }
                }
            }
            return string.Empty;
        }



        #region unzip

        /// <summary>   
        /// unzip the file to the prescribed route  
        /// </summary>   
        /// <param name="fileToUnZip">zip file</param>   
        /// <param name="zipedFolder">prescribed route</param>   
        /// <param name="password">password</param>   
        /// <returns>decompression results</returns>   
        public bool UnZip(string fileToUnZip, string zipedFolder, string password)
        {
            bool result = true;
            FileStream fs = null;
            ZipInputStream zipStream = null;
            ZipEntry ent = null;
            string fileName;

            if (!File.Exists(fileToUnZip))
                return false;

            if (!Directory.Exists(zipedFolder))
                Directory.CreateDirectory(zipedFolder);

            try
            {
                zipStream = new ZipInputStream(File.OpenRead(fileToUnZip));
                if (!string.IsNullOrEmpty(password)) zipStream.Password = password;
                while ((ent = zipStream.GetNextEntry()) != null)
                {
                    if (!string.IsNullOrEmpty(ent.Name))
                    {
                        fileName = Path.Combine(zipedFolder, ent.Name);
                        fileName = fileName.Replace('/', '\\');

                        if (fileName.EndsWith("\\"))
                        {
                            Directory.CreateDirectory(fileName);
                            continue;
                        }

                        fs = File.Create(fileName);
                        int size = 2048;
                        byte[] data = new byte[size];
                        while (true)
                        {
                            size = zipStream.Read(data, 0, data.Length);
                            if (size > 0)
                                fs.Write(data, 0, size);
                            else
                                break;
                        }
                    }
                }
            }
            catch
            {
                result = false;
            }
            finally
            {
                if (fs != null)
                {
                    fs.Flush();
                    fs.Close();
                    fs.Dispose();
                }
                if (zipStream != null)
                {
                    zipStream.Close();
                    zipStream.Dispose();
                }
                if (ent != null)
                {
                    ent = null;
                }
                GC.Collect();
                GC.Collect(1);
            }
            return result;
        }


        /// <summary>   
        /// unzip the file to the prescribed route  
        /// </summary>   
        /// <param name="fileToUnZip">zip file</param>   
        /// <param name="zipedFolder">prescribed route</param>   
        /// <returns>decompression results</returns>    
        public bool UnZip(string fileToUnZip, string zipedFolder)
        {
            bool result = UnZip(fileToUnZip, zipedFolder, null);
            return result;
        }

        #endregion


        /// <summary>
        /// check the status of the service
        /// </summary>
        /// <param name="name">ServiceName</param>
        /// <returns>noExist:no service; notRunning: exist but not running; running:running</returns>
        private int SvrStatus(string name)
        {
            int exist = None;
            ServiceController[] Services = ServiceController.GetServices();

            for (int i = 0; i < Services.Length; i++)
            {
                if (Services[i].ServiceName.ToString() == name)
                {
                    exist = Stopped;
                    if (Services[i].Status == ServiceControllerStatus.Running)
                    {
                        exist = Running;
                    }
                    break;
                }
            }
            return exist;
        }

        /// <summary>
        /// start service
        /// </summary>
        /// <param name="svrName"></param>
        private void StartService(string svrName)
        {
            ServiceController service = new ServiceController(svrName);
            //service.Refresh();
            using (service)
            {
                service.Start();
                var timeout = new TimeSpan(0, 0, 30);
                try
                {
                    service.WaitForStatus(ServiceControllerStatus.Running, timeout);
                }
                catch
                {
                    MessageBox.Show("Failed starting the service, please start Tomcat manually.");
                    //Console.WriteLine("Failed to start the service!");
                    //Console.Write("Please press 'Enter' key to continue ... ");
                    //Console.ReadLine();
                }
                if (service.Status == ServiceControllerStatus.Running)
                {
                    string ip = GetLocalIp();
                    string posturl = @"https://" + ip + @":18443/toolbox";
                    try
                    {
                        System.Diagnostics.Process.Start(posturl);
                    }
                    catch (Exception e)
                    {
                        String errorInfo = "Failed opening your browser, please access toolbox manually.";
                        ExceptionOccur(errorInfo);
                        MessageBox.Show(errorInfo);
                        //Console.WriteLine(e.ToString());
                       // Console.Write("Please press 'Enter' key to continue ... ");
                        //Console.ReadLine();
                    }
                }
            }

        }

        /// <summary>
        /// Get the IPv4
        /// </summary>
        /// <returns></returns>
        private string GetLocalIp()
        {
            string AddressIP = string.Empty;
            foreach (IPAddress _IPAddress in Dns.GetHostEntry(Dns.GetHostName()).AddressList)
            {
                if (_IPAddress.AddressFamily.ToString() == "InterNetwork")
                {
                    AddressIP = _IPAddress.ToString();
                }
            }
            return AddressIP;

        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="msg"></param>
        private void ExceptionOccur(String errorInfo)
        {
            timer1.Enabled = false;
            UpdateprogressBar1(0);
            Updatelabel1("0%");
            if (errorInfo.Length == 0)
            {
                UpdaterichTextBox1(@"Some error happened!");
            }
            else
                UpdaterichTextBox1(errorInfo);
        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (progressBar1.Value < progressBar1.Maximum)
            {
                progressBar1.Value++;
                label1.Text = progressBar1.Value + "%";
            }
            else
            {
                timer1.Enabled = false;
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void EnableWindowsRAFeature()
        {
            UpdaterichTextBox1(Environment.SystemDirectory);
            UpdaterichTextBox1("Start to enable remote assistance feature.");
            if (Util.IsFeatureInstalled("42"))  //42: Remote Assistance
            {
                UpdaterichTextBox1("Remote Assistance feature already installed. Skip.");
                return;
            }

            string stdout;
            if (!Util.EnableWindowsFeature("RemoteAssistance", out stdout))
                UpdaterichTextBox1("Remote Assistance: " + stdout);

            UpdaterichTextBox1("Completed to enable remote assistance feature.");
        }

        private void ConfigFirewallRuleForRemoteAssistance()
        {
            Process p = new Process();
            p.StartInfo.FileName = "netsh.exe";
            p.StartInfo.Arguments = "advfirewall firewall set rule group=\"Remote Assistance\" new enable=yes";
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            p.Start();
            string output = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            int ret = p.ExitCode;

            if (ret != 0)
            {
               // msg("  WARNING: return code=" + ret);
                UpdaterichTextBox1("WARNING: " + output);
            }

            UpdaterichTextBox1("Completed to configure firewall for remote assistance feature.");

        }
    }
}
