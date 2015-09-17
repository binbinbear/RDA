package com.vmware.horizontoolset.viewapi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.viewapi.ConnectionServer;
import com.vmware.horizontoolset.viewapi.Container;
import com.vmware.horizontoolset.viewapi.Farm;
import com.vmware.horizontoolset.viewapi.RDS;
import com.vmware.horizontoolset.viewapi.SessionFarm;
import com.vmware.horizontoolset.viewapi.SessionPool;
import com.vmware.horizontoolset.viewapi.SnapShotViewPool;
import com.vmware.horizontoolset.viewapi.Template;
import com.vmware.horizontoolset.viewapi.VM;
import com.vmware.horizontoolset.viewapi.ViewPool;
import com.vmware.vdi.vlsi.binding.vdi.entity.BaseImageVmId;
import com.vmware.vdi.vlsi.binding.vdi.entity.DesktopId;
import com.vmware.vdi.vlsi.binding.vdi.entity.FarmId;
import com.vmware.vdi.vlsi.binding.vdi.entity.ViewComposerDomainAdministratorId;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer.ConnectionServerInfo;
import com.vmware.vdi.vlsi.binding.vdi.query.QueryDefinition;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.CustomizationSettings;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Farm.FarmInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.RDSServer.RDSServerSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.Session.SessionLocalSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADContainer;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADContainer.ADContainerInfo;
import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.BaseImageSnapshot;
import com.vmware.vdi.vlsi.binding.vdi.utils.virtualcenter.BaseImageSnapshot.BaseImageSnapshotInfo;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.CName;
import com.vmware.vdi.vlsi.cname.vdi.users.SessionCName.SessionLocalSummaryViewCName;
import com.vmware.vim.binding.impl.vmodl.TypeNameImpl;
import com.vmware.vim.binding.vmodl.DataObject;

public class ViewQueryService {
	private static Logger log = Logger.getLogger(ViewQueryService.class);
	
	private Desktop _desktop;
	private BaseImageSnapshot _snapshotService;
	private Connection _connection;
	private Container _adContainer = null;
	public ViewQueryService(Connection connect){
		this._connection = connect;
		this._desktop = connect.get(Desktop.class);
		this._snapshotService = connect.get(BaseImageSnapshot.class);
		
	}
	
	
	private ADContainerInfo[]  _containerInfos;
	ADContainerInfo[] getContainers(ViewComposerDomainAdministratorId adminID){
		if (adminID ==null){
			log.info("adminID is null!!");
			return null;
		}
		if (_containerInfos == null){
			 ADContainer _container = _connection.get(ADContainer.class);
			 _containerInfos= _container.listByViewComposerDomainAdministrator(adminID);
			 
		}
		return _containerInfos;
	}
	
	
	
	public ADContainerInfo getContainer(CustomizationSettings customSettings){
		if (customSettings == null){
			log.info("customSettings is null!!");
			return null;
		}
		ADContainerInfo[] infos =  getContainers(customSettings.domainAdministrator);
		if (infos == null){
			log.info("can't get infos");
			return null;
		}
		
		String id = customSettings.getAdContainer().getId();
		String validID = id.substring(id.lastIndexOf('/')).toLowerCase();
		for (int i=0;i<infos.length;i++){
			ADContainerInfo info =infos[i];
			if (info.getRdn()!=null && info.getId().id.toLowerCase().contains(validID)){
				return info;
			}
		}
		return null;
	}
	public DesktopInfo getDesktopInfo(DesktopSummaryView summary){
		DesktopInfo desktopinfo = this._desktop.get(summary.id);
		
		if (desktopinfo == null){
			log.warn("desktop not found for:"+summary.desktopSummaryData.displayName);
		}
		return desktopinfo;
	}
	
	
	
	public Template getTemplate(String id, String templatePath){	
		Template template = Cache.getTemplate(id);
		if (template !=null){
			log.debug("Great VM cache hit " + templatePath);
			return template;
		}
		

		log.info("Create template for "+ templatePath);
		Cache.addOrUpdateTemplate(id, new TemplateImpl(templatePath));
		return Cache.getTemplate(id);
	}
	
	
	public BaseImageSnapshotInfo[] getSnapShots(BaseImageVmId vmid){
		return this._snapshotService.list( vmid);
	}
	
	
	public VM getVM(BaseImageVmId vmid, String fullPath){
		VM result = Cache.getVM(vmid.id);
		if (result !=null){
			log.info("Great VM cache hit ");
			return result;
		}
		

		log.info("Create vm for "+ fullPath);
		Cache.addOrUpdateVM(vmid.id, new VMImpl(fullPath));
		return Cache.getVM(vmid.id);
	}

	
	  private <T extends DataObject> List<T> getAllObjects(Class<T> type) {
		  log.debug("Start to query");
	        List<T> ret = new ArrayList<>();

	        try (Query<T> query = new Query<>(this._connection, type)) {

	            for (T info : query) {

	               ret.add(info);

	            }
	        }
	        return ret;
	    }
	

