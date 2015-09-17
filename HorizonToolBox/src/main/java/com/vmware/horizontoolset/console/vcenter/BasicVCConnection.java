/*
 * ******************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * ******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package  com.vmware.horizontoolset.console.vcenter;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.adam.AdamVCManager;
import com.vmware.vdi.adamwrapper.exceptions.ADAMServerException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.objects.VC;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * This simple object shows how to set up a vSphere connection as it was done in vSphere 4.x and is provided
 * as a reference for anyone working with older vSphere servers that do not support modern SSO features.
 * It is intended as a utility class for use by Samples that will need to connect before they can do anything useful.
 * This is a light weight POJO that should be very easy to reuse later.
 * <p/>
 * Samples that need a connection open before they can do anything useful extend ConnectedVimServiceBase so that the
 * code in those samples can focus on demonstrating the feature at hand. The logic of most samples will not be
 * changed by the use of the BasicConnection or the SsoConnection.
 * </p>
 *
 * @see ConnectedVimServiceBase
 */
public class BasicVCConnection implements VCConnection {
	
	private static Logger log = Logger.getLogger(BasicVCConnection.class);
	
    private ServiceInstance serviceInstance;

    private String username;
    private String password;
    private String url;
    private String host;
    
    public BasicVCConnection(VDIContext ctx,
            String vcname){

    	try {
            VC thevc = null;
            List<VC> vcs = AdamVCManager.getInstance().getAll(ctx);
            for (VC vc:vcs){
            	if (vc.getServerName().equalsIgnoreCase(vcname)){
            		thevc = vc;
            		log.info("Found vc:"+vc.getServerName());
            		break;
            	}
            }
			
			this.username = thevc.getUserName();
			this.password = thevc.getPassword();
			this.url = thevc.getUrl();
			this.host = thevc.getServerName();
			
		} catch (ADAMServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    //never use this function
    // this is for local test only
    public BasicVCConnection(String host, String username, String password){
    		this.username = username;
    		this.password = password;
    		this.url = "https://"+host+"/sdk/vimService";
    		this.host = host;
    }

    public String getHost() {
        return this.host;
    }

   
    
    public VCConnection connect() {
        if (!isConnected()) {
            try {
                _connect();
            } catch (Exception e) {
            	log.warn("Error connect.", e);
                Throwable cause = (e.getCause() != null)?e.getCause():e;
                throw new ConnectionException(
                        "failed to connect: " + e.getMessage() + " : " + cause.getMessage(),
                        cause);
            }
        }
        return this;
    }
    UserSession userSession;
    

	private void _connect() throws RemoteException, MalformedURLException, ADAMServerException  {

		
		this.serviceInstance = new ServiceInstance( new URL(this.url), this.username,this.password, true);
        userSession = serviceInstance.getSessionManager().getCurrentSession();

    }


    public boolean isConnected() {
        if (userSession == null) {
            return false;
        }
        long startTime = userSession.getLastActiveTime().getTime().getTime();

        // 30 minutes in milliseconds = 30 minutes * 60 seconds * 1000 milliseconds
        return new Date().getTime() < startTime + 30 * 60 * 1000;
    }

    public VCConnection disconnect() {
        if (this.isConnected()) {
            try {
            	serviceInstance.getSessionManager().logout();
            } catch (Exception e) {
                Throwable cause = e.getCause();
                throw new ConnectionException(
                        "failed to disconnect properly: " + e.getMessage() + " : " + cause.getMessage(),
                        cause
                );
            } finally {
                // A connection is very memory intensive, I'm helping the garbage collector here
                userSession = null;
                serviceInstance = null;
            }
        }
        return this;
    }

	@Override
	public ServiceInstance getServiceInstance() {
		return this.serviceInstance;
	}
	
	public void close() {
		disconnect();
	}
}
