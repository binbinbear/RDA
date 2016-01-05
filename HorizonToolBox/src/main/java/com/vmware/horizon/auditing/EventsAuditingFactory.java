package com.vmware.horizon.auditing;

import com.vmware.horizon.auditing.impl.EventsAuditingImpl;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;

public class EventsAuditingFactory {
	/**
	 * 
	 * @param vdiContext   type of VDIContext 
	 * @return EventsAuditing
	 * @throws ADAMConnectionFailedException
	 */
	public static EventsAuditing getEventsAuditing(VDIContext vdiContext) throws ADAMConnectionFailedException{
		return new EventsAuditingImpl(vdiContext);
	}
}
