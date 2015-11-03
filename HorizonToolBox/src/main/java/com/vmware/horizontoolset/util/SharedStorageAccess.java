package com.vmware.horizontoolset.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

/**
 * 
 * A storage to store key-value pair. The storage is shared, with late consistency.
 * 
 * Shared: the storage will be shared across multiple View Connection servers.
 * Late consistency: Changes to the storage is only visible to other readers 
 * after certain time period.
 * 
 * The shared storage stores only small values, e.g. less than 100k.
 *
 * @author nanw
 *
 */
public class SharedStorageAccess {

	private static Logger log = Logger.getLogger(SharedStorageAccess.class);
	
	private final static String namePrefix = "CN=TEToolbox-";
	private final static String namePostfix = "OU=Global,OU=Properties,dc=vdi,dc=vmware,dc=int";
	private final static String attrId = "description";
	
	public static void delete(String key) {
		String name = getName(key);
		
		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();
			
			dirCtx.destroySubcontext(name);
		} catch (Exception e) {
			log.error("Error destroying subcontext: " + key, e);
		}
	}
	
	private static void create(DirContext dirCtx, String name, String value) {
		
		Attributes attributes=new BasicAttributes();
        Attribute classes = new BasicAttribute("objectClass");
        classes.add("top");
        classes.add("Group");
        attributes.put(classes);
        
        Attribute data = new BasicAttribute("description", value);
        attributes.put(data);
        
        try {
        	DirContext newEntry = dirCtx.createSubcontext(name, attributes);
            newEntry.close();
        } catch (Exception e) {
        	log.error("Fail creating subcontext: " + name, e);
        } finally {
        }
	}

	
	static String getName(String key) {
		return namePrefix + key + ',' + namePostfix;
	}
	
	
	private static final String separator = "\\/\\/";
	
	public static List<String> getList(String key){
		log.debug("SharedStorageAccess: using default context");
		List<String> list =  Collections.emptyList();
		String content = get(key);
		if (content ==null){
			return list;
		}
		
		String[] array = content.split(separator);
		for (int i=0;i<array.length;i++){
			list.add(array[i]);
		}
		return list;
	}
	
	public static void  setList(String key, List<String> values){
		log.debug("SharedStorageAccess: using default context");
		
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<values.size();i++){
			if (i>0){
				buffer.append(separator);
			}
			buffer.append(values.get(i));
		}
		set(key, new String(buffer));
	}
	
	private static final String mapSeparator = ">=<";
	
	public static Map<String,String> getMap(String key){
		log.debug("SharedStorageAccess: using default context");
		Map<String, String> map = Collections.emptyMap();
		List<String> content = getList(key);
		if (content == null || content.size() == 0){
			return map;
		}
		
		for (String one: content){
			String[] kv =one.split(mapSeparator);
			map.put(kv[0], kv[1]);
		}
		
		return map;
	}
	
	public static void  setMap(String key, Map<String, String> map){
		log.debug("SharedStorageAccess: using default context");
		List<String> content = Collections.emptyList();
		for (String mkey:map.keySet()){
			content.add(mkey+mapSeparator+map.get(mkey));
		}
		
		setList(key,content);
	}
	
	
	public static String get(String key) {
		log.debug("SharedStorageAccess: using default context");
		
		String name = getName(key); 
		
		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();
			
			Attributes attrs = dirCtx.getAttributes(name, new String[] {attrId});
			Attribute a = attrs.get(attrId);
			return (String) a.get();
		} catch (NameNotFoundException e) {
			log.debug("LDAP key not found: " + name + ". e=" + e);
			//omit
		} catch (Exception e) {
			log.warn("Error reading SharedStorageAccess. key=" + key, e);
			return null;
		} 
		return null;
	}
	
	public static void set(String key, String value) {
		String name = getName(key);

		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();
			
			try {
				Attributes attrs = dirCtx.getAttributes(name, new String[] {attrId});
				Attribute a = attrs.get(attrId);
				a.set(0, value);
				dirCtx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
			} catch (NameNotFoundException e) {
				create(dirCtx, name, value);
			} catch (IllegalStateException e) {
				log.debug("Set value problem: Already exist?", e);
			} catch (NamingException e) {
				log.error("Error set key: " + key, e);
			}
		} catch (ADAMConnectionFailedException e1) {
			log.error("Fail connecting to default VDI context.", e1);
		}
	}
}
