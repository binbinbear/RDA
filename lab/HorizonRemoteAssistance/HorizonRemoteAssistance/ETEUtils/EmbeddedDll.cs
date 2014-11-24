using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.IO;

namespace ETEUtils
{
    public class EmbeddedDll
    {

        static Dictionary<string, Assembly> dic = new Dictionary<string, Assembly>();

        /// <summary>
        /// Load Assembly, DLL from Embedded Resources into memory.
        /// </summary>
        /// <param name="embeddedResource">Embedded Resource string. Example: WindowsFormsApplication1.SomeTools.dll</param>
        /// <param name="fileName">File Name. Example: SomeTools.dll</param>
        public static void Load(string defaultNamespace, params string[] embeddedResource)
        {
            //string defaultNamespace = curAsm.GetName().Name;
            defaultNamespace += ".";

            foreach (string s in embeddedResource)
                LoadImpl(defaultNamespace + s);

            AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(CurrentDomain_AssemblyResolve);
        }

        private static void LoadImpl(string embeddedResource)
        {
            Assembly curAsm = Assembly.GetExecutingAssembly();

            using (Stream stream = curAsm.GetManifestResourceStream(embeddedResource))
            {
                // Either the file is not existed or it is not mark as embedded resource
                if (stream == null)
                    throw new Exception(embeddedResource + " is not found in Embedded Resources.");

                // Get byte[] from the file from embedded resource
                byte[] buf = new byte[(int)stream.Length];
                stream.Read(buf, 0, (int)stream.Length);
                Assembly asm = Assembly.Load(buf);

                // Add the assembly/dll into dictionary
                dic.Add(asm.FullName, asm);
            }
        }

        /// <summary>
        /// Retrieve specific loaded DLL/assembly from memory
        /// </summary>
        /// <param name="assemblyFullName"></param>
        /// <returns></returns>
        public static Assembly Get(string assemblyFullName)
        {
            if (dic == null || dic.Count == 0)
                return null;

            if (dic.ContainsKey(assemblyFullName))
                return dic[assemblyFullName];

            return null;

            // Don't throw Exception if the dictionary does not contain the requested assembly.
            // This is because the event of AssemblyResolve will be raised for every
            // Embedded Resources (such as pictures) of the projects.
            // Those resources wil not be loaded by this class and will not exist in dictionary.
        }

        static Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
        {
            return Get(args.Name);
        }
    }
}
