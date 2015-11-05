package com.vmware.vdi.admin.be.events;

import java.util.List;
import java.util.Map;

import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.events.enums.EventSeverity;

/**
 * The interface IAdminEventProvider defines the APIs for event sources.
 *
 *
 */
public interface AdminEventProvider {
    /**
     * It returns the event counts for each severity.
     *
     * @param ctx
     *                The VDIContext
     * @param filter
     *                The filter requirement
     * @return The list of event severities and their counts
     */
    public Map<EventSeverity, Integer> getSeverityCounts(VDIContext ctx,
            AdminEventFilter filter);

    /**
     * It returns the list of event objects which meet the specific filter.
     *
     * @param ctx
     *                The VDIContext
     * @param filter
     *                The filter requirement
     * @return The list of event objects
     */
    public List<AdminEvent> getEventList(VDIContext ctx, AdminEventFilter filter)
        throws Exception;
}
