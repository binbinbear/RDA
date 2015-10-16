/**
 * Util.java
 * Copyright (C) 2006 VMware, Inc.
 * All Rights Reserved.
 */
package com.vmware.vdi.admin.ui.common;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.thymeleaf.util.StringUtils;

import com.vmware.vdi.adamwrapper.ad.DirectoryQueryManager;
import com.vmware.vdi.adamwrapper.exceptions.DirectoryQueryException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.admin.be.events.AdminEvent;


/**
 * This class holds utility functions.
 *
 * @author vivian
 *
 */
public class Util {
    private static Logger LOGGER = Logger.getLogger(Util.class);

    // the message bundle for UI string
    public static final String MESSAGE_BUNDLE = "com.vmware.vdi.admin.ui.messages.MessageBundle";

    public static final int MAX_LEN_COMPUTERNAME = 15;

    public static final int MIN_LEN_COMPUTERNAME = 1;

    public static String ROOT_FOLDER_ID = null;

    public static String ROOT_FOLDER_DISP = null;



    public static final long KB = 1024;

    public static final long MB = KB * 1024;

    public static final long GB = MB * 1024;

    public static final long TB = GB * 1024;

    private static final String DEFAULT_MONITOR_NUM = "2";

    private static final String DEFAULT_RESOLUTION = "1920x1200";

    public static final long WEEK_AGO_MS = 7 * 24 * 60 * 60 *  1000;

    private static final String USER_OR_GROUP_ID_PREFIX = "UserOrGroup/";



    /**
     * Utility method to check if 2 strings are the same
     *
     * @param str1
     * @param str2
     * @return true if both are null or same content
     */
    public static boolean isSame(String str1, String str2) {
        if ((str1 == null) && (str2 == null)) {
            return true;
        } else if ((str1 != null) && (str2 != null) && (str1.equals(str2))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * It returns the thread local locale. It is the locale of the client side.
     *
     * @return The client locale
     */
    public static Locale getThreadLocalLocale() {
        return Locale.ENGLISH;

    }

    /**
     * It returns the resource bundle for the client side locale.
     *
     * @return The resource bundle for client side locale
     */
    private static ResourceBundle getThreadResourceBundle(Locale locale) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = Util.class.getClassLoader();
        }
        ResourceBundle bundle = ResourceBundle.getBundle(MESSAGE_BUNDLE,
                locale, loader);
        return bundle;
    }

