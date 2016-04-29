package com.vmware.eucenablement.horizontoolset.av.api.pojo;

import java.util.List;

/*
 * gson parsed object
 */
public class Message {

	public List<String> successes;
	public String warning;

	public Message() {
	}

	public Message(List<String> successes, String warning) {
		super();
		this.successes = successes;
		this.warning = warning;
	}

}
