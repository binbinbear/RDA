package com.vmware.vdi.broker.toolboxfilter;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XmlRequestProcessor {

    private static final Logger log = Logger
            .getLogger(XmlRequestProcessor.class);

    private Element rootElement = null;


    private List<XmlMessageProcessor> xmlMessageProcessors = new LinkedList<XmlMessageProcessor>();


    public boolean isAllowed(){
    	log.info("Is allowed is called");
    	for (XmlMessageProcessor message: xmlMessageProcessors){
    		if (!message.isMessageAllowed()){
    			return false;
    		}

    	}
    	return true;
    }

    public XmlRequestProcessor(HttpServletRequest request) {

        try {
        	SAXBuilder builder = new SAXBuilder();

            Document document = builder.build(request.getReader());
            log.debug("read XML input");

            rootElement = document.getRootElement();
            addMessageProcessors();

        } catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Exception when paring xml request",e);
		}
    }

    private void addMessageProcessors() {
    	log.info("add message processors");
        for (Object o : rootElement.getChildren()) {

            Element message = (Element) o;

            String messagename = message.getName();
            log.info("message name:"+messagename);
            if (messagename.equalsIgnoreCase("get-desktop-connection") || messagename.equalsIgnoreCase("get-application-connection") ){
            	log.info("get desktop connection message");
            	XmlMessageProcessor processor = new ConnectionAccessProcessor(message);
            	xmlMessageProcessors.add(processor);
            }

        }
    }




}