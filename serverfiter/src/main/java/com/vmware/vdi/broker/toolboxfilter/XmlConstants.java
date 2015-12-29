package com.vmware.vdi.broker.toolboxfilter;

public interface XmlConstants {


    public static final float MINIMUM_VERSION = 1.0f;

    public static final float CURRENT_VERSION = 11.0f;

    /*
     * This must be kept in-sync with the XML in the JSP used to generate
     * responses (xml.jsp)
     */
    public static final String ATT_VERSION = "version";

    /*
     * This must be kept in-sync with the XML in the JSP used to generate
     * responses (xml.jsp)
     */
    public static final String EL_ROOT_NAME = "broker";

    public static final String ATT_XML_REQUEST_PROCESSOR = "processor";

    /*
     * Name of application scope attribute for storing the XML API version, for
     * use by xml.jsp
     */
    public static final String ATT_BROKER_XML_API_VERSION = "brokerXmlApiVersion";

    /*
     * Supported feature names reported by the client.
     */
    public static final String FEATURE_LAST_USER_ACTIVITY = "lastUserActivity";

    public static final String FEATURE_REAUTHENTICATION = "reauthentication";

    public static final String FEATURE_NAME_RESOLUTION = "nameResolution";

    public static final String FEATURE_IPV6 = "ipv6";
}