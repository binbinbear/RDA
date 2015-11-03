package com.vmware.horizontoolset.power;

import java.util.List;

import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;
import com.vmware.horizontoolset.util.StringUtil;

public class PowerOnScheduleStorage {
	
	private static final String key = PowerOnScheduleStorage.class.getSimpleName();
	public static List<PowerOnJob> getAllPowerOnJobs(){
		
		return null;
	}
	
	
	public static boolean addJob(PowerOnJob job){
		return true;
	}
	
	public static boolean removeJob(PowerOnJob job){
		return true;
	}

	

}
