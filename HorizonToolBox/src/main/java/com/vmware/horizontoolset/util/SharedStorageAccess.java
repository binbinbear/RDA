package com.vmware.horizontoolset.util;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

/**
 * 
 * A storage to store key-value pair. The storage is shard, with late consistency.
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
public class SharedStorageAccess implements AutoCloseable {

	private static Logger log = Logger.getLogger(SharedStorageAccess.class);
	
	private final static String namePrefix = "CN=TEToolbox-";
	private final static String namePostfix = ",OU=Global,OU=Properties";
	private final static String attrId = "description";
	
	private final DirContext ctx;

	public SharedStorageAccess(DirContext context) {
		this.ctx = context;
	}

	public String get(String key) {
		String name = getName(key);
		try {
			Attributes attrs = ctx.getAttributes(name, new String[] {attrId});
			Attribute a = attrs.get(attrId);
			return (String) a.get();
		} catch (NamingException e) {
			return null;
		}
	}
	
	public void set(String key, String value) {
		String name = getName(key);
		try {
			Attributes attrs = ctx.getAttributes(name, new String[] {attrId});
			Attribute a = attrs.get(attrId);
			a.set(0, value);
			ctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NameNotFoundException e) {
			create(name, value);
		} catch (NamingException e) {
			log.error("Error set key: " + key, e);
		}
	}
	
	public void delete(String key) {
		String name = getName(key);
		try {
			ctx.destroySubcontext(name);
		} catch (Exception e) {
			log.error("Error destroying subcontext: " + key, e);
		}
	}
	
	private void create(String name, String value) {
		
		Attributes attributes=new BasicAttributes();
        Attribute classes = new BasicAttribute("objectClass");
        classes.add("top");
        classes.add("Group");
        attributes.put(classes);
        
        Attribute data = new BasicAttribute("description", value);
        attributes.put(data);
        
        try {
        	DirContext newEntry = ctx.createSubcontext(name, attributes);
            newEntry.close();
        } catch (Exception e) {
        	log.error("Fail creating subcontext: " + name, e);
        } finally {
        }
	}

	@Override
	public void close() throws Exception {
		//nothing. Ctx is not owned by us and we need not to close it.
	}
	
	static String getName(String key) {
		return namePrefix + key + namePostfix;
	}
	

	public static String defaultContextGet(String key) {
		String name = getName(key);
		
		VDIContext vdiCtx = null;
		DirContext dirCtx = null;
		try {
			vdiCtx = VDIContextFactory.defaultVDIContext();
			dirCtx = vdiCtx.getDirContext();
			
			Attributes attrs = dirCtx.getAttributes(name, new String[] {attrId});
			Attribute a = attrs.get(attrId);
			return (String) a.get();
		} catch (Exception e) {
			log.warn("Error reading SharedStorageAccess. key=" + key, e);
			return null;
		} finally {
			if (dirCtx != null) {
				try {
					dirCtx.close();
				} catch (Exception e) {
				}
			}
			if (vdiCtx != null) {
				try {
					VDIContext.release(vdiCtx);
				} catch (Exception e) {
				}
			}
		}
	}
}
