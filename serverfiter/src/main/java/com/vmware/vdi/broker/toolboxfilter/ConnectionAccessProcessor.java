package com.vmware.vdi.broker.toolboxfilter;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Processor for the get-desktop-connection message.
 */
public class ConnectionAccessProcessor extends AbstractHorizonProcessor {

	public ConnectionAccessProcessor(Element element) {
		super(element);
	}
	 private static final Logger log = Logger.getLogger(ConnectionAccessProcessor.class);
	//TODO: this is just a place holder for white/black list. Please add logic here.

	private static int i=0;
	@Override
	public boolean isMessageAllowed() {
		log.info("Get reqeust from IP:" + super.envInfo.get("IP_Address"));

		return i++%2==0;
		//return false;
	}





}