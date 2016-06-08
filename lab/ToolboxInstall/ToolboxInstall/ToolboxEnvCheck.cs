using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Win32;
using System.Windows.Forms;

namespace ToolboxInstall
{
    class ToolboxEnvCheck
    {
        public static bool VerifyJavaVersion()
        {
            // should be bigger than 1.8
            try
            {
                RegistryKey rk = Registry.LocalMachine;
                RegistryKey subKey = rk.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");

                if (subKey != null)
                {
                    string currentVerion = subKey.GetValue("CurrentVersion").ToString();

                    if (currentVerion.CompareTo("1.8") >= 0)
                        return true;
                    else
                        return false;
                }
            }
            catch (Exception e)
            {
                ;
            }

            return false;
        }

        public static string GetRegularJrePath()
        {
            try
            {
                RegistryKey rk = Registry.LocalMachine;
                RegistryKey subKey = rk.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment");

                if (subKey != null)
                {
                    string currentVerion = subKey.GetValue("CurrentVersion").ToString();
                    subKey.Close();

                    if (currentVerion.CompareTo("1.8") >= 0)
                    {
                        RegistryKey subJreKey = rk.OpenSubKey("SOFTWARE\\JavaSoft\\Java Runtime Environment\\" + currentVerion);
                        if (subJreKey != null)
                        {
                            string jrePath = subJreKey.GetValue("JavaHome").ToString();
                            return jrePath;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                ;
            }

            return null;
        }


        public static bool NeedVerifyJavaVersion()      // If horizon view is less than 6.2, need install java 8
        {
            try
            {
                RegistryKey rk = Registry.LocalMachine;
                RegistryKey subKey = rk.OpenSubKey(@"SOFTWARE\VMware, Inc.\VMware VDM");
                if (subKey == null)
                {
                    return true;
                }

                string currentVerion = subKey.GetValue("ProductVersion").ToString();
                if (currentVerion == null)
                {
                    return true;
                }

                currentVerion.Trim();
                string[] versions = currentVerion.Split('.');

                if (versions.Length < 4)
                {
                    return true;
                }

                if (int.Parse(versions[0]) >= 7)
                    return false;
                else if ((int.Parse(versions[0]) == 6) && (int.Parse(versions[1]) >= 2))
                    return false;
                else
                {
                    return true;
                }
            }
            catch (Exception e)
            {
                ;
            }

            return true;
        }
    }
}
