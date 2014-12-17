package com.vmware.horizontoolset.viewapi.impl;

import com.vmware.horizontoolset.viewapi.Domain;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADDomain.ADDomainInfo;

public class DomainImpl implements Domain{

	private String dnsName;
	private String netbiosName;
	public DomainImpl(ADDomainInfo domainInfo){
		this.dnsName = domainInfo.getDnsName();
		this.netbiosName = domainInfo.getNetBiosName();
		
	}
	@Override
	public String getDNSName() {
		return this.dnsName;
	}

	@Override
	public String getNetBiosName() {
		return this.netbiosName;
	}


}
