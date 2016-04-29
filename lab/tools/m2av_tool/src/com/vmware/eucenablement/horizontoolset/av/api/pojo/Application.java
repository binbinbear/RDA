package com.vmware.eucenablement.horizontoolset.av.api.pojo;


/*
 * gson parsed object
 */
public class Application {
	public String name;
	public String icon;
	public String img_icon;
	public String version;
	public String publisher;

	public Application() {
	}

	public Application(String name, String icon, String img_icon, String version, String publisher) {
		super();
		this.name = name;
		this.icon = icon;
		this.img_icon = img_icon;
		this.version = version;
		this.publisher = publisher;
	}

}
