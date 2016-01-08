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
		log.info("Start to delete key:"+ key +" from LDAP");
		String name = getName(key);

		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();

			dirCtx.destroySubcontext(name);
		} catch (Exception e) {
			log.error("Error destroying subcontext: " + key, e);
		}
	}


	private static Attribute newAttribute(){
		  return new BasicAttribute(attrId," ");
	}

	private static Attributes getOrcreate(DirContext dirCtx, String name) throws NamingException {

		log.info("Start to get or create LDAP item:"+ name);

		try{
			Attributes attrs = dirCtx.getAttributes(name, new String[] {attrId});
			log.info("Attributes found, return directly");
			return attrs;
		}catch(NameNotFoundException ex){
			log.info("Name not found, try to create a new one");
			Attributes attributes=new BasicAttributes(true);
	        Attribute classes = new BasicAttribute("objectClass");
	        classes.add("top");
	        classes.add("pae-PropertyObject");
	        classes.add("pae-VDMProperties");
	        attributes.put(classes);

	        attributes.put(newAttribute());

	        try {
	        	dirCtx.createSubcontext(name, attributes);
	        } catch (Exception e) {
	        	log.error("Fail creating subcontext: " + name, e);
	        	return null;
	        }
	        return attributes;
		}




	}


	static String getName(String key) {
		return namePrefix + key + ',' + namePostfix;
	}


	@Override
	public List<String> getList(String key){
		log.info("SharedStorageAccess get List: using default context, key:" + key);
		List<String> list =  new ArrayList<String>();

		String name = getName(key);

		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();
			Attributes attrs = getOrcreate(dirCtx,name);


			Attribute a = attrs.get(attrId);
			for (int i=0; i<a.size();i++){
				String result =  (String) a.get(i);
				if (result!=null){
					result = result.trim();
				}
				list.add(result);
			}

			return list;
		}
		catch (Exception e) {
			log.warn("Error reading SharedStorageAccess. key=" + key, e);
			return list;
		}

	}

	@Override
	public void setList(String key, List<String> values) {
		log.info("SharedStorageAccess set List: using default context, key:" + key);
		String name = getName(key);
		try {

			VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();
			DirContext dirCtx = vdiCtx.getDirContext();

			Attributes attrs = getOrcreate(dirCtx,name);


			Attribute a = attrs.get(attrId);
			a.clear();
			for (int i = 0; i < values.size(); i++) {
				String value = values.get(i);
				if (!a.contains(value)) {
					log.info("Add value:"+value);
					a.add(i,value);
				}
			}



			dirCtx.modifyAttributes( name, DirContext.REPLACE_ATTRIBUTE,attrs);
		} catch (Exception e1) {
			log.error("Fail connecting to default VDI context.", e1);
		}

	}


	private static final String mapSeparator = "<==>"  ;

	/**
	 *
	 * @param key
	 * @param T   must be POJO bean
	 * @return
	 */
	@Override
	public Map<String,String> getMap(String key){
		log.info("SharedStorageAccess get Map: using default context, key:"+key);
		Map<String, String> map = new HashMap<String, String>();
		List<String> content = getList(key);
		if (content == null || content.size() == 0){
			return map;
		}

		for (String one: content){
			int index = one.indexOf(mapSeparator);
			if (index>0){
				String k = one.substring(0,index);
				String v = one.substring(index+mapSeparator.length());
				log.info("Found Key:"+k+" Value:"+v);
				map.put(k,v);
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
		log.info("SharedStorageAccess set Map: using default context, key:"+ key);
		List<String> content = new ArrayList<String>();
		for (String mkey:map.keySet()){
			content.add(mkey+mapSeparator+map.get(mkey));
		}

		setList(key,content);
	}


	@Override
	public String get(String key) {
		log.info("LDAP Get key:"+key);
		List<String> list = getList(key);
		if (list == null ||list.size() == 0){
			log.info("value is not found");
			return "";
		}
		log.info("value is found:"+list.get(0));
		return list.get(0);
	}

	@Override
	public void set(String key, String value) {

		ArrayList<String> list = new ArrayList<String>();
		list.add(value);
		this.setList(key, list);
	}
}