	private List<DesktopSummaryView> getDesktopSummaryViews(){
		log.debug("Start to query pools");

		return getAllObjects(DesktopSummaryView.class);	  
	}
	
	
	private List<RDSServerSummaryView> getRDSServerSummaryViews(){
		log.debug("Start to query pools");

		return getAllObjects(RDSServerSummaryView.class);	  
	}
	
	
	private List<ApplicationInfo> getApplicationInfos(){
		log.debug("Start to query pools");

		return getAllObjects(ApplicationInfo.class);	  
	}
	
	
	public List<SessionPool> getAllSessionPools(){
		List<DesktopSummaryView>  results = this.getDesktopSummaryViews();
		List<SessionPool> list = new ArrayList<SessionPool>();
	    for (DesktopSummaryView desktop : results){
	    	SessionPool pool = PoolFactory.getSessionPool(desktop, this.getSessionCount(desktop.id));
	    	if (pool!=null){
	    		list.add(pool);
	    	}
	    }
	    
	    if (list.size()>0){
		    Collections.sort(list,new Comparator<SessionPool>(){  
	            public int compare(SessionPool arg0, SessionPool arg1) {  
	                return arg1.getSessionCount()- arg0.getSessionCount();
	            }  
	        });  
	    }

	    
	        return list;
		
	}


	/**
	 * this is a low performance function, is only called when getting snapshot report
	 * @return
	 */
	public List<SnapShotViewPool> getAllSnapShotViewPools(){
		log.debug("Start to query pools");
		List<DesktopSummaryView> results = this.getDesktopSummaryViews();
		List<SnapShotViewPool> list = new ArrayList<SnapShotViewPool>();
		
	    if (results == null || results.size() == 0) {
	    	log.debug("no results in queryResults");
	        return list;
	    }
	    for (DesktopSummaryView desktop : results){
	    	SnapShotViewPool pool = PoolFactory.getPool(desktop, this);
	    	if (pool!=null){
	    		list.add(pool);
	    	}
	    	
	    }
	        return list;
	}

	
	private static final SessionLocalSummaryViewCName SESSION_LOCAL_SUMMARY_VIEW_CNAME = new SessionLocalSummaryViewCName();
	private static final CName<DesktopId> desktopCName = SESSION_LOCAL_SUMMARY_VIEW_CNAME.referenceData.desktop;
	private static final CName<FarmId> farmCName = SESSION_LOCAL_SUMMARY_VIEW_CNAME.referenceData.farm;
	
	public int getAllSessionCount(){
		return Query.count(this._connection, SessionLocalSummaryView.class,null);
	}
	
	public List<SessionLocalSummaryView> getAllSessions(){
		return this.getAllObjects(SessionLocalSummaryView.class);
	}
	
	public int getSessionCount(DesktopId desktopid){
		QueryFilter filter = QueryFilter.equals(desktopCName,		desktopid);
		return Query.count(this._connection, SessionLocalSummaryView.class, filter);
	}

	private List<FarmInfo> farminfolist ;
	public List<SessionFarm> getAllSessionFarms() {
		log.debug("Start to query farms");
		List<SessionFarm> list = new ArrayList<SessionFarm>();
		if (farminfolist == null){
			farminfolist = this.getAllObjects(FarmInfo.class);
		}
		
		
	    if (farminfolist == null || farminfolist.size()==0) {
	    	log.debug("no result is returned");
	            return list;
	    }
	    
	    for (FarmInfo farm: farminfolist){
	    	list.add(new SessionFarmImpl(farm, this.getAppSessionCount(farm.id)));
	    }
	    

	    if (list.size()>0){
		    Collections.sort(list,new Comparator<SessionFarm>(){  
	            public int compare(SessionFarm arg0, SessionFarm arg1) {  
	                return arg1.getAppSessionCount()- arg0.getAppSessionCount();
	            }  
	        });  
	    }

	    
		return list;
	}

	public int getAppSessionCount(FarmId id) {
		QueryDefinition queryDefinition = new QueryDefinition();
		queryDefinition.setQueryEntityType(new TypeNameImpl("SessionLocalSummaryView"));

		QueryFilter[] filters= new QueryFilter[2];
	    filters[0] =   QueryFilter.equals(farmCName, id);
	    filters[1] = QueryFilter.equals(desktopCName, null);

		
		return Query.count(this._connection, SessionLocalSummaryView.class,QueryFilter.and(filters) );
	}


	public List<ViewPool>  getAllDesktopPools() {
		List<DesktopSummaryView> desktops = this.getDesktopSummaryViews();
		List<ViewPool>  list = new ArrayList<ViewPool>();
	    if (desktops == null || desktops.size() == 0) {
	    	log.debug("no results in pools");
	    }else{
	    	for(DesktopSummaryView desktop: desktops){
		    	list.add(PoolFactory.getBasicViewPool(desktop));
		    }
	    }
	    
		return list;
	}

	public List<ApplicationInfo> getAllApplicationPools() {
		return getAllObjects(ApplicationInfo.class);
	}
	
	public List<RDS> getAllBasicRDSHosts() {
		List<RDSServerSummaryView> rdssummaryviews = this.getRDSServerSummaryViews();
		List<RDS>  list = new ArrayList<RDS>();
	    if (rdssummaryviews == null || rdssummaryviews.size() == 0) {
	    	log.debug("no results in rds hosts");
	    }else{
	    	for(RDSServerSummaryView rds: rdssummaryviews){
		    	list.add(new BasicRDS(rds));
		    }
	    }
	    
		return list;
	}
	
	public List<Farm>  getAllFarms(){
		if (farminfolist == null){
			farminfolist = this.getAllObjects(FarmInfo.class);
		}
		List<Farm> farms = new ArrayList<Farm>();
		for (FarmInfo info: farminfolist){
			farms.add(new BasicFarm(info));
		}
		return farms;
		
		
	}
	

	public List<ConnectionServer> getAllConnectionServers() {
		com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer cs = this._connection.get(com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer.class);
		List<ConnectionServer> servers = new ArrayList<ConnectionServer>();
		ConnectionServerInfo[] infos=cs.list();
		for (int i=0;i<infos.length;i++){
			servers.add(new ConnectionServerImpl(infos[i]));
		}
		return servers;
	}
}
