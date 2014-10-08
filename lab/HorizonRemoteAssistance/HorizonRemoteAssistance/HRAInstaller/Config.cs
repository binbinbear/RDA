using System;
using System.Collections.Generic;
using System.Text;

namespace HRAInstaller
{
    class Config
    {

        public const int UNINSTALL = 0;
        public const int INSTALL_FOR_END_USER = 1;
        public const int INSTALL_FOR_ADMIN = 2;
        public const int INSTALL_LAUNCHER = 3;

        public static int installType;

        public static readonly string HRALauncherDir = Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles) + @"\VMware\Horizon Remote Assistance";
        public static readonly string HRALauncherPath = HRALauncherDir + "\\Horizon Remote Assistance Launcher.exe";
        public static readonly string AdminExUrl = "https://localhost/admin_ex";
        public static readonly string AdminExShortcutName = "Horizon Remote Assistance Administroator";

        public static readonly string HRARequestorName = "Horizon Remote Assistance.exe";
        public static readonly string HRARequestorPath = Util.GetAllUserDesktopFolderPriorNET40() + "\\" + Config.HRARequestorName;
        public static readonly string MsraPath = Environment.GetFolderPath(Environment.SpecialFolder.System) + "\\msra.exe";
    }
}
