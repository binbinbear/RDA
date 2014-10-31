using System;
using System.Collections.Generic;
using System.Text;

namespace Utils
{
    public class HttpUtil
    {
        public static bool HttpGet(string url, string var, string content)
        {
            using (var wb = new WebClient())
            {
                wb.Headers["User-Agent"] = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

                wb.QueryString.Add(var, content);

                Logger.Log("Requesting: " + url);

                try
                {
                    //we should post, however View Connection server has disabled POST.
                    //string ret = wb.UploadValues(.UploadString(url, );
                    string ret = wb.DownloadString(url);
                    Logger.Log("Response: " + ret);
                    return true;
                }
                catch (Exception ex)
                {
                    Logger.Log(ex);
                    return false;
                }
            }
        }
    }
}
