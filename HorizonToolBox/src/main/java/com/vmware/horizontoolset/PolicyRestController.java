package com.vmware.horizontoolset;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.horizontoolset.util.DesktopPool;
import com.vmware.horizontoolset.util.GPOData;
import com.vmware.horizontoolset.util.Registry;
import com.vmware.horizontoolset.util.RunProgramWithinVM;
import com.vmware.horizontoolset.util.SessionUtil;
import com.vmware.horizontoolset.viewapi.impl.ViewAPIServiceImpl;
import com.vmware.vim.binding.vmodl.Binary;
import com.vmware.vim25.GuestProgramSpec;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.util.SecureString;

@RestController
public class PolicyRestController {
	private static Logger log = Logger.getLogger(PolicyRestController.class);
	
	public PolicyRestController(){
		log.debug("Create Policy Rest Controller");
	}

	

	@RequestMapping("/policy/updatepolicies")
    public void updatePolicies(HttpSession session, 
    		@RequestParam(value="pool", required=false, defaultValue="Win7X6402") String pool,
    		@RequestParam(value="clipboard", required=false, defaultValue="") String clipboard,
    		@RequestParam(value="device", required=false, defaultValue="") String device,
    		
    		@RequestParam(value="productionLogs", required=false, defaultValue="") String productionLogs,
    		@RequestParam(value="debugLogs", required=false, defaultValue="") String debugLogs,
    		@RequestParam(value="logSize", required=false, defaultValue="") String logSize,
    		@RequestParam(value="logDirectory", required=false, defaultValue="") String logDirectory,
    		@RequestParam(value="sendLogs", required=false, defaultValue="") String sendLogs,
    		@RequestParam(value="interval", required=false, defaultValue="") String interval,
    		@RequestParam(value="overallCPU", required=false, defaultValue="") String overallCPU,
    		@RequestParam(value="overallMemory", required=false, defaultValue="") String overallMemory,
    		@RequestParam(value="processCPU", required=false, defaultValue="") String processCPU,
    		@RequestParam(value="processMemory", required=false, defaultValue="") String processMemory,
    		@RequestParam(value="processCheck", required=false, defaultValue="") String processCheck,
    		@RequestParam(value="certificateRevocation", required=false, defaultValue="") String certificateRevocation,
    		@RequestParam(value="cachedRevocation", required=false, defaultValue="") String cachedRevocation,
    		@RequestParam(value="checkTimeout", required=false, defaultValue="") String checkTimeout,
    		@RequestParam(value="lossless", required=false, defaultValue="") String lossless,
    		@RequestParam(value="maximum", required=false, defaultValue="") String maximum,
    		@RequestParam(value="MTU", required=false, defaultValue="") String MTU,
    		@RequestParam(value="floor", required=false, defaultValue="") String floor,
    		@RequestParam(value="enDisAudio", required=false, defaultValue="") String enDisAudio,
    		@RequestParam(value="limit", required=false, defaultValue="") String limit,
    		@RequestParam(value="SSL", required=false, defaultValue="") String SSL,
    		@RequestParam(value="encryption", required=false, defaultValue="") String encryption,
    		@RequestParam(value="USB", required=false, defaultValue="") String USB,
    		@RequestParam(value="TCP", required=false, defaultValue="") String TCP,
    		@RequestParam(value="UDP", required=false, defaultValue="") String UDP,
    		@RequestParam(value="channels", required=false, defaultValue="") String channels,
    		@RequestParam(value="image", required=false, defaultValue="") String image,
    		@RequestParam(value="FIPS", required=false, defaultValue="") String FIPS,
    		@RequestParam(value="vSphere", required=false, defaultValue="") String vSphere,
    		@RequestParam(value="synchronization", required=false, defaultValue="") String synchronization,
    		@RequestParam(value="alternate", required=false, defaultValue="") String alternate,
    		@RequestParam(value="CAD", required=false, defaultValue="") String CAD,
    		@RequestParam(value="transport", required=false, defaultValue="") String transport,
    		@RequestParam(value="verbosity", required=false, defaultValue="") String verbosity,
    		@RequestParam(value="timeindays", required=false, defaultValue="") String timeindays,
    		@RequestParam(value="sizeinmb", required=false, defaultValue="") String sizeinmb,
    		@RequestParam(value="exclude", required=false, defaultValue="") String exclude,
    		@RequestParam(value="splitdevice", required=false, defaultValue="") String splitdevice,
    		@RequestParam(value="other", required=false, defaultValue="") String other,
    		@RequestParam(value="HID", required=false, defaultValue="") String HID,
    		@RequestParam(value="inputdevices", required=false, defaultValue="") String inputdevices,
    		@RequestParam(value="outputdevices", required=false, defaultValue="") String outputdevices,
    		@RequestParam(value="keyboard", required=false, defaultValue="") String keyboard,
    		@RequestParam(value="videodevices", required=false, defaultValue="") String videodevices,
    		@RequestParam(value="smartcards", required=false, defaultValue="") String smartcards,
    		@RequestParam(value="autodevice", required=false, defaultValue="") String autodevice,
    		@RequestParam(value="excludeVP", required=false, defaultValue="") String excludeVP,
    		@RequestParam(value="includeVP", required=false, defaultValue="") String includeVP,
    		@RequestParam(value="excludeDF", required=false, defaultValue="") String excludeDF,
    		@RequestParam(value="includeDF", required=false, defaultValue="") String includeDF,
    		@RequestParam(value="exclludeAll", required=false, defaultValue="") String exclludeAll,
    		@RequestParam(value="MMR", required=false, defaultValue="") String MMR,
    		@RequestParam(value="multimedia", required=false, defaultValue="") String multimedia,
    		@RequestParam(value="directRDP", required=false, defaultValue="") String directRDP,
    		@RequestParam(value="singleSignon", required=false, defaultValue="") String singleSignon,
    		@RequestParam(value="timeout", required=false, defaultValue="") String timeout,
    		@RequestParam(value="credentialFilter", required=false, defaultValue="") String credentialFilter,
    		@RequestParam(value="usingDNS", required=false, defaultValue="") String usingDNS,
    		@RequestParam(value="disableTZName", required=false, defaultValue="") String disableTZName,
    		@RequestParam(value="toggle", required=false, defaultValue="") String toggle,
    		@RequestParam(value="onConnect", required=false, defaultValue="") String onConnect,
    		@RequestParam(value="onReconnect", required=false, defaultValue="") String onReconnect,
    		@RequestParam(value="onDisconnect", required=false, defaultValue="") String onDisconnect,
    		@RequestParam(value="showIcon", required=false, defaultValue="") String showIcon,
    		@RequestParam(value="frameworkChannel", required=false, defaultValue="") String frameworkChannel,
    		@RequestParam(value="unity", required=false, defaultValue="") String unity,
    		@RequestParam(value="maxFrames", required=false, defaultValue="") String maxFrames,
    		@RequestParam(value="maxImageHeight", required=false, defaultValue="") String maxImageHeight,
    		@RequestParam(value="maxImageWidth", required=false, defaultValue="") String maxImageWidth,
    		@RequestParam(value="defaultImageHeight", required=false, defaultValue="") String defaultImageHeight,
    		@RequestParam(value="defaultImageWidth", required=false, defaultValue="") String defaultImageWidth,
    		@RequestParam(value="disableRTAV", required=false, defaultValue="") String disableRTAV,
    		@RequestParam(value="portNumber", required=false, defaultValue="") String portNumber,
    		@RequestParam(value="sessionTimeout", required=false, defaultValue="") String sessionTimeout,
    		@RequestParam(value="disclaimerEnabled", required=false, defaultValue="") String disclaimerEnabled,
    		@RequestParam(value="disclaimerText", required=false, defaultValue="") String disclaimerText,
    		@RequestParam(value="applictionsEnabled", required=false, defaultValue="") String applictionsEnabled,
    		@RequestParam(value="autoConnect", required=false, defaultValue="") String autoConnect,
    		@RequestParam(value="alwaysConnect", required=false, defaultValue="") String alwaysConnect,
    		@RequestParam(value="screenSize", required=false, defaultValue="") String screenSize,
    		@RequestParam(value="PCoIPport", required=false, defaultValue="") String PCoIPport,
    		@RequestParam(value="RDPport", required=false, defaultValue="") String RDPport,
    		@RequestParam(value="blastPort", required=false, defaultValue="") String blastPort,
    		@RequestParam(value="IPaddress", required=false, defaultValue="") String IPaddress,
    		@RequestParam(value="channelPort", required=false, defaultValue="") String channelPort,
    		@RequestParam(value="USBenabled", required=false, defaultValue="") String USBenabled,
    		@RequestParam(value="MMRenabled", required=false, defaultValue="") String MMRenabled,
    		@RequestParam(value="resetEnabled", required=false, defaultValue="") String resetEnabled,
    		@RequestParam(value="USBautoConnect", required=false, defaultValue="") String USBautoConnect,
    		@RequestParam(value="cacheTimeout", required=false, defaultValue="") String cacheTimeout,
    		@RequestParam(value="DisSessionTimeout", required=false, defaultValue="") String DisSessionTimeout	
    		
    		) {
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
			if(!(clipboard == "" | clipboard.endsWith(null)))
				updateGPO("clipboard",clipboard,runScriptWithinVM,si,msvs);
			if(!(device == "" | device.endsWith(null)))
				updateGPO("device",device,runScriptWithinVM,si,msvs);
			//
			if(!(productionLogs== "" | productionLogs.endsWith(null))) 
				 updateGPO("productionLogs",productionLogs,runScriptWithinVM,si,msvs);
			if(!(debugLogs== "" | debugLogs.endsWith(null))) 
				 updateGPO("debugLogs",debugLogs,runScriptWithinVM,si,msvs);
			if(!(logSize== "" | logSize.endsWith(null))) 
				 updateGPO("logSize",logSize,runScriptWithinVM,si,msvs);
			if(!(logDirectory== "" | logDirectory.endsWith(null))) 
				 updateGPO("logDirectory",logDirectory,runScriptWithinVM,si,msvs);
			if(!(sendLogs== "" | sendLogs.endsWith(null))) 
				 updateGPO("sendLogs",sendLogs,runScriptWithinVM,si,msvs);
			if(!(interval== "" | interval.endsWith(null))) 
				 updateGPO("interval",interval,runScriptWithinVM,si,msvs);
			if(!(overallCPU== "" | overallCPU.endsWith(null))) 
				 updateGPO("overallCPU",overallCPU,runScriptWithinVM,si,msvs);
			if(!(overallMemory== "" | overallMemory.endsWith(null))) 
				 updateGPO("overallMemory",overallMemory,runScriptWithinVM,si,msvs);
			if(!(processCPU== "" | processCPU.endsWith(null))) 
				 updateGPO("processCPU",processCPU,runScriptWithinVM,si,msvs);
			if(!(processMemory== "" | processMemory.endsWith(null))) 
				 updateGPO("processMemory",processMemory,runScriptWithinVM,si,msvs);
			if(!(processCheck== "" | processCheck.endsWith(null))) 
				 updateGPO("processCheck",processCheck,runScriptWithinVM,si,msvs);
			if(!(certificateRevocation== "" | certificateRevocation.endsWith(null))) 
				 updateGPO("certificateRevocation",certificateRevocation,runScriptWithinVM,si,msvs);
			if(!(cachedRevocation== "" | cachedRevocation.endsWith(null))) 
				 updateGPO("cachedRevocation",cachedRevocation,runScriptWithinVM,si,msvs);
			if(!(checkTimeout== "" | checkTimeout.endsWith(null))) 
				 updateGPO("checkTimeout",checkTimeout,runScriptWithinVM,si,msvs);
			if(!(lossless== "" | lossless.endsWith(null))) 
				 updateGPO("lossless",lossless,runScriptWithinVM,si,msvs);
			if(!(maximum== "" | maximum.endsWith(null))) 
				 updateGPO("maximum",maximum,runScriptWithinVM,si,msvs);
			if(!(MTU== "" | MTU.endsWith(null))) 
				 updateGPO("MTU",MTU,runScriptWithinVM,si,msvs);
			if(!(floor== "" | floor.endsWith(null))) 
				 updateGPO("floor",floor,runScriptWithinVM,si,msvs);
			if(!(enDisAudio== "" | enDisAudio.endsWith(null))) 
				 updateGPO("enDisAudio",enDisAudio,runScriptWithinVM,si,msvs);
			if(!(limit== "" | limit.endsWith(null))) 
				 updateGPO("limit",limit,runScriptWithinVM,si,msvs);
			if(!(SSL== "" | SSL.endsWith(null))) 
				 updateGPO("SSL",SSL,runScriptWithinVM,si,msvs);
			if(!(encryption== "" | encryption.endsWith(null))) 
				 updateGPO("encryption",encryption,runScriptWithinVM,si,msvs);
			if(!(USB== "" | USB.endsWith(null))) 
				 updateGPO("USB",USB,runScriptWithinVM,si,msvs);
			if(!(TCP== "" | TCP.endsWith(null))) 
				 updateGPO("TCP",TCP,runScriptWithinVM,si,msvs);
			if(!(UDP== "" | UDP.endsWith(null))) 
				 updateGPO("UDP",UDP,runScriptWithinVM,si,msvs);
			if(!(channels== "" | channels.endsWith(null))) 
				 updateGPO("channels",channels,runScriptWithinVM,si,msvs);
			if(!(image== "" | image.endsWith(null))) 
				 updateGPO("image",image,runScriptWithinVM,si,msvs);
			if(!(FIPS== "" | FIPS.endsWith(null))) 
				 updateGPO("FIPS",FIPS,runScriptWithinVM,si,msvs);
			if(!(vSphere== "" | vSphere.endsWith(null))) 
				 updateGPO("vSphere",vSphere,runScriptWithinVM,si,msvs);
			if(!(synchronization== "" | synchronization.endsWith(null))) 
				 updateGPO("synchronization",synchronization,runScriptWithinVM,si,msvs);
			if(!(alternate== "" | alternate.endsWith(null))) 
				 updateGPO("alternate",alternate,runScriptWithinVM,si,msvs);
			if(!(CAD== "" | CAD.endsWith(null))) 
				 updateGPO("CAD",CAD,runScriptWithinVM,si,msvs);
			if(!(transport== "" | transport.endsWith(null))) 
				 updateGPO("transport",transport,runScriptWithinVM,si,msvs);
			if(!(verbosity== "" | verbosity.endsWith(null))) 
				 updateGPO("verbosity",verbosity,runScriptWithinVM,si,msvs);
			if(!(timeindays== "" | timeindays.endsWith(null))) 
				 updateGPO("timeindays",timeindays,runScriptWithinVM,si,msvs);
			if(!(sizeinmb== "" | sizeinmb.endsWith(null))) 
				 updateGPO("sizeinmb",sizeinmb,runScriptWithinVM,si,msvs);
			if(!(exclude== "" | exclude.endsWith(null))) 
				 updateGPO("exclude",exclude,runScriptWithinVM,si,msvs);
			if(!(splitdevice== "" | splitdevice.endsWith(null))) 
				 updateGPO("splitdevice",splitdevice,runScriptWithinVM,si,msvs);
			if(!(other== "" | other.endsWith(null))) 
				 updateGPO("other",other,runScriptWithinVM,si,msvs);
			if(!(HID== "" | HID.endsWith(null))) 
				 updateGPO("HID",HID,runScriptWithinVM,si,msvs);
			if(!(inputdevices== "" | inputdevices.endsWith(null))) 
				 updateGPO("inputdevices",inputdevices,runScriptWithinVM,si,msvs);
			if(!(outputdevices== "" | outputdevices.endsWith(null))) 
				 updateGPO("outputdevices",outputdevices,runScriptWithinVM,si,msvs);
			if(!(keyboard== "" | keyboard.endsWith(null))) 
				 updateGPO("keyboard",keyboard,runScriptWithinVM,si,msvs);
			if(!(videodevices== "" | videodevices.endsWith(null))) 
				 updateGPO("videodevices",videodevices,runScriptWithinVM,si,msvs);
			if(!(smartcards== "" | smartcards.endsWith(null))) 
				 updateGPO("smartcards",smartcards,runScriptWithinVM,si,msvs);
			if(!(autodevice== "" | autodevice.endsWith(null))) 
				 updateGPO("autodevice",autodevice,runScriptWithinVM,si,msvs);
			if(!(excludeVP== "" | excludeVP.endsWith(null))) 
				 updateGPO("excludeVP",excludeVP,runScriptWithinVM,si,msvs);
			if(!(includeVP== "" | includeVP.endsWith(null))) 
				 updateGPO("includeVP",includeVP,runScriptWithinVM,si,msvs);
			if(!(excludeDF== "" | excludeDF.endsWith(null))) 
				 updateGPO("excludeDF",excludeDF,runScriptWithinVM,si,msvs);
			if(!(includeDF== "" | includeDF.endsWith(null))) 
				 updateGPO("includeDF",includeDF,runScriptWithinVM,si,msvs);
			if(!(exclludeAll== "" | exclludeAll.endsWith(null))) 
				 updateGPO("exclludeAll",exclludeAll,runScriptWithinVM,si,msvs);
			if(!(MMR== "" | MMR.endsWith(null))) 
				 updateGPO("MMR",MMR,runScriptWithinVM,si,msvs);
			if(!(multimedia== "" | multimedia.endsWith(null))) 
				 updateGPO("multimedia",multimedia,runScriptWithinVM,si,msvs);
			if(!(directRDP== "" | directRDP.endsWith(null))) 
				 updateGPO("directRDP",directRDP,runScriptWithinVM,si,msvs);
			if(!(singleSignon== "" | singleSignon.endsWith(null))) 
				 updateGPO("singleSignon",singleSignon,runScriptWithinVM,si,msvs);
			if(!(timeout== "" | timeout.endsWith(null))) 
				 updateGPO("timeout",timeout,runScriptWithinVM,si,msvs);
			if(!(credentialFilter== "" | credentialFilter.endsWith(null))) 
				 updateGPO("credentialFilter",credentialFilter,runScriptWithinVM,si,msvs);
			if(!(usingDNS== "" | usingDNS.endsWith(null))) 
				 updateGPO("usingDNS",usingDNS,runScriptWithinVM,si,msvs);
			if(!(disableTZName== "" | disableTZName.endsWith(null))) 
				 updateGPO("disableTZName",disableTZName,runScriptWithinVM,si,msvs);
			if(!(toggle== "" | toggle.endsWith(null))) 
				 updateGPO("toggle",toggle,runScriptWithinVM,si,msvs);
			if(!(onConnect== "" | onConnect.endsWith(null))) 
				 updateGPO("onConnect",onConnect,runScriptWithinVM,si,msvs);
			if(!(onReconnect== "" | onReconnect.endsWith(null))) 
				 updateGPO("onReconnect",onReconnect,runScriptWithinVM,si,msvs);
			if(!(onDisconnect== "" | onDisconnect.endsWith(null))) 
				 updateGPO("onDisconnect",onDisconnect,runScriptWithinVM,si,msvs);
			if(!(showIcon== "" | showIcon.endsWith(null))) 
				 updateGPO("showIcon",showIcon,runScriptWithinVM,si,msvs);
			if(!(frameworkChannel== "" | frameworkChannel.endsWith(null))) 
				 updateGPO("frameworkChannel",frameworkChannel,runScriptWithinVM,si,msvs);
			if(!(unity== "" | unity.endsWith(null))) 
				 updateGPO("unity",unity,runScriptWithinVM,si,msvs);
			if(!(maxFrames== "" | maxFrames.endsWith(null))) 
				 updateGPO("maxFrames",maxFrames,runScriptWithinVM,si,msvs);
			if(!(maxImageHeight== "" | maxImageHeight.endsWith(null))) 
				 updateGPO("maxImageHeight",maxImageHeight,runScriptWithinVM,si,msvs);
			if(!(maxImageWidth== "" | maxImageWidth.endsWith(null))) 
				 updateGPO("maxImageWidth",maxImageWidth,runScriptWithinVM,si,msvs);
			if(!(defaultImageHeight== "" | defaultImageHeight.endsWith(null))) 
				 updateGPO("defaultImageHeight",defaultImageHeight,runScriptWithinVM,si,msvs);
			if(!(defaultImageWidth== "" | defaultImageWidth.endsWith(null))) 
				 updateGPO("defaultImageWidth",defaultImageWidth,runScriptWithinVM,si,msvs);
			if(!(disableRTAV== "" | disableRTAV.endsWith(null))) 
				 updateGPO("disableRTAV",disableRTAV,runScriptWithinVM,si,msvs);
			if(!(portNumber== "" | portNumber.endsWith(null))) 
				 updateGPO("portNumber",portNumber,runScriptWithinVM,si,msvs);
			if(!(sessionTimeout== "" | sessionTimeout.endsWith(null))) 
				 updateGPO("sessionTimeout",sessionTimeout,runScriptWithinVM,si,msvs);
			if(!(disclaimerEnabled== "" | disclaimerEnabled.endsWith(null))) 
				 updateGPO("disclaimerEnabled",disclaimerEnabled,runScriptWithinVM,si,msvs);
			if(!(disclaimerText== "" | disclaimerText.endsWith(null))) 
				 updateGPO("disclaimerText",disclaimerText,runScriptWithinVM,si,msvs);
			if(!(applictionsEnabled== "" | applictionsEnabled.endsWith(null))) 
				 updateGPO("applictionsEnabled",applictionsEnabled,runScriptWithinVM,si,msvs);
			if(!(autoConnect== "" | autoConnect.endsWith(null))) 
				 updateGPO("autoConnect",autoConnect,runScriptWithinVM,si,msvs);
			if(!(alwaysConnect== "" | alwaysConnect.endsWith(null))) 
				 updateGPO("alwaysConnect",alwaysConnect,runScriptWithinVM,si,msvs);
			if(!(screenSize== "" | screenSize.endsWith(null))) 
				 updateGPO("screenSize",screenSize,runScriptWithinVM,si,msvs);
			if(!(PCoIPport== "" | PCoIPport.endsWith(null))) 
				 updateGPO("PCoIPport",PCoIPport,runScriptWithinVM,si,msvs);
			if(!(RDPport== "" | RDPport.endsWith(null))) 
				 updateGPO("RDPport",RDPport,runScriptWithinVM,si,msvs);
			if(!(blastPort== "" | blastPort.endsWith(null))) 
				 updateGPO("blastPort",blastPort,runScriptWithinVM,si,msvs);
			if(!(IPaddress== "" | IPaddress.endsWith(null))) 
				 updateGPO("IPaddress",IPaddress,runScriptWithinVM,si,msvs);
			if(!(channelPort== "" | channelPort.endsWith(null))) 
				 updateGPO("channelPort",channelPort,runScriptWithinVM,si,msvs);
			if(!(USBenabled== "" | USBenabled.endsWith(null))) 
				 updateGPO("USBenabled",USBenabled,runScriptWithinVM,si,msvs);
			if(!(MMRenabled== "" | MMRenabled.endsWith(null))) 
				 updateGPO("MMRenabled",MMRenabled,runScriptWithinVM,si,msvs);
			if(!(resetEnabled== "" | resetEnabled.endsWith(null))) 
				 updateGPO("resetEnabled",resetEnabled,runScriptWithinVM,si,msvs);
			if(!(USBautoConnect== "" | USBautoConnect.endsWith(null))) 
				 updateGPO("USBautoConnect",USBautoConnect,runScriptWithinVM,si,msvs);
			if(!(cacheTimeout== "" | cacheTimeout.endsWith(null))) 
				 updateGPO("cacheTimeout",cacheTimeout,runScriptWithinVM,si,msvs);
			if(!(DisSessionTimeout== "" | DisSessionTimeout.endsWith(null))) 
				 updateGPO("DisSessionTimeout",DisSessionTimeout,runScriptWithinVM,si,msvs);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	private void updateGPO(Hash gpoHash, RunProgramWithinVM runScriptWithinVM,
			ServiceInstance si, List<MachineSummaryView> msvs) {
		Registry registry = new Registry();
		
		
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = null;
		try {
			for(MachineSummaryView msv : msvs){
				vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", msv.base.name);
				for(gpoHash){
					GuestProgramSpec spec = registry.addRegistry(
							GPOData.getData(gpoHash.key + "_KEYNAME"),
							GPOData.getData(gpoHash.value + "_VALUENAME"),
							gpoValue);
					runScriptWithinVM.runScriptWithinVM(si, vm, spec);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
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
