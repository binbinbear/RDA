using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Management;
using System.Net;
using System.Diagnostics;
using System.Threading;
using Microsoft.Win32;
using System.Reflection;
using ETEUtils;

namespace Test2
{
    class Program
    {
        static void Main(string[] args)
        {
            bool? b = null;
            if (b.HasValue)
                Console.WriteLine("aa");

            //Server.Start(18443, "asdf");

            Console.WriteLine("OK.");
            Console.ReadKey();
            //PolFile pol = new PolFile();

            //pol.LoadFile(@"Z:\EnablementPrjs\policy\registry.pol");
            //pol.SaveFile(@"c:\r1.pol");

            /*
            try
            {
                string userDomain = Environment.GetEnvironmentVariable("USERDNSDOMAIN");
                Console.WriteLine(userDomain);
                if (userDomain == null || userDomain.Trim().Length == 0)
                    userDomain = "asdf.nanw";
                GPMGMTLib.GPM gpm = new GPMGMTLib.GPM();
                GPMGMTLib.IGPMConstants gpc = gpm.GetConstants();
                GPMGMTLib.IGPMDomain gpd = gpm.GetDomain(userDomain, "", gpc.UseAnyDC);
                GPMGMTLib.GPMSearchCriteria searchOBJ = gpm.CreateSearchCriteria();
                GPMGMTLib.IGPMGPOCollection gpoc = gpd.SearchGPOs(searchOBJ);
                string outputString = "";
                foreach (GPMGMTLib.GPMGPO name in gpoc)
                {
                    outputString += "ID: " + name.ID + "\tName: " + name.DisplayName + "\r\n";
                }
                Console.WriteLine(outputString);

                string strGPO = "StarterGPO_notepad";
                string strOU = "OU=NestedOU2,OU=TestOU,DC=asdf,DC=nanw";

                searchOBJ.Add(gpc.SearchPropertyGPODisplayName, gpc.SearchOpEquals, strGPO);
                GPMGMTLib.GPMGPOCollection objGPOlist = gpd.SearchGPOs(searchOBJ);

                GPMGMTLib.GPMSOM gpSom = gpd.GetSOM(strOU);
                //GPMGMTLib.IGPMGPO gpo = gpd.CreateGPO();
                //GPMGMTLib.IGPMGPO gpo = (GPMGMTLib.IGPMGPO)objGPOlist[1];

                GPMGMTLib.IGPMGPO gpo = gpd.CreateGPO();
                gpo.DisplayName = "TestOutCome";
                //gpo.Import();
                gpSom.CreateGPOLink(-1, gpo);
                //Console.ReadKey();
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
            //*/
        }
    }
}
