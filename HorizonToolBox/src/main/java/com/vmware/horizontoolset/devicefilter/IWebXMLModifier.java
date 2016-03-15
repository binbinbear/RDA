package com.vmware.horizontoolset.devicefilter;

public interface IWebXMLModifier {

	public boolean hasToolboxFilter();

	public void insertToolboxFilter();

	public void removeToolboxFilter();
}
