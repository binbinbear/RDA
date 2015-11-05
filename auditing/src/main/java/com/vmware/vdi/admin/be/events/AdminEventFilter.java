package com.vmware.vdi.admin.be.events;

/**
 * It defines what kind of events need to be displayed on admin's screen.
 *
 *
 */
public class AdminEventFilter {
    private String filterText;

    private boolean filterMessages = true;

    private boolean filterTime = false;

    private boolean filterType = false;

    private int filterDays = 1;

    private int pageIndex = 0;

    private int pageSize = 0;

    private AdminEventSource source = null;

    private String sid = null;

    /**
     * Quickly check if we need to do any filtering
     *
     * @return true if there are filter conditions to be met, false otherwise
     */
    public boolean hasFilterConditions() {
        return (filterText != null)
                && (filterMessages || filterTime || filterType);
    }

    /**
     * Get the current filter text
     *
     * @return
     */
    public String getFilterText() {
        return filterText;
    }

    /**
     * Set the filter text
     *
     * @param filterText
     */
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    /**
     * Are we filtering messages?
     *
     * @return
     */
    public boolean isFilterMessages() {
        return filterMessages;
    }

    /**
     * Set message filtering on / off
     *
     * @param filterMessages
     */
    public void setFilterMessages(boolean filterMessages) {
        this.filterMessages = filterMessages;
    }

    /**
     * Are we filtering by time?
     *
     * @return
     */
    public boolean isFilterTime() {
        return filterTime;
    }

    /**
     * Turn time filtering on / off
     *
     * @param filterTime
     */
    public void setFilterTime(boolean filterTime) {
        this.filterTime = filterTime;
    }

    /**
     * Are we filtering by type?
     *
     * @return
     */
    public boolean isFilterType() {
        return filterType;
    }

    /**
     * Turn type filtering on / off
     *
     * @param filterType
     */
    public void setFilterType(boolean filterType) {
        this.filterType = filterType;
    }

    /**
     * @return the filterDays
     */
    public int getFilterDays() {
        return filterDays;
    }

    /**
     * @param filterDays
     *            the filterDays to set
     */
    public void setFilterDays(int filterDays) {
        this.filterDays = filterDays;
    }

    /**
     * @return the pageIndex
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * @param pageIndex
     *            the pageIndex to set
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the source
     */
    public AdminEventSource getSource() {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(AdminEventSource source) {
        this.source = source;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * @param sid
     *            the sid to set
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

}
