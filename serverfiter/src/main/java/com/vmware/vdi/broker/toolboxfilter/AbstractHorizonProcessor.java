package com.vmware.vdi.broker.toolboxfilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;



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

    private boolean doParseInput() {

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