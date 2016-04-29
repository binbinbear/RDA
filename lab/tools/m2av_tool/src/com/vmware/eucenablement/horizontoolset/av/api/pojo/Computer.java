package com.vmware.eucenablement.horizontoolset.av.api.pojo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * gson parsed object
 */
public class Computer {

	private long _id;
	public String upn;
	private String _realName;
	private String _domain;
	public String name;
	public boolean enable;
	public long writables;
	public long appstacks;
	public long attachments;
	public String agent_version;
	public String os;
	public long logins;
	public String last_login;
	public String last_login_human;

	public Computer() {

	}

	

	public Computer(String upn, String name, boolean enable, long writables, long appstacks, long attachments, String agent_version, String os, long logins,
			String last_login, String last_login_human) {
		super();
		this.upn = upn;
		this.name = name;
		this.enable = enable;
		this.writables = writables;
		this.appstacks = appstacks;
		this.attachments = attachments;
		this.agent_version = agent_version;
		this.os = os;
		this.logins = logins;
		this.last_login = last_login;
		this.last_login_human = last_login_human;
	}



	public long getId() {
		Pattern pattern = Pattern.compile("<a href=.*?title=");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			token = matcher.group().substring(matcher.group().lastIndexOf('/') + 1, matcher.group().length() - 8);
		}
		this._id = Long.parseLong(token);
		return _id;
	}

	public String getRealName() {
		Pattern pattern = Pattern.compile(">.*?</a>");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			token = matcher.group().substring(matcher.group().indexOf('\\') + 1, matcher.group().length() - 5);
		}
		this._realName = token;
		return _realName;
	}

	public String getDomain() {
		Pattern pattern = Pattern.compile(">.*?</a>");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			token = matcher.group().substring(matcher.group().indexOf('>') + 1, matcher.group().indexOf('\\'));
		}
		this._domain = token;
		return _domain;
	}

	public String getDomainName() {
		getRealName();
		getDomain();
		if (null != this._realName && null != this._domain)
			return this._realName + "." + this._domain;
		else
			return this._realName;

	}

}
