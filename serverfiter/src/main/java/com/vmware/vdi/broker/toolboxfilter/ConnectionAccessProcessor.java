package com.vmware.vdi.broker.toolboxfilter;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.vmware.vdi.broker.devicefilter.DeviceFilterPolicy;
import com.vmware.vdi.broker.devicefilter.FilterStorage;
import com.vmware.vdi.broker.toolboxfilter.util.StringUtil;

/**
 * Processor for the get-desktop-connection message.
 */
public class ConnectionAccessProcessor extends AbstractHorizonProcessor {

	private static FilterStorage storage = new FilterStorage();
	public ConnectionAccessProcessor(Element element) {
		super(element);
	}
	 private static final Logger log = Logger.getLogger(ConnectionAccessProcessor.class);

	@Override
	public boolean isMessageAllowed() {
		String requestpool = StringUtil.isEmpty(super.desktopID)? super.applicationID: super.desktopID ;

		DeviceFilterPolicy policy = storage.getPolicy(requestpool);

		if (policy!=null && policy.getItems()!=null && policy.getItems().size()>0){
			log.info("Policy is found, pool:"+ requestpool);
			return policy.checkAccess(super.envInfo);
		}else{
			log.info("Policy is not matched,  request pool:"+ requestpool);
		}

		return true;
	}





}