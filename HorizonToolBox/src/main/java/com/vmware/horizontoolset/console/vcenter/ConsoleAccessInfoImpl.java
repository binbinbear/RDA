package com.vmware.horizontoolset.console.vcenter;

import java.util.Properties;



public class ConsoleAccessInfoImpl implements com.vmware.horizontoolset.console.ConsoleAccessInfo {

	protected String type;
	protected String host;
	protected int port;
	protected String protocol;
	protected String uri;
	protected String thumbprint;
	protected Properties props = new Properties();
	
	public ConsoleAccessInfoImpl(String type) {
		this.type = type;
	}
	
	public String getUri() throws Exception {
		return uri;
	}
	
	public String getAttribute(String name) {
		return props.getProperty(name);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}



	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getThumbprint() {
		return thumbprint;
	}

	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String toString() {
		String s = "ConsoleAccessInfo: ";
		s += "type=" + getType();
		s += ", host=" + getHost();
		s += ", port=" + getPort();
		s += ", protocol=" + getProtocol();
		s += ", uri=" + uri;
		s += ", thumbprint=" + getThumbprint();
		s += ", props=" + props;
		
		return s;
	}
}
