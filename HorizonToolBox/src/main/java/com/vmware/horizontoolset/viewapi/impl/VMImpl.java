package com.vmware.horizontoolset.viewapi.impl;

import java.util.Collection;
import java.util.HashMap;

import com.vmware.horizontoolset.viewapi.SnapShot;
import com.vmware.horizontoolset.viewapi.VM;

public class VMImpl implements VM{

	private String _path;
	
	private HashMap<String, SnapShot> content = new HashMap<String, SnapShot>();
	

	VMImpl(String vmpath){
		this._path = vmpath;
	}
	@Override
	public Collection<SnapShot> getChildren() {
		return content.values();
	}

	@Override
	public String getFullName() {
		return this._path;
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
		String information = "<table><tr><td>VM Path</td><tr><td> " + this._path 
				+"</td></tr></table>";              
			
		return information;
	}

}