    /**
     * The internal method to get a message from resource bundles by the given
     * key and locale.
     *
     * @param key
     * @param locale
     * @return message
     */
    private static String getStringByLocale(String key, Locale locale) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }

        try {
            ResourceBundle bundle = getThreadResourceBundle(locale);
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            LOGGER.error("Missing message in MessageBundle for key: " + key);
            return key;
        } catch (NullPointerException ex) {
            LOGGER.error("Could not obtain message for key: " + key);
            return key;
        }
    }

    /**
     * The method to get a localized String from msg bundle.
     */
    public static String getString(String key) {
//        Locale locale = getThreadLocalLocale();
//        return getStringByLocale(key, locale);
    	return key;
    }
    /**
     * It returns the localized string representation for date value.
     *
     * @param englishDate
     *            It takes the date in English
     * @param DateStyle
     *            dateStyle DateFormat.SHORT, MEDIUM, LONG or FULL
     * @param TimeStyle
     *            timeStyle DateFormat.SHORT, MEDIUM, LONG or FULL
     * @return Date as a localized string
     */
    public static String getLocalizedDateString(java.util.Date date,
            int dateStyle, int timeStyle) {
        if (date != null) {
            Locale myLocale =  Locale.US;

            if (!isValidStyle(dateStyle)) {
                dateStyle = DateFormat.MEDIUM;
            }
            if (!isValidStyle(timeStyle)) {
                timeStyle = DateFormat.LONG;
            }

            DateFormat df = DateFormat.getDateTimeInstance(dateStyle,
                    timeStyle, myLocale);
            return df.format(date).toString();
        } else {
            return "";
        }
    }
    private static boolean isValidStyle(int val) {
        if ((val == DateFormat.FULL) || (val == DateFormat.LONG)
                || (val == DateFormat.SHORT) || (val == DateFormat.MEDIUM)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Helper method to normalize a RDN/DN SessionReporing needs to use
     * normalized DNs
     *
     * @param dn
     *            The RDN/DN to normalize.
     *
     * @return Normalized RDN/DN
     */
    public static String normalize(String dn) {
        if (dn != null) {
            StringTokenizer st = new StringTokenizer(dn, ",");
            StringBuilder sb = new StringBuilder();
            while (st.hasMoreTokens()) {
                if (sb.length() != 0)
                    sb.append(",");
                sb.append(st.nextToken().trim());
            }
            return sb.toString().toLowerCase();
        } else {
            return null;
        }
    }


   
    
    /**
     * The method to get a String in English from msg bundle.
     */
    public static String getNonLocalizedString(String key) {
        return getStringByLocale(key, Locale.ENGLISH);
    }

    /**
     * The internal method to get a message from resource bundles by the given
     * key and locale.
     *
     * @param key
     * @param locale
     * @return message
     */
    private static String getStringByLocale(String key, Locale locale,
            Object... params) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }

        ResourceBundle bundle = getThreadResourceBundle(locale);

        String result = null;

        try {
            String pattern = bundle.getString(key);
            MessageFormat format = new MessageFormat(pattern, locale);
            result = format.format(params);
        } catch (MissingResourceException ex) {
            LOGGER.error("Missing message in MessageBundle for key: " + key);
            result = key;
        }

        return result;
    }

    /**
     * The method to get a localized String from msg bundle with parameter.
     *
     * @param key
     *            the key in resource property file
     * @param params
     *            the parameters
     * @return the localized string.
     */
    public static String getString(String key, Object... params) {
        Locale locale = getThreadLocalLocale();
        return getStringByLocale(key, locale, params);
    }

    /**
     * The method to get a String in English from msg bundle with parameter.
     *
     * @param key
     *            the key in resource property file
     * @param params
     *            the parameters
     * @return the localized string.
     */
    public static String getNonLocalizedString(String key, Object... params) {
        return getStringByLocale(key, Locale.ENGLISH, params);
    }

    /**
     * Return an number in string formatted by the locale.
     *
     * @param number
     * @return
     */
    public static String getLocalizedNumber(long number) {
        return NumberFormat.getInstance(Util.getThreadLocalLocale()).format(
                number);
    }

    /**
     * Get the domains associated with the specified VDI context.
     *
     * @param inContext
     *            VDI context
     * @return list of domains
     */
    public static Collection<String> getDomains(VDIContext inContext) {
        Collection<String> domainList = new ArrayList<String>();
        domainList.add(getString("EntireDirectory"));
        try {
            List<String> domains = DirectoryQueryManager.Factory.getInstance()
                    .getLocationList(inContext);
            for (String domainName : domains) {
                domainList.add(domainName);
            }
        } catch (DirectoryQueryException e) {
           LOGGER.error("Can't get domains",e);
        }

        return domainList;
    }





    /**
     * Parse vmIs from comma seperated string to a ArrayList of string.
     *
     * @return ArrayList of ids.
     */
    public static ArrayList<String> parseIds(String ids) {
        if ((ids == null) || ("".equals(ids.trim()))) {
            return new ArrayList<String>();
        }
        ArrayList<String> idList = new ArrayList<String>();
        StringTokenizer token = new StringTokenizer(ids, ",");
        while (token.hasMoreTokens()) {
            idList.add(token.nextToken());
        }
        return idList;
    }




    public static boolean isEmpty(String val) {
        if ((val != null) && (val.trim().length() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getFilterString(String[] fieldNames) {
        switch (fieldNames.length) {
        case 0:
            return "";
        case 1:
            return Util.getString("FilterOneField", fieldNames);
        case 2:
            return Util.getString("FilterTwoFields", fieldNames);
        case 3:
            return Util.getString("FilterThreeFields", fieldNames);
        case 4:
            return Util.getString("FilterFourFields", fieldNames);
        case 5:
            return Util.getString("FilterFiveFields", fieldNames);
        case 6:
            return Util.getString("FilterFields", fieldNames);
        default:
            throw new RuntimeException("Max of five filter fields allowed");
        }
    }


    /**
     * It returns the localized string for event module.
     *
     * @param event
     *            The event object
     * @return The localized event mdoule
     */
    public static String getLocalizedEventModule(AdminEvent event) {
        if (event == null) {
            return "";
        }
        switch (event.getModule()) {
        case Admin:
            return getString("AdminUiEventModule");
        case Broker:
            return getString("BrokerEventModule");
        case Endpoint:
            return getString("EndpointEventModule");
        case Tunnel:
            return getString("TunnelEventModule");
        case Framework:
            return getString("FrameworkEventModule");
        case Client:
            return getString("ClientEventModule");
        case Agent:
            return getString("AgentEventModule");
        case Vlsi:
            return getString("ViewApiModule");
        default:
            return "Unknown";
        }
    }



    public static String convertEmptyStringToNull(String input) {
        if (Util.isEmpty(input)) {
            return null;
        } else {
            return input;
        }
    }
}
