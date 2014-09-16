package com.vmware.horizontoolset.viewapi.impl;

import java.util.Collection;
import java.util.HashMap;

import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.VmTemplate.VmTemplateInfo;
import com.vmware.horizontoolset.viewapi.FullClonePool;
import com.vmware.horizontoolset.viewapi.Template;

public class TemplateImpl implements Template {

	private HashMap<String, FullClonePool> pools = new HashMap<String, FullClonePool>();
	private VmTemplateInfo _template;
	private String _path;
	public TemplateImpl(VmTemplateInfo template){
		this._template = template;
		this._path = template.path;
	}
	
	@Override
	public String getPath() {
		return this._path;
	}
	
	public String toString(){
		return this._path;
	}

	@Override
	public void addOrUpdateFullClonePool(FullClonePool pool) {
		pools.put(pool.getName(), pool);
		
		
	}

	@Override
	public Collection<FullClonePool> getFullClonePools() {
		// TODO Auto-generated method stub
		return pools.values();
	}

}
