package com.vmware.vdi.admin.be.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.adam.AdamEventTemplateManager;
import com.vmware.vdi.adamwrapper.exceptions.ADAMServerException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.objects.EventTemplate;
import com.vmware.vdi.admin.ui.common.Util;
import com.vmware.vdi.events.enums.EventSeverity;

/**
 * The AdminEventManager provides the interfaces to retrieve and filter events.
 *
 * Events will be retrieved from either database or log files.
 *
 * @author dliu
 *
 */
public class AdminEventManager {
    private static Logger logger = Logger.getLogger(AdminEventManager.class);

    private static final AdminEventManager instance = new AdminEventManager();

    private Map<String, EventTemplate> localesToTemplates = null;

    /**
     * It returns the singleton instance of this object.
     *
     * @return The AdminEventManager object
     */
    static public AdminEventManager getInstance() {
        return instance;
    }

    private final List<AdminEventProvider> providers;

    /**
     * Constructor
     */
    private AdminEventManager() {
        this.providers = new ArrayList<AdminEventProvider>();
        this.providers.add(new AdminEventDbProvider());

        this.refreshEventTemplates();
    }



    /**
     * It returns the event template in thread locale.
     *
     * The eventTemplate lookup is performed in the following sequence:
     * 1. thread locale
     * 2. the default system locale
     * 3. en
     *
     * @param type
     *            The event type
     * @param defMessage
     *            The default template
     * @return The template in thread locale
     */
    public String getLocaleTemplate(String type, String defMessage) {
        Locale locale = Util.getThreadLocalLocale();
        EventTemplate template = lookupTemplateByLocale(locale);

        if (template == null) {
            Locale systemLocale = Locale.getDefault();
            template = lookupTemplateByLocale(systemLocale);

            if (template == null) {
                template = this.localesToTemplates.get(Locale.ENGLISH
                        .getLanguage());
            }
        }
        if (template != null) {
            String message = template.getIdsToMessages().get(type);
            if (message != null) {
                return message;
            }
        }
        // if no message is found, the default message is returned
        return defMessage;
    }

    /**
     * It returns the total event counts by the filter.
     *
     * @param ctx
     *            The VDIContext
     * @param filter
     *            The event filter
     * @return The count of events
     */
    public int getEventCounts(VDIContext ctx, AdminEventFilter filter) {
        Map<EventSeverity, Integer> severities2counts = this.getSeverityCounts(
                ctx, filter);

        int count = 0;
        if (severities2counts != null) {
            for (EventSeverity severity : severities2counts.keySet()) {
                int severitycount = severities2counts.get(severity);
                count += severitycount;
            }
        }
        return count;
    }

    /**
     * It returns the event counts for each severity.
     *
     * @param ctx
     *            The VDIContext
     * @param filter
     *            The event filter
     * @return The list of event severities and their event counts
     */
    public Map<EventSeverity, Integer> getSeverityCounts(VDIContext ctx,
            AdminEventFilter filter) {
        Map<EventSeverity, Integer> counts = null;

        long starttime = System.currentTimeMillis();
        for (AdminEventProvider provider : this.providers) {
            counts = provider.getSeverityCounts(ctx, filter);
            if (counts != null) {
                break;
            }
        }
        long endtime = System.currentTimeMillis();
        logger.debug("Loading event severity (ms): " + (endtime - starttime));

        return counts;
    }

    /**
     * It returns the list of event objects which meet the specific filter.
     *
     * @param cxt
     *            The VDIContext
     * @param filter
     *            The filter requirement
     * @return The list of event objects
     * @throws Exception
     */
    public List<AdminEvent> getEventList(VDIContext ctx, AdminEventFilter filter)
            throws Exception {
        List<AdminEvent> events = null;

        long starttime = System.currentTimeMillis();
        for (int i = 0; i < this.providers.size(); i++) {
            AdminEventProvider provider = this.providers.get(i);
            try {
                events = provider.getEventList(ctx, filter);
                if (events != null) {
                    break;
                }
            } catch (Throwable e) {
                if (i < this.providers.size() - 1) {
                    // if this is not the last provider, give others a chance
                    continue;
                }
                if (e instanceof ADAMServerException) {
                    throw (ADAMServerException) e;
                } else {
                    throw new ADAMServerException(
                            ADAMServerException.ERROR_EVENT_DATABASE_CONNECT);
                }
            }
        }
        long endtime = System.currentTimeMillis();
        logger.debug("Loading events (ms): " + (endtime - starttime));

        if (events == null) {
            // throw exception for invalid database configuration
            throw new ADAMServerException(
                    ADAMServerException.ERROR_EVENT_DATABASE_CONFIG);
        }
        return events;
    }

    /**
     * It loads the event templates into memory and cached.
     */
    private void refreshEventTemplates() {
        this.localesToTemplates = new HashMap<String, EventTemplate>();

        VDIContext ctx = null;
        try {
            ctx = VDIContext.getDefaultContext();
            List<EventTemplate> templates = AdamEventTemplateManager
                    .getInstance().getAll(ctx);
            for (EventTemplate template : templates) {
                this.localesToTemplates.put(template.getId(), template);
            }
        } catch (ADAMServerException e) {
            logger.debug("Failed to load event templates", e);
        } finally {
            VDIContext.release(ctx);
        }
    }

    /**
     * For a given locale, the sequence to search a template is:
     *             <language>_<country>
     *             <language>
     * @param locale
     * @return
     */
    private EventTemplate lookupTemplateByLocale(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        EventTemplate template = null;

        if (country.length() > 0) {
            String localeId = String.format("%s_%s", language, country);
            template = this.localesToTemplates.get(localeId);
         }

        if (language.length() > 0 && template == null) {
            template = this.localesToTemplates.get(language);
        }
        return template;
    }
}
