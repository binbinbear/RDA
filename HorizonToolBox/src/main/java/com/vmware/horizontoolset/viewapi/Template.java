package com.vmware.horizontoolset.viewapi;

import java.util.Collection;

public interface Template {
	public String getPath();
	public void addOrUpdateFullClonePool(FullClonePool pool);
	public Collection<FullClonePool> getFullClonePools();

}
