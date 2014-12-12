package com.vmware.horizontoolset.util;

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.report.CEIPReportUtil;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;

public class LDAP implements AutoCloseable {
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(LDAP.class);

	private Hashtable<String, Object> env = new Hashtable<String, Object>();
	private VDIContext vdiContext;
	private DirContext dirContext;
	private static final String COMMON_CONFIG_DN = "CN=Common,OU=Global,OU=Properties,DC=vdi,DC=vmware,DC=int";

	private static final String LDAP_SEARCH_FILTER = "(objectClass=pae-VDMProperties)";

	@Override
	public void close() {
		log.info("Release resource: Start to close the ctx");
		try {
			if (vdiContext != null) {
				this.vdiContext.disposeContext();
				this.vdiContext = null;
				this.dirContext = null;
			}
		} catch (Exception e) {
			log.warn("Can't close the ctx", e);
		}
	}

	public VDIContext getVDIContext(){
		return this.vdiContext;
	}
	DirContext getContext() {
		
		return this.dirContext;
	}
	
	// TODO: FIXME: only local host is supported by this default context.
	public LDAP(String username, String password, String domain) throws ADAMConnectionFailedException {
		this.vdiContext = VDIContextFactory.VDIContext(username, password, domain);
		this.dirContext = vdiContext.getDirContext();
	}

	protected static String getAttribute(Attributes attributes, String attrId,
			String defaultValue) {
		log.debug("start to get attribute:" + attrId);

		try {
			Attribute attribute = attributes.get(attrId);
			String value = null;
			if (attribute != null) {
				Object obj = attribute.get();
				value = (obj == null) ? null : obj.toString();
			}
			return value == null ? defaultValue : value;
		} catch (NamingException e) {
			log.debug("Retrieve ADAM object attribute failed: ", e);
		}

		return defaultValue;
	}


	/**
	 * NEVER use it for released code!!!!
	 * This can only be used by unit test from remote dev environment
	 * This doesn't work on connection server
	 * @param hostname
	 * @param username
	 * @param password
	 */
	@Deprecated
	private LDAP(String hostname, String username, String password, String domain){

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + hostname + ":389/");

        env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
		 try {
			 dirContext = new InitialDirContext(env);
			} catch (NamingException e) {
				log.warn("Can't connect to server", e);
				e.printStackTrace();
			}

	}
	/**
	 * NEVER use it for released code!!!!
	 * This can only be used by unit test from remote dev environment
	 * This doesn't work on connection server
	 * @param hostname
	 * @param username
	 * @param password
	 */
	@Deprecated
	public static LDAP _get_junit_ldap(String hostname, String username, String password, String domain){
		return new LDAP(hostname, username, password, domain);
	}
	

	   
	public String getValue(String key){
		   if (dirContext ==null){
			   log.warn("Can't get since ctx is null!");
			   return null;
		   }
		SearchControls searchObject = new SearchControls();
		searchObject.setSearchScope(SearchControls.OBJECT_SCOPE);

		String ldapKey = "pae-" + key;
		String[] attributesToReturn = { ldapKey };
		NamingEnumeration<SearchResult> answers = null;

		try {

			answers = dirContext.search(COMMON_CONFIG_DN, LDAP_SEARCH_FILTER,
					attributesToReturn, searchObject);

			if (answers.hasMore()) {
				SearchResult sr = answers.next();
				return getAttribute(sr.getAttributes(), ldapKey, null);
			}
		} catch (Exception e) {
			/* Failed to get value, return default. */
		}

		return null;

	}

	public boolean getBool(String key, boolean def) {
		if (dirContext == null) {
			log.warn("Can't get since ctx is null!");
			return false;
		}
		String val = getValue(key);
		if (val == null) {
			return def;
		} else {
			/* By View convention, "1" is also a boolean True. */
			if ("1".equals(val)) {
				return true;
			}
			return Boolean.parseBoolean(val);
		}
	}

	public int getInt(String key, int defaultVal) {
		if (dirContext == null)
			return defaultVal;

		String val = getValue(key);

		int ret;
		try {
			ret = Integer.parseInt(val);
		} catch (Exception e) {
			ret = defaultVal;
		}
		return ret;
	}

	public void setAttribute(String key, Object value) {
		if (dirContext == null) {
			log.warn("Can't set since ctx is null!");
			return;
		}
		try {
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("pae-" + key, value);
			dirContext.modifyAttributes(COMMON_CONFIG_DN,
					DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static final String CEIPEnabled = "ceipEnabled";
	private static final String CEIPSpoolDirectory = "ceipSpoolDirectory";

	// return the ceip folder
	public void enableCEIP() {
		log.info("Try to set ceip");
		this.setAttribute(CEIPEnabled, "1");
	}

	public void disableCEIP() {
		log.info("Try to disable ceip");
		this.setAttribute(CEIPEnabled, "0");
		CEIPReportUtil.resetCount();
	}

	private static String viewServerPath;
	public static void setViewServerPath(String viewServerPath){
		LDAP.viewServerPath = viewServerPath;
	}
	public String getCEIPFolder() {
		String path = this.getValue(CEIPSpoolDirectory);
		if (!isEmptyString(path)) {
			return path;
		}

		String tempDir = LDAP.viewServerPath + File.separator + "broker" + File.separator + "temp"; 
		
		log.debug("CEIP directory is:" + tempDir);
		return tempDir;
	}

	private static boolean isEmptyString(String in) {
		return (in == null || in.trim().length() == 0);
	}

	public boolean isCEIPEnabled() {
		boolean ceipEnabled = this.getBool(CEIPEnabled, false);
		return ceipEnabled;

	}
	   
//	   public static void main(String args[]){
//		   LDAP ldap = new LDAP("10.112.118.27", "administrator", "ca$hc0w");
//		   ldap.enableCEIP();
//		   System.out.println(ldap.isCEIPEnabled());
//	   }
	   
}
