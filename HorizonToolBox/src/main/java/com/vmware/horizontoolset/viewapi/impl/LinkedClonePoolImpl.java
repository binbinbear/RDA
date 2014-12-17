package com.vmware.horizontoolset.viewapi.impl;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vdi.vlsi.binding.vdi.entity.BaseImageSnapshotId;
import com.vmware.vdi.vlsi.binding.vdi.entity.BaseImageVmId;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADContainer.ADContainerInfo;
import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.BaseImageSnapshot.BaseImageSnapshotInfo;
import com.vmware.horizontoolset.report.SnapShotReport;
import com.vmware.horizontoolset.viewapi.Container;
import com.vmware.horizontoolset.viewapi.LinkedClonePool;
import com.vmware.horizontoolset.viewapi.SnapShot;
import com.vmware.horizontoolset.viewapi.VM;
import com.vmware.horizontoolset.viewapi.ViewType;


@JsonIgnoreProperties(value={"snapShot"})
public class LinkedClonePoolImpl extends ViewPoolImpl implements LinkedClonePool{

	public LinkedClonePoolImpl(DesktopInfo info, ViewQueryService service){
		super(info, service);

	}
	
	private static Logger log = Logger.getLogger(LinkedClonePoolImpl.class);
	
	
	@Override
	public ViewType getViewType() {
		// TODO Auto-generated method stub
		return ViewType.LinkedClone;
	}

	
	
	private SnapShot _snapshot;
	@Override
	public SnapShot getSnapShot() {
		if (this._snapshot!=null){
			return this._snapshot;
		}
		BaseImageVmId vmid = super.info.automatedDesktopData.virtualCenterProvisioningSettings.virtualCenterProvisioningData.parentVm;
		BaseImageSnapshotId sid = super.info.automatedDesktopData.virtualCenterProvisioningSettings.virtualCenterProvisioningData.snapshot;
		String snapShotFullPath = super.info.automatedDesktopData.virtualCenterNamesData.snapshotPath;
		if (vmid == null){
			log.warn("No Parent VM for pool:"+this.getName());
			return null;
		}
		log.debug("Snapshot full path:"+snapShotFullPath +" vmid:"+vmid.id);
		if (Cache.getSnapShot(vmid.id,snapShotFullPath)!=null){
			log.debug("Great, snapshot cache hit!");
			this._snapshot = Cache.getSnapShot(vmid.id, snapShotFullPath);
			this._snapshot.addOrUpdateLinkedClonePool(this);
			return this._snapshot;
		}
		
		
		log.debug("Cache not hit, I have to query all snapshots and images:"+this.getName());
		
		
		VM vm = super.service.getVM(vmid,  super.info.automatedDesktopData.virtualCenterNamesData.parentVmPath) ;
		
		BaseImageSnapshotInfo[] snapshots = super.service.getSnapShots(vmid);
		
		if (snapshots == null || snapshots.length == 0){
			return null;
		}
		for (int i=0;i<snapshots.length;i++){
			
			if (Cache.getSnapShot(vmid.id, snapshots[i].path) == null){
				log.debug("Add snapshot into Cache:"+ snapshots[i].path);
				Cache.addOrUpdateSnapShot(vmid.id, snapshots[i].path, new SnapShotImpl(snapshots[i], vm));
			}else{
				log.debug("Ignore snapshot since it's in Cache:"+ snapshots[i].path);
			}
			
		}
		 this._snapshot = Cache.getSnapShot(vmid.id, snapShotFullPath);
		 if (this._snapshot!=null){
			 log.debug("Snapshot "+ this._snapshot.getName()+ " is found for " + this.getName());
			 this._snapshot.addOrUpdateLinkedClonePool(this);
		 }else{
			 log.warn("No snapshot is found from vCenter, so this should be an unknown snapshot!!!!! for:"+ this.getName());
			 //snapshot is null, so we use the sid to create a new snapshot
			 BaseImageSnapshotInfo snapshotinfo = new BaseImageSnapshotInfo();
			 snapshotinfo.id = sid;
			 snapshotinfo.name = "";
			 snapshotinfo.path = snapShotFullPath;
			 SnapShotImpl anonymSnapShot = new SnapShotImpl(snapshotinfo, vm);
			 Cache.addOrUpdateSnapShot(vmid.id, snapshotinfo.path,anonymSnapShot);
			 this._snapshot = anonymSnapShot;
			 this._snapshot.addOrUpdateLinkedClonePool(this);
		 }
		return  this._snapshot;
	}
	
	
	public String toString(){
		
		return super.toString()+ this.getInformation();
	}


	@Override
	public String getInformation() {

		String information = "<table><tr><td>Data center</td> <td>Host </td> <td>VM Folder</td> </tr> <tr><td>" 
		+  super.info.automatedDesktopData.virtualCenterNamesData.datacenterPath  + 
				"</td><td>" +super.info.automatedDesktopData.virtualCenterNamesData.hostOrClusterPath
				+"</td><td>" +  super.info.automatedDesktopData.virtualCenterNamesData.vmFolderPath
				+"</td></tr></table>";
			
		return information;
	}


	@Override
	public void upateReport(SnapShotReport report) {
		SnapShot snapShot = this.getSnapShot();
		if (snapShot == null){
			log.warn("How can a linked clone pool has no snapshot!!!!");
			return;
		}
		VM thisvm = snapShot.getParentVM();
		if (thisvm==null){
			log.warn("why linked clone pool has not vm? :" + this.getName());
			return;
		}
		
		report.addOrUpdateVM(thisvm);
		
	}

	private Container _container;
	
	@Override
	public Container getADContainer() {
		if (this._container==null){
			log.info("start to get ad container");
			ADContainerInfo adinfo = super.service.getContainer(this.info.automatedDesktopData.customizationSettings);
			if (adinfo != null){
				this._container = new ContainerImpl(adinfo);
			}
		}
		return this._container;
	}

}
