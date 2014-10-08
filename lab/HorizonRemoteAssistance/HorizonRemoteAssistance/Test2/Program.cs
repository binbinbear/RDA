using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Management;

namespace Test2
{
    class Program
    {
        static void Main(string[] args)
        {
            ManagementClass objMC = new ManagementClass("Win32_ServerFeature");
            ManagementObjectCollection objMOC = objMC.GetInstances();
            foreach (ManagementObject objMO in objMOC)
            {
                string featureName = (string)objMO.Properties["Name"].Value;
                string id = objMO.Properties["Id"].Value.ToString();
                Console.WriteLine("" + id + " - " + featureName);
            }

            Console.WriteLine("Done.");
        }
    }
}
