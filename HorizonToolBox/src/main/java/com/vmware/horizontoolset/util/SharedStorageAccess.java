package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.HashMap;
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
 *
 */
public class SharedStorageAccess extends ToolboxStorage{

	private static Logger log = Logger.getLogger(SharedStorageAccess.class);

	private final static String namePrefix = "CN=TEToolbox-";
	private final static String namePostfix = "OU=Global,OU=Properties,dc=vdi,dc=vmware,dc=int";
	private final static String attrId = "description";

	@Override
	public void delete(String key) {
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

	//use some chars that can't be typed to avoid conflict
	private static final String separator = "" + (char) 198   + (char)214;

	@Override
	public List<String> getList(String key){
		log.debug("SharedStorageAccess get List: using default context, key:" + key);
		List<String> list =  new ArrayList<String>();
		String content = get(key);
		if (StringUtil.isEmpty(content)){
			return list;
		}

		String[] array = content.split(separator);
		for (int i=0;i<array.length;i++){
			list.add(array[i]);
		}
		return list;
	}

	@Override
	public void  setList(String key, List<String> values){
		log.debug("SharedStorageAccess set List: using default context, key:"+ key);

		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<values.size();i++){
			if (i>0){
				buffer.append(separator);
			}
			buffer.append(values.get(i));
		}
		set(key, new String(buffer));
	}

	//use some chars that can't be typed to avoid conflict
	private static final String mapSeparator = ""  + (char)199 + (char) 215;

	/**
	 *
	 * @param key
	 * @param T   must be POJO bean
	 * @return
	 */
	@Override
	public Map<String,String> getMap(String key){
		log.debug("SharedStorageAccess get Map: using default context, key:"+key);
		Map<String, String> map = new HashMap<String, String>();
		List<String> content = getList(key);
		if (content == null || content.size() == 0){
			return map;
		}

		for (String one: content){
			String[] kv =one.split(mapSeparator);
			if (kv.length > 1){
				map.put(kv[0], kv[1]);
			}else{
				log.error("Invalid map content:" + one);
			}

		}

		return map;
	}

	/**
	 *
	 * @param key
	 * @param map
	 */
	@Override
	public void  setMap(String key, Map<String, String> map){
		log.debug("SharedStorageAccess set Map: using default context, key:"+ key);
		List<String> content = new ArrayList<String>();
		for (String mkey:map.keySet()){
			content.add(mkey+mapSeparator+map.get(mkey));
		}

		setList(key,content);
	}


	@Override
	public String get(String key) {


		log.debug("SharedStorageAccess get: using default context, key:" + key);

		String name = getName(key);

		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();

			Attributes attrs = dirCtx.getAttributes(name, new String[] {attrId});
			Attribute a = attrs.get(attrId);
			String result =  (String) a.get();
			if (result!=null){
				result = result.trim();
			}
			return result;
		} catch (NameNotFoundException e) {
			log.debug("LDAP key not found: " + name + ". e=" + e);
			//omit
		} catch (Exception e) {
			log.warn("Error reading SharedStorageAccess. key=" + key, e);
			return null;
		}
		return null;
	}

	@Override
	public void set(String key, String value) {
		if (StringUtil.isEmpty(value)){
			//empty string can not be set, so we have to use " "
			value = " ";
		}
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
