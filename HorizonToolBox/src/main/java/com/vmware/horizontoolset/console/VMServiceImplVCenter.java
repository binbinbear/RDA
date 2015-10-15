package com.vmware.horizontoolset.console;

import java.net.URLEncoder;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.console.vcenter.BasicVCConnection;
import com.vmware.horizontoolset.console.vcenter.ConsoleAccessInfoImpl;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vim25.AboutInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualMachineTicket;
import com.vmware.vim25.VirtualMachineTicketType;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class VMServiceImplVCenter implements VMService {

	private static Logger log = Logger.getLogger(VMServiceImplVCenter.class);
	private String path;
	
	private VDIContext vdictx;
	
	private String vcName;
	
	private static final String encoding = "utf-8";
	
	private VCVersion vcversion;
	
	public VMServiceImplVCenter(VDIContext ctx,
            String vcName, String path) {
		this.path = path;
		this.vdictx = ctx;
		this.vcName = vcName;
	}
	


	private int getMajorVersion(ServiceContent serviceContent) {
		AboutInfo about = serviceContent.getAbout();
		String version = about.getVersion();
		return Integer.valueOf(version.substring(0, 1)).intValue();
	}
	
	public ConsoleAccessInfo requestConsoleAccessInfo()  {
		

		
		
		BasicVCConnection vc = null;
		log.info("Start to create a connection to vcenter");
		try {
			vc = new BasicVCConnection(this.vdictx, this.vcName);
			
			//vc = new BasicVCConnection("10.117.160.99","root","vmware");
			vc.connect();
			log.info("Connection is ready");
			ServiceInstance service = vc.getServiceInstance();
			
			ManagedObjectReference vmMor = findVM(service, path);
			VirtualMachine vm = new VirtualMachine(service.getServerConnection(), vmMor);
			
			VirtualMachineTicket ticket = vm.acquireTicket(VirtualMachineTicketType.mks.toString());
				
			setVcversion(new VCVersion( vc.getServiceInstance().getAboutInfo()));
			
			
			log.debug("Connected to vCenter: version=" + getVcversion().versionString 
					+ ", build=" + getVcversion().build
					+ ", osType=" + getVcversion().osType);
			String uri;
			if (getVcversion().isNewerThanVC600()){
				uri = "/vsphere-client/webconsole/authd";
			}else{
				uri = "/console/authd";
			}
			uri = uri + "?vmId=" + vmMor.getVal() 
					+ "&host=" + ticket.host
					+ "&port=" + ticket.port
					+ "&mksTicket=" + ticket.ticket
					+ "&thumbprint=" + ticket.sslThumbprint
					+ "&cfgFile=" + URLEncoder.encode(ticket.cfgFile,encoding);
						
			
			ConsoleAccessInfoImpl ret = new ConsoleAccessInfoImpl("VCENTER");
			
			ret.setUri(uri);
			
			
			if (getVcversion().isWssSupported()) {
				ret.setProtocol("wss");
				if (getVcversion().isNewerThanVC600()){
					ret.setPort(9443);
				}else{
					ret.setPort(7343);
				}				
			} else {
				ret.setProtocol("ws");
				ret.setPort(7331);
			}
			ret.setHost(vc.getHost());
			return ret;

		} catch(Exception ex){
			ex.printStackTrace();
			log.error("Exception when calling vcenter",ex);
		}finally {
			if (vc != null) {
				vc.close();
			}
		}
		return null;
	}
	
	private ManagedObjectReference findVM(ServiceInstance service, String path) throws RuntimeFault, RemoteException {
		
		ServerConnection conn = service.getServerConnection();
		ServiceContent svcContent = service.getServiceContent();
		Boolean instanceUuid = null;
		
		if (getMajorVersion(svcContent) == 4) {
			// we look up VMs by BIOS UUID
			instanceUuid = Boolean.FALSE;
		}
		
		VimPortType vim = conn.getVimService();
//		
//		String uuid = "423878c3-68e9-02b7-2a0c-c594bcd2912b";
	//	ManagedObjectReference moRef = vim.findByUuid(svcContent.getSearchIndex(), null, vmUuid, true, instanceUuid);
		ManagedObjectReference moRef = vim.findByInventoryPath(svcContent.getSearchIndex(),  path);
		if (moRef == null)
			throw new RuntimeException("VM not found: " + path);
		
		return moRef;
	}



	public VCVersion getVcversion() {
		return vcversion;
	}



	public void setVcversion(VCVersion vcversion) {
		this.vcversion = vcversion;
	}






}




