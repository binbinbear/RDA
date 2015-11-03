package com.vmware.horizontoolset.power;


import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.horizontoolset.console.VMServiceImplVCenter;
import com.vmware.horizontoolset.util.CronJob;

import com.vmware.horizontoolset.util.TaskModuleUtil;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.operator.Machine;
import com.vmware.horizontoolset.viewapi.operator.ViewOperator;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;
import com.vmware.vdi.vlsi.binding.vdi.resources.Machine.MachineInfo;

@JsonIgnoreProperties(value={"log"})
public class PowerOnJob implements CronJob{

	private String poolName;
	private String cron;
	private static Logger log = Logger.getLogger(PowerOnJob.class);
	
	private int interval = 5000;
	public PowerOnJob(String poolName, String cron, int startInterval){
		this.poolName = poolName;
		this.cron = cron;
		this.interval = startInterval;
	}
	
	public void setCron(String cron){
		this.cron = cron;
	}
	

	
	@Override
	public void execute()  {
		ViewOperator operator=null;
		ViewAPIService api = null;
    	try {
    		api = TaskModuleUtil.getViewAPIService(null);
    		if (api==null){
    			log.error("Cant' get api service");
    			return;
    		}
    		
    		operator = new ViewOperator(api.getConn());
    		VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();
    		com.vmware.horizontoolset.viewapi.operator.DesktopPool pool = operator.getDesktopPool(this.poolName);
    		
    		if (pool == null){
    			log.error("Can't find the pool with name:"+ this.poolName);
    			return;
    		}
    		List<Machine> machines = pool.machines.get(); 
    		
    		if (machines==null || machines.size() == 0){

    			log.error("Can't find vm in the pool:"+ this.poolName);
    			return;
    			
    		}
    		
    		String vcname=null;
    		for (Machine m: machines){
    			MachineInfo minfo = api.getMachineInfo(m.getVmid());
    	    	if (minfo == null){
    	    		log.warn("Can not find vminfo for vm:"+m.getName());
    	    		continue;
    	    	}

    	    	
	    		if( vcname == null){
	    			vcname =  api.getVCInfo(m.getVcenterId()).serverSpec.serverName;
	    		}
	    		
	    		if (vcname == null){
    	    		log.warn("Can not find vc for pool:"+this.poolName);
    	    		return;
	    		}
	    		
	    		VMServiceImplVCenter vmservice = new VMServiceImplVCenter(vdiCtx , vcname , minfo.managedMachineData.getVirtualCenterData().path);
	    		vmservice.poweron();
	    		
	    		Thread.sleep(getInterval());
    		}
    		api.close();

    	}catch(Exception ex){
    		log.warn("Exception", ex);
    		
    	}finally{
    		if (operator!=null){
    			operator.close();
    		}    		
    	}
		
	}

	@Override
	public String getCron() {
		
		return cron;
	}

	public int getInterval() {
		return interval;
	}

	@Override
	public String getKey() {
		return this.poolName;
	}




}
