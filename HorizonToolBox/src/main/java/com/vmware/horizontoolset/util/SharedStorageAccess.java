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
	private final static String defaultattrkey = "description";

	@Override
	public void delete(String key) {
		if (StringUtil.isEmpty(key)){
			return;
		}
		key = key.toLowerCase();
		log.info("Start to delete key:"+ key +" from LDAP");
		String name = getName(key);

		try (VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();) {
			DirContext dirCtx = vdiCtx.getDirContext();

			dirCtx.destroySubcontext(name);
		} catch (Exception e) {
			log.error("Error destroying subcontext: " + key, e);
		}
	}


	private static void initAttr(DirContext dirCtx, Attributes attrs, String attrkey){

		Attribute classes = new BasicAttribute("objectClass");
        classes.add("top");
        classes.add("pae-PropertyObject");
        classes.add("pae-VDMProperties");
        attrs.put(classes);
        attrs.put(new BasicAttribute(attrkey," "));

	}

	private static Attributes getOrcreate(DirContext dirCtx, String name, String attrkey) throws NamingException {

		log.info("Start to get or create LDAP item:"+ name);

		try{
			Attributes attrs = dirCtx.getAttributes(name, new String[] {attrkey});
			log.info("Attributes found, return directly");
			Attribute a = attrs.get(attrkey);
			if (a==null){
				initAttr(dirCtx, attrs, attrkey);
				dirCtx.modifyAttributes( name, DirContext.REPLACE_ATTRIBUTE,attrs);
			}
			return attrs;
		}catch(NameNotFoundException ex){
			log.info("Name not found, try to create a new one");
			Attributes attributes=new BasicAttributes(true);
	        initAttr(dirCtx, attributes, attrkey);

	        try {
	        	dirCtx.createSubcontext(name, attributes);
	        } catch (Exception e) {
	        	log.error("Fail creating subcontext: " + name, e);
	        	return null;
	        }
	        return attributes;
		}




	}


	private static String getName(String key) {
		return namePrefix + key + ',' + namePostfix;
	}


	private List<String> getList(String namekey, String attrkey){

		log.info("SharedStorageAccess get List: using default context, key:" + namekey);
		List<String> list =  new ArrayList<String>();

		String name = getName(namekey);

		try  {
			VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();
			DirContext dirCtx = vdiCtx.getDirContext();
			Attributes attrs = getOrcreate(dirCtx,name, attrkey);

			Attribute a = attrs.get(attrkey);
			if (a==null){
				log.warn("No attr found for LDAP name:"+ name+", attr:"+ attrkey);
				return list;
			}
			for (int i=0; i<a.size();i++){

				String result =  (String) a.get(i);
				if (result!=null){
					result = result.trim();
				}
				if (!StringUtil.isEmpty(result)){
					list.add(result);
				}

			}

			return list;
		}
		catch (Throwable e) {
			log.warn("Error reading SharedStorageAccess. key=" + namekey+", attr:"+ attrkey, e);
			return list;
		}
	}

	@Override
	public List<String> getList(String key){
		if (StringUtil.isEmpty(key)){
			return null;
		}
		key = key.toLowerCase();
		return this.getList(key, defaultattrkey);
	}


	private void setList(String key, String attrkey, List<String> values){

		log.info("SharedStorageAccess set List: using default context, key:" + key +" attr:"+attrkey);
		String name = getName(key);
		try {
			VDIContext vdiCtx = VDIContextFactory.defaultVDIContext();
			DirContext dirCtx = vdiCtx.getDirContext();

			if (values.size() == 0){
				log.info("Remove LDAP attr:" + name);
				dirCtx.destroySubcontext(name);
				return;
			}
			Attributes attrs = getOrcreate(dirCtx,name, attrkey);

			Attribute a = attrs.get(attrkey);
			a.clear();

			dirCtx.modifyAttributes( name, DirContext.REPLACE_ATTRIBUTE,attrs);
			for (int i=0;i<values.size();i++) {
				String value = values.get(values.size()-1-i);
				if (!a.contains(value)) {
					log.debug("Add value:"+value);
					a.add(i,value);
				}
			}

			dirCtx.modifyAttributes( name, DirContext.REPLACE_ATTRIBUTE,attrs);
		} catch (Exception e1) {
			log.error("Fail connecting to default VDI context.", e1);
		}


	}

	@Override
	public void setList(String key, List<String> values) {
		if (StringUtil.isEmpty(key)){
			return;
		}
		key = key.toLowerCase();
		this.setList(key, defaultattrkey, values);
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
		if (StringUtil.isEmpty(key)){
			return null;
		}
		key = key.toLowerCase();

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
		if (StringUtil.isEmpty(key)){
			return;
		}
		key = key.toLowerCase();

		log.info("SharedStorageAccess set Map: using default context, key:"+ key);
		List<String> content = new ArrayList<String>();
		for (String mkey:map.keySet()){
			content.add(mkey+mapSeparator+map.get(mkey));
		}

		setList(key,content);
	}


	@Override
	public String get(String key) {
		if (StringUtil.isEmpty(key)){
			return null;
		}
		key = key.toLowerCase();
		return this.get(key, defaultattrkey);
	}

	@Override
	public void set(String key, String value) {
		if (StringUtil.isEmpty(key)){
			return;
		}
		key = key.toLowerCase();
		this.set(key,defaultattrkey, value);
	}



	@Override
	public String get(String namekey, String attrkey) {
		if (StringUtil.isEmpty(namekey) || (StringUtil.isEmpty(attrkey))){
			return null;
		}
		namekey = namekey.toLowerCase();
		attrkey = attrkey.toLowerCase();

		log.info("LDAP Get key:"+namekey+",attr:"+attrkey);
		List<String> list = getList(namekey,attrkey);
		if (list == null ||list.size() == 0){
			log.info("value is not found");
			return "";
		}
		log.info("value is found:"+list.get(0));
		return list.get(0);

	}


	@Override
	public void set(String namekey, String attrkey, String value) {
		if (StringUtil.isEmpty(namekey) || (StringUtil.isEmpty(attrkey))){
			return;
		}
		namekey = namekey.toLowerCase();
		attrkey = attrkey.toLowerCase();

		log.info("LDAP Set key:"+namekey+",attr:"+attrkey);
		ArrayList<String> list = new ArrayList<String>();
		list.add(value);
		this.setList(namekey, attrkey, list);


	}





}
