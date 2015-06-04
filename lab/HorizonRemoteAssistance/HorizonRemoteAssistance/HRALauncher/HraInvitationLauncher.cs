using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Diagnostics;
using System.Threading;
using System.Windows.Forms;
using Newtonsoft.Json;

namespace HRALauncher
{
    public class HraInvitationLauncher : IDisposable
    {
        public string machine;
        public string user;
        public string domain;
        public string os;
        public string inv;
        public string code;

        private string invFileName;
        private string msraIncidentFileName;

        internal static HraInvitationLauncher Load(string invitationFileName)
        {
            string json = null;
            try
            {
                json = File.ReadAllText(invitationFileName, Encoding.UTF8);
                HraInvitationLauncher inv = JsonConvert.DeserializeObject<HraInvitationLauncher>(json);

                inv.invFileName = invitationFileName;

                return inv;
            }
            catch (Exception e)
            {
                if (json != null)
                    Log.Error("Error parsing inv: " + json);
                throw e;
            }
        }

        internal string SaveIncidentFile()
        {
            msraIncidentFileName = invFileName + ".msrcIncident";
            Log.Info("Writing ticket: " + msraIncidentFileName);
            File.WriteAllText(msraIncidentFileName, inv);
            return msraIncidentFileName;
        }

        internal string GetCodeUnmasked()
        {
            StringBuilder res = new StringBuilder();
            char[] chars = code.ToCharArray();
            for (int i = chars.Length - 1; i >= 0; i--)
            {
                char c = chars[i];
                c = (char)(c + ((chars.Length - i - 1) % 2 == 0 ? -1 : 1));
                res.Append(c);
            }
            return res.ToString();
        }

        internal void DeleteTempFiles()
        {
            Log.Info("Clean up...");
            try
            {
                File.Delete(msraIncidentFileName);
            }
            catch (Exception e)
            {
                Log.Error(e);
            }
            try
            {
                File.Delete(invFileName);
            }
            catch (Exception e)
            {
                Log.Error(e);
            }
        }

        internal void LaunchMSRA()
        {

            SaveIncidentFile();

            try
            {
                Log.Info("Starting MSRA...");
                Process proc = new Process();
                proc.StartInfo.FileName = "msra.exe";
                proc.StartInfo.Arguments = "/openfile \"" + msraIncidentFileName + "\"";
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

                proc.Start();

                LoginAutomator.FillPasswordAndProceed(proc, GetCodeUnmasked());

                Log.Info("Waiting for MSRA complete...");
                proc.WaitForExit();
            }
            catch (Exception e)
            {
                Log.Error(e);
            }

        }

        #region IDisposable Members

        public void Dispose()
        {
            DeleteTempFiles();
        }

        #endregion
    }
}
