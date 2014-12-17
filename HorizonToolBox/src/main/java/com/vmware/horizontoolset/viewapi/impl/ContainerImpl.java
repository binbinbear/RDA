package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.Container;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADContainer.ADContainerInfo;

public class ContainerImpl implements Container{

	private String rdn;
	public ContainerImpl(ADContainerInfo info){
		this.rdn = info.getRdn();
	}
	@Override
	public String getRDN() {
		return this.rdn;
	}

}
