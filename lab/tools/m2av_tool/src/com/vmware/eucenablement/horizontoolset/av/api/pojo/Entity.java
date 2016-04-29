package com.vmware.eucenablement.horizontoolset.av.api.pojo;
/*
 * gson parsed object
 */
public class Entity {
	public String name;
	public String entity_type;
	public String mount_prefix;
	public String host;
public Entity() {
}
	public Entity(String name, String entity_type, String mount_prefix, String host) {
		super();
		this.name = name;
		this.entity_type = entity_type;
		this.mount_prefix = mount_prefix;
		this.host = host;
	}

}
