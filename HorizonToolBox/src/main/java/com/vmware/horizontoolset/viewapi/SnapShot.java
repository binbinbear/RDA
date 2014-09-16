package com.vmware.horizontoolset.viewapi;

import java.util.Collection;


public interface SnapShot{
	/**
	 * 
	 * @return parent of this SnapShot, this may be a VM or a SnapShot
	 */
	public VM getParentVM();
	public SnapShot getParentSnapShot();
	public Collection<SnapShot> getChildrenSnapShots();
	
	
	public Collection<SnapShot> getAllDescendantSnapShots();
	
	
	
	public String getPath();
	
	public String getName();
	
	public Collection<LinkedClonePool> getLinkedClonePools();
	
	public void addOrUpdateLinkedClonePool(LinkedClonePool pool);
	
	public void setParentSnapShot(SnapShot parentSnapShot) ;
	
	public void addOrUpdateChildSnapShot(SnapShot snapshot);
	
	
	public boolean isChildOf(SnapShot other);
	
	public boolean isNotInUse();
}
