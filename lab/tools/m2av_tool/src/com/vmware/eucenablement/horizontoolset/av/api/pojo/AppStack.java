package com.vmware.eucenablement.horizontoolset.av.api.pojo;

/*
 * gson parsed object
 */
public class AppStack {

	public long id;
	public String name;
	public String name_html;
	public String path;
	public String datastore_name;
	public String status;
	public String create_at;
	public String created_at_human;
	public String mounted_at;
	public String mounted_at_human;
	public String temlapte_version;
	public boolean assigned;
	public int mount_count;
	public int size_mb;
	public int assignments_total;
	public int attachments_total;

	public String description;
	public String file_location;
	public String filename;
	public String primordial_os_id;
	public String primordial_os_name;
	public String provision_duration;
	public String template_file_name;
	public String volume_guid;

	public AppStack() {
	}

	public AppStack(long id, String name, String name_html, String path, String datastore_name, String status, String create_at, String created_at_human,
			String mounted_at, String mounted_at_human, String temlapte_version, boolean assigned, int mount_count, int size_mb, int assignments_total,
			int attachments_total, String description, String file_location, String filename, String primordial_os_id, String primordial_os_name,
			String provision_duration, String template_file_name, String volume_guid) {
		super();
		this.id = id;
		this.name = name;
		this.name_html = name_html;
		this.path = path;
		this.datastore_name = datastore_name;
		this.status = status;
		this.create_at = create_at;
		this.created_at_human = created_at_human;
		this.mounted_at = mounted_at;
		this.mounted_at_human = mounted_at_human;
		this.temlapte_version = temlapte_version;
		this.assigned = assigned;
		this.mount_count = mount_count;
		this.size_mb = size_mb;
		this.assignments_total = assignments_total;
		this.attachments_total = attachments_total;
		this.description = description;
		this.file_location = file_location;
		this.filename = filename;
		this.primordial_os_id = primordial_os_id;
		this.primordial_os_name = primordial_os_name;
		this.provision_duration = provision_duration;
		this.template_file_name = template_file_name;
		this.volume_guid = volume_guid;
	}
}
