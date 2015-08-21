package com.vmware.horizontoolset.policy.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.policy.config.ItemConfig;
import com.vmware.horizontoolset.policy.config.PolicyConfig;
import com.vmware.horizontoolset.policy.config.ProfileConfig;
import com.vmware.horizontoolset.policy.model.PolicyModel;
import com.vmware.horizontoolset.policy.model.ProfileModel;
import com.vmware.horizontoolset.policy.polfile.PolFile;
import com.vmware.horizontoolset.policy.service.PolFileService;
import com.vmware.horizontoolset.util.JsonUtil;

public class PolFileServiceImpl implements PolFileService{
	private static ProfileConfig  polConfig;
	public final static String polPath = getRootPath()+"/resources/policies/";
	private final static String configFilePath = getRootPath()+"/resources/polFiles/PolicyConfig.json";
	
	private static Logger log = Logger.getLogger(PolFileServiceImpl.class);
	
	private ProfileServiceImpl profileServiceImpl;
	
	public PolFileServiceImpl(){
		log.debug("[DEBUG ] polPath = " + polPath);
		log.debug("[DEBUG ] configFilePath = " + configFilePath);
		loadPolicyConfigFile();
		profileServiceImpl = new ProfileServiceImpl();
	}
	
	public boolean createPolFile(String profileName){
		PolFile pf = new PolFile();
		String profileStr = profileServiceImpl.getProfileFromLdap(profileName);
		
		if( profileStr!= null){
			//log.debug("[DEBUG createPolFile] " + profileStr);;
			ProfileModel prof = JsonUtil.jsonToJava(profileStr, ProfileModel.class);
			setPolPolicy(prof, pf);
			pf.save( polPath+profileName+".pol" );
			log.debug("[DEBUG ] create pol success. file:"+profileName+".pol");
		}else{
			log.debug("[DEBUG ] profileStr==null ");
			return false;
		}
		return true;
	}
	
	public void deletePolFile(String profileName){	
		File polFile = new File(polPath + profileName + ".pol");
		log.debug("[DEBUG ] [remove] " + polPath + profileName + ".pol");
		if(polFile.isFile()){
			log.debug("[DEBUG ] delete polFile " + profileName + ".pol");
			polFile.delete();
		}
	}
	
	private void setPolPolicy(ProfileModel prof, PolFile pf){
		for(int i=0; i<prof.policies.length; i++){
			policyHandler(prof.policies[i], pf);	// for each policy
		}
	}
	
	private void policyHandler(PolicyModel policyModel, PolFile pf) {
		// 根据policiId 从配置文件中得到对应的policy配置项
		// 检查是 enabled 还是  disabled， disabled当做not configured处理
		// 对map中的每个item， 从policy配置项里取得数据
		log.debug("[DEBUG ] [policyHandler] policyModel=" + policyModel.toString());
		if( policyModel.enabled == 2 ){       //not configured
			log.debug("[DEBUG ] enabled == 2");
			return;
		}
		else if( policyModel.enabled == 1 ){	 //disabled
			log.debug("[DEBUG ] enabled == 1");
			policyDisabledHandler(policyModel, pf);
			return;
		}
		else if( policyModel.enabled == 0 ){  //enabled
			policyEnabledHandler(policyModel, pf);
		}	
	}
	
	private void policyEnabledHandler(PolicyModel policyModel, PolFile pf) {
		PolicyConfig pConf = polConfig.getPolicy(policyModel.policyId);	//get Policy Configure Information
		if( null!=pConf ){
			log.debug("[DEBUG ] pConf=" + pConf.toString());
		}			
		else{
			log.debug("[DEBUG ] [pConf==null] !!!  policyId="+policyModel.policyId);
			return;
		}
		
		if(pConf.items!=null){	// with subItems
			Map<String,String> itemMap = policyModel.items;
			for( Map.Entry<String, String> mapEntry : itemMap.entrySet() ){
				String itemId = mapEntry.getKey();
				String itemVal = mapEntry.getValue();
				log.debug("[DEBUG ] itemId="+itemId+", itemVal="+itemVal);
				ItemConfig itemEntry = pConf.getItemEntry(itemId);
				if(itemEntry != null){
					itemEntry.data = itemVal; 
					itemHandler(itemEntry, pf);
				}else{
					log.debug(" itemEntry==null !!!");
				}
			}
			// handle Addition and Title [enable]
			for( Map.Entry<String, ItemConfig> itemEntry : pConf.items.entrySet() ){
				ItemConfig itemConfig = itemEntry.getValue();
				switch(itemConfig.elementType){
					case ELE_GRID_TITLE:
						pf.setString(itemConfig.keyName, itemConfig.valueName, "");
						break;
					case ELE_ADDITION:
						log.debug("[DEBUG ] [case ELE_ADDITION] " + itemConfig.valueName + ", " + itemConfig.data);
						pf.setDWORD(itemConfig.keyName, itemConfig.valueName, Integer.parseInt(itemConfig.defaultData));	//TODO defaultData/data
						log.debug("[DEBUG ] [case ELE_ADDITION] over");
						break;
					case ELE_ADDITION_0:
						pf.setDWORD(itemConfig.keyName, itemConfig.valueName, 0);
						break;
					default:
				}
			}
			
		}else{	// no sunItems
			log.debug("[DEBUG ] [policyHandler] hasOptions=false");
			switch(pConf.pType){
				case USB:
					pf.setString(pConf.policyKey, pConf.policyValue, "true");
					break;
				case COMMON:
					pf.setString(pConf.policyKey, pConf.policyValue, "true");
					break;
				case DEF:
					if(!pConf.reverse)
						pf.setDWORD(pConf.policyKey, pConf.policyValue, 0);
					else
						pf.setDWORD(pConf.policyKey, pConf.policyValue, 1);
					break;
				default:
			}
		}
	}
	
