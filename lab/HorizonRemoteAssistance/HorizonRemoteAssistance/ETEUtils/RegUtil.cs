using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace ETEUtils
{
    public class RegUtil
    {
        public static int ReadStringAsInt(string key, string attr, int defaultValue)
        {
            object val = Registry.GetValue(key, attr, null);

            int result;
            if (val == null || !int.TryParse(val.ToString(), out result))
                return defaultValue;
            return result;
        }

        public static string ReadString(string key, string attr, string defaultValue)
        {
            object ret = Registry.GetValue(key, attr, null);
            return ret == null ? defaultValue : ret.ToString();
        }
    }
}
