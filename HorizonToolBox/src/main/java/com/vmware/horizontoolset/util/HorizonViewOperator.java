/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vmware.vdi.vlsi.binding.vdi.EntityId;
import com.vmware.vdi.vlsi.binding.vdi.entity.ApplicationId;
import com.vmware.vdi.vlsi.binding.vdi.entity.DesktopId;
import com.vmware.vdi.vlsi.binding.vdi.entity.FarmId;
import com.vmware.vdi.vlsi.binding.vdi.entity.UserEntitlementId;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer;
import com.vmware.vdi.vlsi.binding.vdi.infrastructure.ConnectionServer.ConnectionServerInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application;
import com.vmware.vdi.vlsi.binding.vdi.resources.Application.ApplicationInfo;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop;
import com.vmware.vdi.vlsi.binding.vdi.resources.Desktop.DesktopSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.Farm.FarmSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.resources.RDSServer.RDSServerSummaryView;
import com.vmware.vdi.vlsi.binding.vdi.users.ADUserOrGroup;
import com.vmware.vdi.vlsi.binding.vdi.users.AccessGroup;
import com.vmware.vdi.vlsi.binding.vdi.users.UserEntitlement;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADDomain;
import com.vmware.vdi.vlsi.binding.vdi.utils.ADDomain.ADDomainInfo;
import com.vmware.vdi.vlsi.client.Connection;
import com.vmware.vdi.vlsi.client.Query;
import com.vmware.vdi.vlsi.client.Query.QueryFilter;
import com.vmware.vdi.vlsi.cname.vdi.users.ADUserOrGroupCName;
import com.vmware.vim.binding.vmodl.DataObject;
import com.vmware.vim.vmomi.client.http.HttpConfiguration;
import com.vmware.vim.vmomi.client.http.ThumbprintVerifier;

/**
 * A layer to encapsulate all Horizon View Connection Server related operations.
 * 
 * @author nanw
 */
public class HorizonViewOperator implements AutoCloseable {
    
    private final Connection conn;
    public final String host;
    public final String user;
    public final String password;
    public final String domain;
    
    public HorizonViewOperator(String host, String user, String password, String domain) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.domain = domain;
        
        conn = connect(host, user, password, domain);
    }
    
    public HorizonViewOperator(Connection conn) {
        this.host = null;
        this.user = null;
        this.password = null;
        this.domain = null;
        this.conn = conn;
    }
    
    public Connection getConnection() {
        return conn;
    }
    
    public void ensureConnection() {
        //if conn timeout
        conn.login(user, password, domain);
    }
    
    private static Connection connect(String host, String user, String password, String domain) {

        System.out.println("Connecting to Horizon View connection server: " + host + "...");

        HttpConfiguration httpConfig = HttpConfiguration.Factory.newInstance();
        
        httpConfig.setThumbprintVerifier(ThumbprintVerifier.Factory.createAllowAllThumbprintVerifier());
        //httpConfig.setThumbprintVerifier(MyThumbprintVerifier.INSTANCE);

        String sdkUri;   // = String.format("%s/view-vlsi/sdk", conf.viewConnectionServer);
        if (host.startsWith("http://") || host.startsWith("https://"))
            sdkUri = host;
        else
            sdkUri = "https://" + host;
        sdkUri += "/view-vlsi/sdk";
            
        Connection connection = new Connection(sdkUri, httpConfig);
        connection.login(user, password, domain);
        return connection;
    }
    
    @Override
    public void close() {
        conn.close();
    }
    
    public String getConnectionServerVersion() {
        ConnectionServer cs = conn.get(ConnectionServer.class);
        ConnectionServerInfo[] servers = cs.list();

        if (servers.length == 1)
            return servers[0].general.version;

        //this does not always work...
        for (ConnectionServerInfo csi : servers) {
            if (csi.general.serverAddress.equalsIgnoreCase(host)) {
                return csi.general.version;
            }
        }
        return null;
    }
    
    public List<RDSServerSummaryView> listRDSServers() {
        
        return listAll(RDSServerSummaryView.class);
    }

    public List<FarmSummaryView> listFarms() {
        
        return listAll(FarmSummaryView.class);
    }

    public List<DesktopSummaryView> listDesktopPools() {
        
        return listAll(DesktopSummaryView.class);
    }
    
    public List<ApplicationInfo> listAppPools() {
        
        return listAll(ApplicationInfo.class);
    }
    
    public ADDomainInfo[] listDomains() {
    	ADDomain domainService = conn.get(ADDomain.class);
    	return domainService.list();
    }
    
    public <T extends DataObject> List<T> listAll(Class<T> type) {
    	
        List<T> ret = new ArrayList<>();
        try (Query<T> query = new Query<>(conn, type)) {
            for (T info : query) {
               ret.add(info);
            }
        }
        return ret;
    }
    
    public <T extends DataObject> List<T> listAll(Class<T> type, QueryFilter filter) {
    	
        List<T> ret = new ArrayList<>();
        try (Query<T> query = new Query<>(conn, type, filter)) {
            for (T info : query) {
               ret.add(info);
            }
        }
        return ret;
    }
        
