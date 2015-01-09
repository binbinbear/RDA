package com.vmware.horizontoolset.policy.service;

import java.io.IOException;
import java.util.Map;

import com.vmware.horizontoolset.policy.config.ItemConfig;
import com.vmware.horizontoolset.policy.config.PolicyConfig;
import com.vmware.horizontoolset.policy.config.ProfileConfig;
import com.vmware.horizontoolset.policy.model.PolicyModel;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.policy.polfile.PolFile;
import com.vmware.horizontoolset.util.JsonUtil;
import com.vmware.horizontoolset.util.SharedStorageAccess;

public class PolFileService {
	private static ProfileConfig  polConfig;
	
	public PolFileService(){
		loadPolicyConfigFile();
	}
	
	public void createPolFile(String profileName){
		PolFile pf = new PolFile();
		String profileStr = SharedStorageAccess.get(profileName);
		ProfileModel prof = JsonUtil.jsonToJava(profileStr, ProfileModel.class);
		setPolPolicy(prof, pf);
		pf.save( "c:\\temp\\"+profileName+".pol" );
	}
	
	private void policyHandler(PolicyModel policyTest, PolFile pf) {	//处理policy
		// 根据policiId 从配置文件中得到对应的policy配置项
		// 检查是 enabled 还是  disabled， disabled当做not configured处理
		// 对map中的每个item， 从policy配置项里取得数据

		if( policyTest.enabled == 1 ){	//if disabled
			policyHandlerDisabled(policyTest, pf);
			return;
		}
		
		PolicyConfig pConf = polConfig.getPolicy(policyTest.PolicyId);	//得到Policy配置文件中的信息
		Map<String,String> itemMap = policyTest.items;	//HashMap
		for( Map.Entry<String, String> mapEntry : itemMap.entrySet()){
			String itemId = mapEntry.getKey();
			String itemVal = mapEntry.getValue();
			
			ItemConfig itemEntry = pConf.getItemEntry(itemId);
			itemEntry.data = itemVal;
			itemHandler(itemEntry, pf);
		}
	}
	
	private void policyHandlerDisabled(PolicyModel policyTest, PolFile pf) {	//处理policy是disabled 的情况
		// TODO 如果没有“暂存用户配置”的需求，则该方法不需要实现
		PolicyConfig pConf = polConfig.getPolicy(policyTest.PolicyId);
		if( pConf.hasOptions ){  //有子配置项，则对每个子项进行disabled处理
			
		}else{	//子配置项为空, 从配置文件中得到Policy的KeyName，ValueName
			
		}
	}

	private void itemHandler(ItemConfig itemEntry, PolFile pf){
		//对每项单独处理，直接写入 polFile
		//根据不同的type
		System.out.println("[DEBUG type]"+itemEntry.type);
		switch(itemEntry.type){
			case REG_DWORD:
				pf.setDWORD(itemEntry.keyName, itemEntry.valueName, Integer.parseInt(itemEntry.data));
				break;
			case REG_SZ:
				pf.setString(itemEntry.keyName, itemEntry.valueName, itemEntry.data);
				break;
			default:
				// TODO
		}
	}
	
	private void setPolPolicy(ProfileModel prof, PolFile pf){
		for(int i=0; i<prof.policies.length; i++){
			policyHandler(prof.policies[i], pf);
		}
	}
	
	private void loadPolicyConfigFile(){
		String configFilePath = getRootPath()+"/resources/polFiles/PolicyConfig.json";
		try {
			polConfig = JsonUtil.load(configFilePath, ProfileConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getRootPath(){
        String result = PolFileService.class.getResource("PolFileService.class").toString();  
        System.out.println("result= "+result);
        	
        int index = result.indexOf("WEB-INF");    
        if(index == -1){    
        	index = result.indexOf("bin");    
        }
  
        result = result.substring(0,index);    
        if(result.startsWith("jar")){    
            // 当class文件在jar文件中时，返回"jar:file:/F:/ ..."样的路径     
            result = result.substring(10);    
        }else if(result.startsWith("file")){    
            // 当class文件在class文件中时，返回"file:/F:/ ..."样的路径     
            result = result.substring(6);    
        }    
        if(result.endsWith("/") )
        	result = result.substring(0,result.length()-1);//不包含最后的"/" 
        //替换 %20
        result = result.replaceAll("%20", " ");
        return result;    
    }
	
}
