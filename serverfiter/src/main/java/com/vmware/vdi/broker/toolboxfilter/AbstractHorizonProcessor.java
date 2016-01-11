package com.vmware.vdi.broker.toolboxfilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.vmware.vdi.broker.toolboxfilter.util.StringUtil;



/**
 * Base class for the get-desktop-connection, get-application-connection and
 * get-application-session-connection message Processors.
 */
public abstract class AbstractHorizonProcessor extends AbstractProcessor {

    public AbstractHorizonProcessor(Element element) {
		super(element);
		doParseInput();
	}

	private static final Logger log = Logger
            .getLogger(AbstractHorizonProcessor.class);



    protected Map<String, String> envInfo = null;

    protected String desktopID = null;
    protected String applicationID = null;

    //full name is like "cn=app1,ou=applications,dc=vdi,dc=vmware,dc=int", shortname is "app1"
    private String getIDFromFullName(String fullname){
    	log.info("full name:"+fullname);
    	if (!StringUtil.isEmpty(fullname)){

    		int start = fullname.indexOf("cn=");
    		int end = fullname.indexOf(",");
    		if (start>=0 && end >start+3){
    			return fullname.substring(start+3, end);
    		}
    	}
    	return null;
    }
    private boolean doParseInput() {
        Element desktopEl = element.getChild("desktop-id");
        if (desktopEl != null){
        	desktopID = getIDFromFullName(desktopEl.getText());
        	log.info("desktopID:"+desktopID);
        }else{
        	log.info("no desktop id");
        }

        Element applicationEl = element.getChild("application-id");
        if (applicationEl != null){
        	applicationID = getIDFromFullName(applicationEl.getText());
        	log.info("application ID:"+applicationID);
        }

        Element envInfoEl = element.getChild("environment-information");
        if (envInfoEl != null) {
            envInfo = new HashMap<String, String>();
            List<?> infos = envInfoEl.getChildren("info");

            for (Object info : infos) {
                Element infoEl = (Element) info;

                String name = infoEl.getAttributeValue("name");
                String value = infoEl.getText();

                if (StringUtils.isBlank(name)) {

                    return false;
                }
                envInfo.put(name, value);
            }
        }

        /*
         * Only test for timezones if environment info has been provided.
         */
        if (envInfo != null) {


            // List environment information in trace
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Environment Info: ");
                for (Map.Entry<String, String> en : envInfo.entrySet()) {
                    sb.append(en.getKey()).append('=').append(en.getValue())
                            .append("; ");
                }
                log.trace(sb.toString());
            }
        }


        return true;
    }




}