//    public FarmId createFarm(String farmName, List<RDSServerId> rDSServerIds) {
//    	
//        final Farm.FarmData farmData = new Farm.FarmData();
//        farmData.setName(farmName);
//        farmData.setAccessGroup(getRootAccessGroup().getId());
//        
//        final Farm.FarmSpec spec = new Farm.FarmSpec();
//        spec.data = farmData;
//        spec.rdsServers = rDSServerIds.toArray(new RDSServerId[]{});
//        final Farm farm = conn.get(Farm.class);
//        
//        return farm.create(spec);
//    }

    public AccessGroup.AccessGroupInfo getRootAccessGroup() {
        List<AccessGroup.AccessGroupInfo> accessGroupInfos = listAccessGroupInfos();
        for (final AccessGroup.AccessGroupInfo info : accessGroupInfos) {
            if ("Root".equals(info.getBase().getName())) {
                return info;
            }
        }
        return null;
    }
        
    public List<AccessGroup.AccessGroupInfo> listAccessGroupInfos() {
        AccessGroup accessGroup = conn.get(AccessGroup.class);
        AccessGroup.AccessGroupInfo[] infos = accessGroup.list();
        return Arrays.asList(infos);
    }
    
    public ApplicationId createAppPool(FarmId farmId, String name, String displayName, String commandLine, String parameters, String workingDirectory, String description) {
        final Application.ApplicationData data = new Application.ApplicationData();
        
        data.setName(name);
        if (!"".equals(description))
            data.setDescription(description);
        data.setDisplayName(displayName);
        
        final Application.ApplicationExecutionData exeData = new Application.ApplicationExecutionData();
        exeData.setFarm(farmId);
        exeData.setExecutablePath(commandLine);
        exeData.setStartFolder(workingDirectory);
        exeData.setArgs(parameters);
        final Application.ApplicationSpec spec = new Application.ApplicationSpec();
        spec.setData(data);
        spec.setExecutionData(exeData);
        
        final Application application = conn.get(Application.class);
        return application.create(spec);
    }
    
    public DesktopId createRdsDesktopPool(FarmId farmId, String id, String name, String description) {
        
        Desktop.RDSDesktopSpec rdsds = new Desktop.RDSDesktopSpec();
        rdsds.setFarm(farmId);

        Desktop.DesktopBase base = new Desktop.DesktopBase();
        base.setName(id);
        base.setDisplayName(name);
        if (!"".equals(description))
            base.setDescription(description);
        base.setAccessGroup(getRootAccessGroup().getId());
        
        final Desktop.DesktopSpec spec = new Desktop.DesktopSpec();
        spec.setBase(base);
        spec.setRdsDesktopSpec(rdsds);
        spec.setType("RDS");
        final Desktop desktop = conn.get(Desktop.class);
        return desktop.create(spec);
    }
    
    public UserEntitlementId entitleUser(String fullName, EntityId resourceId) {
        String[] tmp = parseDomainName(fullName);
        String domainName = tmp[0];
        String loginName = tmp[1];
               
        final ADUserOrGroup.ADUserOrGroupSummaryView userOrGroup = getADUserOrGroup(loginName, domainName);
        if (userOrGroup == null) {
            System.out.println("    Error: Invalid user: " + fullName);
            return null;
        }

        final UserEntitlement.UserEntitlementBase entitlementBase = new UserEntitlement.UserEntitlementBase();
        entitlementBase.setResource(resourceId);
        entitlementBase.setUserOrGroup(userOrGroup.getId());
        final UserEntitlement userEntitlement = conn.get(UserEntitlement.class);

        if (userOrGroup.base.isGroup())
            System.out.println("    Group: " + fullName);
        else
            System.out.println("    User:  " + fullName);
        return userEntitlement.create(entitlementBase);
    }

    private static final ADUserOrGroupCName.ADUserOrGroupSummaryViewCName ADUSER_GROUP_SUMMARY_VIEW_CNAME = new ADUserOrGroupCName.ADUserOrGroupSummaryViewCName();
    
    public ADUserOrGroup.ADUserOrGroupSummaryView getADUserOrGroup(String name, String domain) {
        
    	Query.QueryFilter filter = Query.QueryFilter.and(new Query.QueryFilter[] { 
            Query.QueryFilter.equals(ADUSER_GROUP_SUMMARY_VIEW_CNAME.base.loginName, name),
            Query.QueryFilter.equals(ADUSER_GROUP_SUMMARY_VIEW_CNAME.base.domain, domain)
        });
        
        try (Query<ADUserOrGroup.ADUserOrGroupSummaryView> query = new Query<>(conn, ADUserOrGroup.ADUserOrGroupSummaryView.class, filter)) {
            for (ADUserOrGroup.ADUserOrGroupSummaryView info : query) {
            	return info;
            }
        }
        
        /*
        //if we reach here, the user is not found.
        //One case is that the user is a group. The input parameter looks like:
        //	name="Domain User", domain="asdf"
        //The domain name is short name, not full name. It's very strange that 
        //it works well for user, but does not work for group.
        //So we have another try here, by fixing the domain name as full domain name, and do the search again
        if (domain.indexOf('.') < 0) {
        	ADDomainInfo[] domains = listDomains();
        	String prefix = domain + '.';
        	for (ADDomainInfo info : domains) {
        		if (info.dnsName.startsWith(prefix)) {
        			domain = info.dnsName;
        			break;
        		}
        	}
	        filter = Query.QueryFilter.and(new Query.QueryFilter[] { 
	            Query.QueryFilter.equals(ADUSER_GROUP_SUMMARY_VIEW_CNAME.base.loginName, name),
	            Query.QueryFilter.equals(ADUSER_GROUP_SUMMARY_VIEW_CNAME.base.domain, domain)
	        });
	        
	        try (Query<ADUserOrGroup.ADUserOrGroupSummaryView> query = new Query<>(conn, ADUserOrGroup.ADUserOrGroupSummaryView.class, filter)) {
	            for (ADUserOrGroup.ADUserOrGroupSummaryView info : query) {
	            	return info;
	            }
	        }
        }
        */
    	/*
        try (Query<ADUserOrGroup.ADUserOrGroupSummaryView> query = new Query<>(conn, ADUserOrGroup.ADUserOrGroupSummaryView.class)) {
            for (ADUserOrGroup.ADUserOrGroupSummaryView info : query) {
            	System.out.println("---------------------------------------------------------");
            	System.out.println("adDistinguishedName:" + info.base.adDistinguishedName);
            	System.out.println("displayName:        " + info.base.displayName);
            	System.out.println("longDisplayName:    " + info.base.longDisplayName);
            	System.out.println("sid:                " + info.base.sid);
            	System.out.println("domain:             " + info.base.domain);
            	System.out.println("firstName:          " + info.base.firstName);
            	System.out.println("lastName:           " + info.base.lastName);
            	System.out.println("name:               " + info.base.name);
            	System.out.println("email:              " + info.base.email);
            	System.out.println("inFolder:           " + info.base.inFolder);
            	System.out.println("loginName:          " + info.base.loginName);
            	System.out.println("phone:              " + info.base.phone);
               //return info;
            }
        }
        */
        return null;
    }
    
    private static String[] parseDomainName(String longName) {
        String domain = null;
        String name = null;
        
        int i = longName.indexOf('\\');
        if (i > 0) {
            domain = longName.substring(0, i);
            name = longName.substring(i + 1);
        } else {
            i = longName.indexOf('@');
            if (i > 0) {
                domain = longName.substring(i + 1);
                name = longName.substring(0, i);
            }
        }
        return new String[] {domain, name};
    }
}
