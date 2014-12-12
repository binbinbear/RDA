package com.vmware.vdi.admin.be.events;

/**
 * It defines the sources for event object in admin console.
 *
 * @author dliu
 *
 */
public class AdminEventSource {
    public static enum Type {
        POOL, DESKTOP, UDD, THINAPP, CVP, ERROR_CODE, APPLICATION, FARM, RDSSERVER
    }

    private String id;

    private String name;

    private Type type;

    /**
     * @param id
     * @param name
     * @param type
     */
    public AdminEventSource(String id, String name, Type type) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *                the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *                the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     *                the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
}
