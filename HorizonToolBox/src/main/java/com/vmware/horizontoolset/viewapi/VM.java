package com.vmware.horizontoolset.viewapi;

import java.util.Collection;

public interface VM {
	public Collection<SnapShot> getChildren();
	public String getFullName();
	public void addOrUpdateSnapShot(SnapShot shot);
	public String getInformation();
}
