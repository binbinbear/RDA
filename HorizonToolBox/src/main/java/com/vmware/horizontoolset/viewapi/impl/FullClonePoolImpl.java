package com.vmware.horizontoolset.viewapi.impl;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vdi.vlsi.binding.vdi.entity.VmTemplateId;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.VmTemplate.VmTemplateInfo;
import com.vmware.horizontoolset.report.SnapShotReport;
import com.vmware.horizontoolset.viewapi.FullClonePool;
import com.vmware.horizontoolset.viewapi.Template;
import com.vmware.horizontoolset.viewapi.ViewType;
@JsonIgnoreProperties(value={"template"})
public class FullClonePoolImpl  extends ViewPoolImpl implements FullClonePool{

	private static Logger log = Logger.getLogger(FullClonePoolImpl.class);
	//this is a cache to store all template
	
	
	private Template _template;
	
	public FullClonePoolImpl(DesktopInfo info, ViewQueryService service){
		super(info, service);
		
	}
	
	
	@Override
	public ViewType getViewType() {
		return ViewType.FullClone;
	}

	@Override
	public Template getTemplate() {
		if (this._template!=null){
			return this._template;
		}
		
		VmTemplateId tid = super.info.automatedDesktopData.virtualCenterProvisioningSettings.virtualCenterProvisioningData.template;
		//try cache first
		TemplateImpl result = Cache.getTemplate(tid.id);
		if (result!=null){
			log.debug("Cache hit, template cache great!!");
			result.addOrUpdateFullClonePool(this);
			return result;
		}
		log.debug("cache not hit, I have to query for tid:"+tid.id);
		//cache is not found, so query again
		VmTemplateInfo[] results=service.getTemplates(info.automatedDesktopData.virtualCenter);
		if (results == null){
			log.debug("no template is found");
			return null;
		}
	
		for (int i=0;i< results.length;i++){
			log.debug("add into cache:"+results[i].id.id);
		     Cache.addOrUpdateTemplate(results[i].id.id, new TemplateImpl(results[i]));
		}
		this._template =  Cache.getTemplate(tid.id);
		if (this._template != null){
			this._template.addOrUpdateFullClonePool(this);
		}
		log.debug("getTemplate result:"+ this._template);
		return this._template;

	}
	
	
	public String toString(){
		
		
		return super.toString() + this.getInformation();
	}


	@Override
	public String getInformation() {

		String information = "<table><tr><td>Data center</td> <td>Resource Pool </td> <td>VM Folder</td> </tr> <tr><td>" 
		+  super.info.automatedDesktopData.virtualCenterNamesData.datacenterPath  + 
				"</td><td>" +super.info.automatedDesktopData.virtualCenterNamesData.resourcePoolPath
				+"</td><td>" +  super.info.automatedDesktopData.virtualCenterNamesData.vmFolderPath
				+"</td></tr></table>";
			
		return information;
	}


	@Override
	public void upateReport(SnapShotReport report) {
		report.addOrUpdateTemplate(this.getTemplate());
	}

}
