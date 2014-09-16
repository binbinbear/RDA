package com.vmware.horizontoolset.viewapi.impl;

import java.util.Collection;
import java.util.HashMap;

import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.BaseImageVm.BaseImageVmInfo;
import com.vmware.horizontoolset.viewapi.SnapShot;
import com.vmware.horizontoolset.viewapi.VM;

public class VMImpl implements VM{

	private BaseImageVmInfo _info;
	
	private HashMap<String, SnapShot> content = new HashMap<String, SnapShot>();
	
	//don't crate new VMImpl by yourself, I don't want two VM have the same VMId
	//refer to getVM in DesktopService
	VMImpl(BaseImageVmInfo vminfo){
		this._info = vminfo;
	}
	@Override
	public Collection<SnapShot> getChildren() {
		return content.values();
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return this._info.path;
	}
	@Override
	public void addOrUpdateSnapShot(SnapShot shot) {
		String path = shot.getPath();
		if (content.containsKey(path)){
			//update directly
			content.put(shot.getPath(), shot);
			return;
		}
		//check children relation ship
		for(String key: content.keySet()){

			SnapShot child = content.get(key);
			if (child.isChildOf(shot)){
				//replace child with shot, and then add child to shot's children
				content.put(shot.getPath(), shot);
				content.remove(key);
				shot.addOrUpdateChildSnapShot(child);
				return;
			}else if (shot.isChildOf(child)){
				child.addOrUpdateChildSnapShot(shot);
				return;
			}
		}
		content.put(shot.getPath(), shot);
		
		
	}
	@Override
	public String getInformation() {
		String information = "<table><tr><td>VM Path</td> <td>VM Name </td> <td>OS Name</td> </tr> <tr><td>" + this._info.path + 
				"</td><td>" + this._info.name
				+"</td><td>" + this._info.operatingSystem
				+"</td></tr></table>";
			
		return information;
	}

}
