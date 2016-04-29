package com.vmware.eucenablement.horizontoolset.av.api.pojo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * gson parsed object
 */
public class User {

	private long _id;
	public String upn;
	public String name;
	private String _realName;
	private String _domain;
	public boolean enable;
	public long writable;
	public long appstacks;
	public long attachments;
	public long logins;
	public String last_login;
	public String last_login_human;

	public User() {
	}

	public User(long id, String upn, String name, boolean enable, long writable, long appstacks, long attachments, long logins, String last_login,
			String last_login_human) {
		super();
		this.upn = upn;
		this.name = name;
		this.enable = enable;
		this.writable = writable;
		this.appstacks = appstacks;
		this.attachments = attachments;
		this.logins = logins;
		this.last_login = last_login;
		this.last_login_human = last_login_human;
	}

	public long getId() {
		Pattern pattern = Pattern.compile("<a href=.*?title=");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			String group = matcher.group();
			if (group.contains("/"))
				token = group.substring(group.lastIndexOf('/') + 1, group.length() - 8);
		}
		this._id = Long.parseLong(token);
		return _id;
	}

	public String getRealName() {
		Pattern pattern = Pattern.compile(">.*?</a>");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			String group = matcher.group();
			if (group.contains("\\"))
				token = group.substring(group.indexOf('\\') + 1, group.length() - 4);
		}
		this._realName = token;
		return _realName;
	}

	public String getDomain() {
		Pattern pattern = Pattern.compile(">.*?</a>");
		String token = null;
		Matcher matcher = pattern.matcher(this.upn);
		while (matcher.find()) {
			String group = matcher.group();
			if (group.contains(">") && group.contains("\\"))
				token = group.substring(group.indexOf('>') + 1, group.indexOf('\\'));
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
