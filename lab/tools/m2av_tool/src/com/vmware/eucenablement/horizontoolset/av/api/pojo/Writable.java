package com.vmware.eucenablement.horizontoolset.av.api.pojo;

public class Writable {
    public long id;
    public String name;
    public String name_html;
    public String owner;
    public String link;
    public String owner_name;
    public String owner_type;
    public String description;
    public String created_at;
    public String created_at_human;
    public String mounted_at;
    public String mounted_at_human;
    public String mount_count;
    public String attached;
    public String status;
    public String version_tag;
    public String block_login;
    public String mount_prefix;
    public String defer_create;
    public String size_mb;
    public String template_version;
    public String datastore_name;
    public String machine_manager_host;
    public String machine_manager_type;
    public String path;
    public String filename;
    public String file_location;
    public String template_file_name;
    public String protectedItem;
    public String free_mb;
    public String total_mb;
    public String percent_available;
    public String can_expand;
    public String primordial_os_id;
    public String primordial_os_name;


    public Writable() {

    }

    public Writable(long id, String name, String name_html,String owner, String link,
                    String owner_name, String owner_type, String description, String created_at,
                    String created_at_human, String mounted_at, String mounted_at_human, String mount_count,
                    String attached, String status, String version_tag, String block_login, String mount_prefix,
                    String defer_create, String size_mb, String template_version, String datastore_name,
                    String machine_manager_host, String machine_manager_type, String path, String filename,
                    String file_location, String template_file_name, String protectedItem, String free_mb,
                    String total_mb, String percent_available, String can_expand, String primordial_os_id,String primordial_os_name) {
        this.id = id;
        this.name = name;
        this.name_html = name_html;
        this.owner = owner;
        this.link = link;
        this.owner_name = owner_name;
        this.owner_type = owner_type;
        this.description = description;
        this.created_at = created_at;
        this.created_at_human = created_at_human;
        this.mounted_at = mounted_at;
        this.mounted_at_human = mounted_at_human;
        this.mount_count = mount_count;
        this.attached = attached;
        this.status = status;
        this.version_tag = version_tag;
        this.block_login = block_login;
        this.mount_prefix = mount_prefix;
        this.defer_create = defer_create;
        this.size_mb = size_mb;
        this.template_version = template_version;
        this.datastore_name = datastore_name;
        this.machine_manager_host = machine_manager_host;
        this.machine_manager_type = machine_manager_type;
        this.path = path;
        this.filename = filename;
        this.file_location = file_location;
        this.template_file_name = template_file_name;
        this.protectedItem = protectedItem;
        this.free_mb = free_mb;
        this.total_mb = total_mb;
        this.percent_available = percent_available;
        this.can_expand = can_expand;
        this.primordial_os_id = primordial_os_id;
        this.primordial_os_name = primordial_os_name;
    }


}
