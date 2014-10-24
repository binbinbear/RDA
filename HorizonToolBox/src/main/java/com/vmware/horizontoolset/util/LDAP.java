package com.vmware.horizontoolset.util;

import java.net.InetAddress;
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

import com.vmware.vdi.common.winauth.UserContext;
import com.vmware.vdi.common.winauth.UserContextFactory;
import com.vmware.vdi.common.winauth.sasl.WinAuthSASLClient;
import com.vmware.horizontoolset.report.CEIPReportUtil;
public class LDAP{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(LDAP.class);
	private static final String ADAM_URL_PROPERTY_NAME = "ADAM_URL";

	private Hashtable<String, Object> env = new Hashtable<String, Object>();
	private DirContext ctx;
	private static final String COMMON_CONFIG_DN = "CN=Common,OU=Global,OU=Properties";

	private static final String LDAP_SEARCH_FILTER = "(objectClass=pae-VDMProperties)";
	private static final String BINARY_ATTRIBUTES = "java.naming.ldap.attributes.binary";
	private static final String BASE_DN = "dc=vdi,dc=vmware,dc=int";

	public void close() {
		log.info("Release resource: Start to close the ctx");
		try {
			if (ctx != null) {
				this.ctx.close();
				this.ctx = null;
			}
		} catch (NamingException e) {
			log.warn("Can't close the ctx", e);
		}
	}

	// TODO: FIXME: only local host is supported by this default context.
	public LDAP(String servername, String domain, String username,
			String password) {

		try {
			UserContext user = UserContextFactory.userContext(domain, username,
					password);

			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			String server = System.getProperty(ADAM_URL_PROPERTY_NAME,
					"ldap://" + servername + ":389");

			log.info("LDAP Server :" + server);
			env.put(Context.PROVIDER_URL, server + "/" + BASE_DN);
			env.put(Context.SECURITY_AUTHENTICATION, WinAuthSASLClient.NAME);
			String fqdn = InetAddress.getByName(servername).getHostName();
			env.put(WinAuthSASLClient.SPN, "ldap/" + fqdn);
			env.put(WinAuthSASLClient.USER, user);
			env.put(BINARY_ATTRIBUTES,
					"pae-SecurIDConf pae-DisplayIcon pae-IconData");
			/*
			 * enable referrals
			 */
			env.put(Context.REFERRAL, "follow");

			ctx = new InitialDirContext(env);
		} catch (Exception e) {
			log.warn("Can't connect to server", e);
			e.printStackTrace();
		}

	}

	protected static String getAttribute(Attributes attributes, String attrId,
			String defaultValue) {
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

	public String getValue(String key) {
		if (ctx == null) {
			log.warn("Can't get since ctx is null!");
			return null;
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
	public LDAP(String hostname, String username, String password){

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + hostname + ":389/"
                + BASE_DN);

        env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
		 try {
				ctx = new InitialDirContext(env);
			} catch (NamingException e) {
				log.warn("Can't connect to server", e);
				e.printStackTrace();
			}

	}
	
	
	
	   protected String getAttribute(Attributes attributes, String attrId,
		         String defaultValue) {
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
	   
	public String getValue(String key){
		   if (ctx ==null){
			   log.warn("Can't get since ctx is null!");
			   return null;
		   }
		SearchControls searchObject = new SearchControls();
		searchObject.setSearchScope(SearchControls.OBJECT_SCOPE);

		String ldapKey = "pae-" + key;
		String[] attributesToReturn = { ldapKey };
		NamingEnumeration<SearchResult> answers = null;

		try {

			answers = ctx.search(COMMON_CONFIG_DN, LDAP_SEARCH_FILTER,
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
		if (ctx == null) {
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
		if (ctx == null)
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
		if (ctx == null) {
			log.warn("Can't set since ctx is null!");
			return;
		}
		try {
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("pae-" + key, value);
			ctx.modifyAttributes(COMMON_CONFIG_DN,
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

	public String getCEIPFolder() {
		String path = this.getValue(CEIPSpoolDirectory);
		if (!isEmptyString(path)) {
			return path;
		}

		String tempDir = System.getProperty("java.io.tmpdir");
		if (tempDir == null || tempDir.length() == 0) {
			tempDir = "tmp";
		}
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
//	   }
	   
}
