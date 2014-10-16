package com.vmware.horizontoolset.report;

import java.util.Collection;
import java.util.HashMap;

import com.vmware.horizontoolset.viewapi.Template;
import com.vmware.horizontoolset.viewapi.VM;

public class SnapShotReport extends AbstractReport{
	private HashMap<String, VM> vms;
	private HashMap<String, Template> templates;
	
	public Collection<VM> getVms() {
		return vms.values();
	}
	
	public Collection<Template> getTemplates() {
		return templates.values();
	}
	

	public void addOrUpdateTemplate(Template template){
		if (template!=null){
			this.templates.put(template.getPath(), template);
		}
		
	}

	public void addOrUpdateVM(VM vm){
		if (vm!=null){
			this.vms.put(vm.getFullName(),vm);
		}
		
	}

	public SnapShotReport(){
		this.vms = new HashMap<String, VM>();
		this.templates = new  HashMap<String, Template>();
	}
	

	



}
