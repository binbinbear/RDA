package com.vmware.vdi.broker.toolboxfilter;

import java.util.List;

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
		List<DeviceFilterPolicy> policies = storage.policies.get();
		log.info("Number of Access Policy in Storage:"+ policies.size());
		for (DeviceFilterPolicy policy: policies){
			String policypool = policy.getPoolName();
			String requestpool = StringUtil.isEmpty(super.desktopID)? super.applicationID: super.desktopID ;
			if (!StringUtil.isEmpty(policypool) && policypool.equalsIgnoreCase(requestpool)){
				log.info("Policy is matched, pool:"+ policypool);
				return policy.checkAccess(super.envInfo);
			}else{
				log.info("Policy is not matched, pool in policy:"+ policypool+ " request pool:"+ requestpool);
			}
		}

		return true;
	}





}