	private void policyDisabledHandler(PolicyModel policyTest, PolFile pf) {
		PolicyConfig pConf = polConfig.getPolicy(policyTest.policyId);
		if( pConf == null ){
			log.debug("pConf==null !!");
			return;
		}
		if( pConf.items != null ){ 
			Map<String,ItemConfig> itemMap = pConf.items;
			for( Map.Entry<String, ItemConfig> mapEntry : itemMap.entrySet() ){
				ItemConfig itemconf = mapEntry.getValue();
				itemDisabledHandler(itemconf, pf);
			}
		}else{
			log.debug("[DEBUG ] k=" + pConf.policyKey + ", v=" + pConf.policyValue);
			switch(pConf.pType){
				case USB:
					pf.setString(pConf.policyKey, pConf.policyValue, "false");
					break;
				case COMMON:
					pf.setString(pConf.policyKey, pConf.policyValue, "false");
					break;
				case DEF:
					if(!pConf.reverse)
						pf.setDWORD(pConf.policyKey, pConf.policyValue, 1);
					else
						pf.setDWORD(pConf.policyKey, pConf.policyValue, 0);
					break;
				default:
			}	
		}
	}

	private void itemHandler(ItemConfig itemEntry, PolFile pf){
		//log.debug("[DEBUG ] [ItemConfig] "+itemEntry.toString());
		switch(itemEntry.elementType){
			case ELE_GRID1:	//GRID1		没有Attention项， Value和Data不一致
				log.debug("[DEBUG ] [GRID1]");
				String girdArrayStr = itemEntry.data;
				String[] gridValArray = girdArrayStr.split(";");
				log.debug("[grid] str=" + gridValArray.toString());
				for(int i=1; i<=gridValArray.length; i++){
					log.debug("[grid1] " + gridValArray[i-1]);
					pf.setString(itemEntry.keyName, "Command"+i, gridValArray[i-1]);
				}
				return;
			case ELE_GRID2: //GRID2		有Attention项， Value和Data一致
				String girdArrayStr2 = itemEntry.data;
				String[] gridValArray2 = girdArrayStr2.split(";");
				log.debug("[grid2] str=" + gridValArray2.toString());
				for(int i=1; i<=gridValArray2.length; i++){
					log.debug("[grid2] " + gridValArray2[i-1]);
					pf.setString(itemEntry.keyName, gridValArray2[i-1], gridValArray2[i-1]);
				}
				return;
			case ELE_CHECKBOX_V:
				if(itemEntry.data.equals("0")){ //check
					pf.setDWORD(itemEntry.keyName, itemEntry.valueName, Integer.parseInt(itemEntry.defaultData));
				}else if((itemEntry.data.equals("1"))){ //uncheck
					pf.setDWORD(itemEntry.keyName, itemEntry.valueName, 0);
				}else{
					log.debug("[DEBUG ] ELE_CHECKBOX_V");
				}
				return;
			default:
		}
		
		//对每项单独处理，直接写入 polFile
		//根据不同的type
		//log.debug("[DEBUG ] [itemHandler]"+itemEntry.toString());
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
	
	private void itemDisabledHandler(ItemConfig itemEntry, PolFile pf){
		//log.debug("[DEBUG lyx] [ItemConfig] "+itemEntry.toString());
		switch(itemEntry.elementType){
			case ELE_CHECKBOX:
				pf.setDWORD(itemEntry.keyName, itemEntry.valueName, 0);
				break;
			case ELE_CHECKBOX_V:
				pf.setDWORD(itemEntry.keyName, itemEntry.valueName, 0);
				break;
			case ELE_INPUTBOX:
				pf.setString(itemEntry.keyName, "**del." + itemEntry.valueName, ""); //不能为null
				break;
			case ELE_SELECTBOX:
				pf.setString(itemEntry.keyName, "**del." + itemEntry.valueName, "");
				break;
			case ELE_GRID1:
			case ELE_GRID2:
			case ELE_ADDITION:
				pf.setDWORD(itemEntry.keyName, itemEntry.valueName, 0);
				break;
			case ELE_ADDITION_0:
				log.debug("[DEBUG ] itemEntry.defaultData="+itemEntry.defaultData);
				pf.setDWORD(itemEntry.keyName, itemEntry.valueName, Integer.parseInt(itemEntry.defaultData));
				log.debug("[DEBUG ] getDWORD="+pf.getDWORD(itemEntry.keyName, itemEntry.valueName));;
				break;
			case ELE_GRID_TITLE:
				pf.setString(itemEntry.keyName, itemEntry.valueName, "");
				break;
			default:
				
		}
	}
	
	private void loadPolicyConfigFile(){
		try {
			polConfig = JsonUtil.load(configFilePath, ProfileConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("[DEBUG ] [loadPolicyConfigFile] load success! :: " /* + polConfig.toString() */ );
	}
	
	private static String getRootPath(){
        String result = PolFileServiceImpl.class.getResource("PolFileServiceImpl.class").toString();  
        System.out.println("result= "+result);
        	
        int index = result.indexOf("WEB-INF");    
        if(index == -1){    
        	index = result.indexOf("bin");    
        }
        System.out.println("index="+index);
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
