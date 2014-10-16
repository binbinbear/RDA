package com.vmware.horizontoolset.util;

import java.rmi.RemoteException;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

public class RunProgramWithinVM {
	private String username;
	private String password;

	public RunProgramWithinVM(String userName, String passWord){
		username = userName;
		password = passWord;
	}

	public long runScriptWithinVM(ServiceInstance si,
			VirtualMachine vm,
			GuestProgramSpec spec)
					throws GuestOperationsFault, InvalidState, TaskInProgress, FileFault, RuntimeFault, RemoteException{
		GuestOperationsManager gom = new GuestOperationsManager(
        		si.getServerConnection(), 
        		si.getServiceContent().getGuestOperationsManager());

        if(!"guestToolsRunning".equals(vm.getGuest().toolsRunningStatus))
        {
          System.out.println("The VMware Tools is not running in the Guest OS on VM: " + vm.getName());
          System.out.println("Exiting...");
          return 0;
        }
     
        NamePasswordAuthentication npa = new NamePasswordAuthentication();
        npa.username = username;
        npa.password = password;
     
        //GuestProgramSpec spec = new GuestProgramSpec();
        //spec.programPath = programPath;
        //spec.arguments = arguments;
        //spec.programPath = "C:\\WINDOWS\\System32\\reg.exe";
        //spec.arguments = "add \"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\" /v pcoip.server_clipboard_state /t REG_SZ /d 1 /f";
     
        GuestProcessManager gpm = gom.getProcessManager(vm);
        long pid = gpm.startProgramInGuest(npa, spec);
        return pid;
	}
}
