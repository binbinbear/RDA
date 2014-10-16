package com.vmware.horizontoolset;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.report.ViewPoolReport;
import com.vmware.horizontoolset.util.DesktopPool;
import com.vmware.horizontoolset.util.GPOData;
import com.vmware.horizontoolset.util.Registry;
import com.vmware.horizontoolset.util.RunProgramWithinVM;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.impl.ViewAPIServiceImpl;
import com.vmware.vim.binding.vmodl.Binary;
import com.vmware.vim25.GuestProgramSpec;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.util.SecureString;

@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	
	public static final int refershInterValSeconds = 300;
	private static ViewPoolReport cachedreport =null;
	private static long timestamp;
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
	}

	@RequestMapping("/policy/viewpools")
    public synchronized List<DesktopSummaryView> getViewPools(HttpSession session) {
		long currenttime = new Date().getTime();
		List<DesktopSummaryView> alldsv = null;
    	if (cachedreport !=null && currenttime - timestamp < 1000 *refershInterValSeconds ){
    		 log.debug("Receive get request for clients, and reuse previous report");
    	}else{
    		timestamp = currenttime;
        	try{
                log.debug("Receive get request for clients");
                ViewAPIService service = SessionUtil.getViewAPIService(session);
                alldsv = service.listDesktopPools();
        	}catch(Exception ex){
        		log.error("Exception, return to login",ex);
        	}
    	}
    	return alldsv;
	}

	@RequestMapping("/policy/updatepolicies")
    public void getConnections(HttpSession session, 
    		@RequestParam(value="pool", required=false, defaultValue="Win7X6402") String pool,
    		@RequestParam(value="clipboard", required=false, defaultValue="0") String clipboard) {
		log.debug("Run updata policies function, and pool is "+pool+", clipboard is "+clipboard+".");
		ServiceInstance si;
		try {
			si = new ServiceInstance(new URL("https://10.117.9.198/sdk"),"root","vmware", true);
			ViewAPIServiceImpl service = (ViewAPIServiceImpl)SessionUtil.getViewAPIService(session);
			SecureString vcpass = service.listVirtualCenters().get(0).getServerSpec().getPassword();
			Binary vcbinary = vcpass.getUtf8String();
			log.debug("VC password is:" + vcbinary.toString());
    		DesktopPool desktopPool = service.getDesktopPool(pool);
    		RunProgramWithinVM runScriptWithinVM = new RunProgramWithinVM(service.get_domain()+"\\"+service.get_user(),service.get_pass());
			List<MachineSummaryView> msvs = desktopPool.getMachines(desktopPool.id);
			updateGPO("clipboard",clipboard,runScriptWithinVM,si,msvs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanReport() {
		cachedreport =null;
	}

	private void updateGPO(String gpoName, String gpoValue, RunProgramWithinVM runScriptWithinVM,
			ServiceInstance si, List<MachineSummaryView> msvs) {
		Registry registry = new Registry();
		GuestProgramSpec spec = registry.addRegistry(
				GPOData.getData(gpoName + "_KEYNAME"),
				GPOData.getData(gpoName + "_VALUENAME"),
				gpoValue);
		
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = null;
		try {
			for(MachineSummaryView msv : msvs){
				vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", msv.base.name);
				runScriptWithinVM.runScriptWithinVM(si, vm, spec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//PolicyRestController prc = new PolicyRestController();
		try {
			ServiceInstance si = new ServiceInstance(new URL("https://10.117.9.198/sdk"),"root","vmware", true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
