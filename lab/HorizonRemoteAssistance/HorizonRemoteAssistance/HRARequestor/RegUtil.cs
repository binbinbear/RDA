using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace HRARequestor
{
    class RegUtil
    {
        public static string ReadLocalMachine(string key, string attr)
        {
            try
            {
                RegistryKey rk = Registry.LocalMachine.OpenSubKey(key);
                if (rk == null)
                    return null;

                Object o = rk.GetValue(attr);
                if (o == null)
                    return null;

                return o.ToString();
            }
            catch (Exception)
            {
                return null;
            }
        }
    }
}
