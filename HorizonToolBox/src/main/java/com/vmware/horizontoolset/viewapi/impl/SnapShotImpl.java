package com.vmware.horizontoolset.viewapi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.BaseImageSnapshot.BaseImageSnapshotInfo;
import com.vmware.horizontoolset.viewapi.LinkedClonePool;
import com.vmware.horizontoolset.viewapi.SnapShot;
import com.vmware.horizontoolset.viewapi.VM;

@JsonIgnoreProperties(value={"parentVM", "parentSnapShot", "allDescendantSnapShots"})
public class SnapShotImpl implements SnapShot{
	private static Logger log = Logger.getLogger(SnapShotImpl.class);
	@Override
	public VM getParentVM() {
		// TODO Auto-generated method stub
		return this.parentVM;
	}
	private String path;
	private VM parentVM;
	private String name;
	
	
	private String getNameFromPath() {
			
			String[] paths = this.path.split("/");
			if (paths.length>0){
				return paths[paths.length-1];
			}
			
			return "";
			
		
	}

	
	//Do not new SnapShotImpl by yourself, I don't want two snapshots have the same path
	//refer to getSnapShot in LinkedClonePoolImpl
	SnapShotImpl(BaseImageSnapshotInfo info, VM vm){
		this.path = info.path;
		this.name = info.name;
		this.parentVM = vm;
		if (this.name == null || this.name.length() == 0){
			this.name = getNameFromPath();
		}
		vm.addOrUpdateSnapShot(this);
	}
	


	@Override
	public String getPath() {
		return this.path;
	}
	
	
	public String toString(){
		return this.path+ "; ParentVM:" + this.parentVM.getFullName();
	}


	private HashMap<String, LinkedClonePool> pools = new HashMap<String,LinkedClonePool>();
	@Override
	public Collection<LinkedClonePool> getLinkedClonePools() {
		log.debug("Pools for snapshot "+ this.getName()+" are:" + pools.size());
		return pools.values();
	}



	@Override
	public void addOrUpdateLinkedClonePool(LinkedClonePool pool) {
		log.debug("update pool for snapshot "+ this.getName()+ " pool:"+pool.getName());
		pools.put(pool.getName(), pool);
		log.debug("after put in map, size :"+ pools.size());
		
	}

	private SnapShot parentSnapShot;
	public void setParentSnapShot(SnapShot parentSnapShot) {
		this.parentSnapShot = parentSnapShot;
	}
	private HashMap<String, SnapShot> children = new HashMap<String,SnapShot>();
	
	@Override
	public SnapShot getParentSnapShot() {
		// TODO Auto-generated method stub
		return 	parentSnapShot;
	}



	@Override
	public Collection<SnapShot> getChildrenSnapShots() {
		return children.values();
	}
	
	private void _addChild(SnapShot snapshot){
		snapshot.setParentSnapShot(this);
		children.put(snapshot.getPath(), snapshot);
	}
	
	public void addOrUpdateChildSnapShot(SnapShot snapshot){
		
		String path = snapshot.getPath();
		if (children.containsKey(path)){
			//update directly
			this._addChild(snapshot);
			return;
		}
		//check children relation ship
		for(String key: children.keySet()){

			SnapShot child = children.get(key);
			if (child.isChildOf(snapshot)){
				//replace child with shot, and then add child to shot's children
				this._addChild(snapshot);
				children.remove(key);
				snapshot.addOrUpdateChildSnapShot(child);
				return;
			}else if (snapshot.isChildOf(child)){
				child.addOrUpdateChildSnapShot(snapshot);
				return;
			}
		}
		this._addChild(snapshot);
	}



	@Override
	public boolean isChildOf(SnapShot other) {
			
			String[] otherpath = other.getPath().split("/");
			String[] thispath = path.split("/");
			if (thispath.length <= otherpath.length){
				return false;
			}
			
			for (int i=0;i<otherpath.length;i++){
				if (!thispath[i].equals(otherpath[i])){
					return false;
				}
			}
			return true;
		
	}



	@Override
	public Collection<SnapShot> getAllDescendantSnapShots() {
		Collection<SnapShot> current = this.getChildrenSnapShots();
		ArrayList<SnapShot> all = new ArrayList<SnapShot>();
		all.addAll(current);
		for (SnapShot one: current){
			Collection<SnapShot> some = one.getAllDescendantSnapShots();
			if (some.size()>0){
				all.addAll(some);
			}
		}
		return all;
	}



	@Override
	public String getName() {
		if (this.name==null){
			
			return this.getPath();
		}
		return this.name;
	}


	@Override
	public boolean isNotInUse() {
		//if this snapshot has a pool, this is in use;
		if (this.pools.size()>0){
			return false;
		}
		
		return true;
	}



}
