package com.vmware.horizontoolset.wsproxy;


import java.net.URI;

public interface RequestRewriter {

	public URI rewriteRequest(URI original) throws Exception;
}
