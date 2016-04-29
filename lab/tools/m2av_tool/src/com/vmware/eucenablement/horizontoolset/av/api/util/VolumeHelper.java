package com.vmware.eucenablement.horizontoolset.av.api.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

/**
 * A VolumeHelper class is a agent to help user communicate with web volume
 * manager throught http clinet.
 *
 * This helper stroed a session id identify the single connection and a
 * Cross-site request forgery token in case cross site attack.
 *
 * @author Gao Xiaoning
 * @version 1.0
 */
public class VolumeHelper implements Closeable {

	private static final Logger _LOG = Logger.getLogger(VolumeHelper.class);
        //private static final Logger _LOG = null;
	/*
	 * Cross-site request forgery token
	 */
	private String _str_csrf_TOKEN = null;
	/*
	 * session id stored in the server
	 */
	private String _str_session_ = null;

	/*
	 * The context of http client
	 */
	private HttpClientContext _CONTEXT = null;
	private String _str_host = null;
	private String _str_domain = null;

	public CloseableHttpClient closeable_HttpClient = null;

	/**
	 * create a closeable http clinet use defualt method
	 *
	 * @return a closeable http clinet
	 */
	public static CloseableHttpClient createDefault() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient;
	}

	/**
	 * create a closeable security http cline by specified file
	 *
	 * @return a closeable security http clinet
	 * @throws Exception
	 *             can find the key stroed file,
	 *             KeyStoreException,NoSuchAlgorithmException
	 *             ,CertificateException,IOException,KeyManagementException
	 */
	public static CloseableHttpClient createSSLClientDefault() throws Exception {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		try (FileInputStream instream = new FileInputStream(new File("/KEYSTORE.key"));) {
			trustStore.load(instream, "nopassword".toCharArray());
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpclient;
	}

	/**
	 * process host string, parse the host to domain, and anylise the protocol
	 *
	 * @param host
	 *            the host server connected
	 * @throws Exception
	 */
	public VolumeHelper(String host) throws Exception {
		host = host.trim();
		if (host.endsWith("/") && host.startsWith("https://")) {
			this.closeable_HttpClient = createSSLClientDefault();
			this._str_host = host;
			this._str_domain = host.substring("https://".length(), host.length() - 1);
		} else if (host.endsWith("/") && host.startsWith("http://")) {
			this.closeable_HttpClient = createDefault();
			this._str_host = host;
			this._str_domain = host.substring("http://".length(), host.length() - 1);
		} else if (!host.endsWith("/") && host.startsWith("https://")) {
			this.closeable_HttpClient = createSSLClientDefault();
			this._str_host = host + "/";
			this._str_domain = host.substring("https://".length(), host.length());
		} else if (!host.endsWith("/") && host.startsWith("http://")) {
			this.closeable_HttpClient = createDefault();
			this._str_host = host + "/";
			this._str_domain = host.substring("http://".length(), host.length());
		} else {
			this.closeable_HttpClient = createDefault();
			this._str_host = "http://" + host + "/";
			this._str_domain = host;
		}
	}

	/**
	 * process the request both get and post
	 *
	 * @param client
	 *            http client
	 * @param parameterMap
	 *            when use post request this parameter refer to the transform
	 *            parameter <name,value>
	 * @param url_request
	 *            the path of request
	 * @param method
	 *            currently refer to post or get
	 * @return this object
	 *
	 * @throws Exception
	 */
	public String requestProcess(Map<String, String> params, String req, HTTPMethod method) throws Exception {
		String request = this._str_host + req;
		if (method == HTTPMethod.POST) {
			return postProcess(closeable_HttpClient, params, request);
		} else {
			return getProcess(closeable_HttpClient, request);
		}
	}

	/**
	 * process the request both get and post
	 *
	 * @param client
	 *            http client
	 * @param parameterMap
	 *            when use post request this parameter refer to the transform
	 *            parameter <name,value>
	 * @param url_request
	 *            the path of request
	 * @return this object
	 *
	 * @throws Exception
	 */
	private String postProcess(CloseableHttpClient client, Map<String, String> params, String req) throws ClientProtocolException, IOException {
		String html = null;
		HttpPost http_post = new HttpPost(req);
		http_post.setHeader("X-CSRF-TOKEN", _str_csrf_TOKEN);
		http_post.setHeader("Cookie", "_session_id=" + _str_session_);
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), Consts.UTF_8);
		http_post.setEntity(postEntity);

		HttpResponse httpResponse = client.execute(http_post, _CONTEXT);

		if (isLoginURL(req)) {
			setContext(setCookieStore(httpResponse));
			String url_redirect = redirect(httpResponse);
			HttpGet http_get = new HttpGet(url_redirect);

			HttpResponse indexResponse = client.execute(http_get, _CONTEXT);
			html = html_extract(indexResponse);

			HttpResponse reload = client.execute(http_get, _CONTEXT);
			html = html_extract(reload);

			_str_csrf_TOKEN = CSRF_TOKEN(html);
			_LOG.info(_str_csrf_TOKEN);
		} else {
			html = html_extract(httpResponse);
		}

		return html;
	}

	/**
	 * process the request both get and post
	 *
	 * @param client
	 *            http client
	 * @param url_request
	 *            the path of request
	 * @return this object
	 *
	 * @throws Exception
	 */
	private String getProcess(CloseableHttpClient client, String req) throws Exception {
		String html = null;
		HttpGet http_get = new HttpGet(req);
		try {
			HttpResponse httpResponse = client.execute(http_get, _CONTEXT);
			html = html_extract(httpResponse);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

	private String html_extract(HttpResponse httpResponse) throws ParseException, IOException {
		HttpEntity entity = httpResponse.getEntity();
		return EntityUtils.toString(entity);
	}

	/**
	 * set the session context
	 *
	 * @param cookieStore
	 *            cookie stored in
	 * @return this client context
	 */
	private HttpClientContext setContext(BasicCookieStore cookieStore) {
		_CONTEXT = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create().register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();
		_CONTEXT.setCookieSpecRegistry(registry);
		_CONTEXT.setCookieStore(cookieStore);
		return _CONTEXT;
	}

	/**
	 * create cookies to store login status
	 *
	 * @param cookieStore
	 *            cookie stored in
	 * @return BasicCookieStore
	 */
	private BasicCookieStore setCookieStore(HttpResponse httpResponse) {
		BasicCookieStore cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		if (("" != setCookie || setCookie != null) && setCookie.contains(";")) {
			String _session_id = setCookie.substring("_session_id=".length(), setCookie.indexOf(";"));
			_str_session_ = _session_id;
			// create a new Cookie
			BasicClientCookie cookie = new BasicClientCookie("_session_id", _session_id);
			cookie.setVersion(0);
			cookie.setDomain(_str_domain);
			cookie.setPath("/");
			cookieStore.addCookie(cookie);
		}
		return cookieStore;
	}

	/**
	 * parse the parameters from the map key value form to name value pair
	 *
	 * @param parameterMap
	 * @return the standard <name,value> parameter List
	 */
	private List<NameValuePair> getParam(Map<String, String> params) {
		if (params == null) {
			return null;
		}
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> parmEntry = (Entry<String, String>) iter.next();
			param.add(new BasicNameValuePair(parmEntry.getKey(), parmEntry.getValue()));
		}
		return param;
	}

	/**
	 * check if the url is login page
	 *
	 * @param url_request
	 *            checked url
	 * @return true if the url is login url
	 */
	private boolean isLoginURL(String req) {
		return req.trim().toUpperCase().endsWith("LOGIN");

	}

	private String redirect(HttpResponse httpResponse) {
		return httpResponse.getFirstHeader("Location").getValue();

	}

	/**
	 * extract the Cross-site request forgery token from the html page
	 *
	 * @param str_htmlContent
	 *            the html page
	 * @return Cross-site request forgery token
	 */
	private String CSRF_TOKEN(String content) {
		String token = null;
		Pattern pattern = Pattern.compile("<meta.*? />");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find())
			if (matcher.group().contains("csrf-token")) {
				String group = matcher.group();
				if (group.contains("=") && group.contains("name"))
					token = group.substring(group.indexOf("=") + 2, group.lastIndexOf("name") - 2);
			}
		return token;
	}

	public String getCsrf_TOKEN() {
		return _str_csrf_TOKEN;
	}

	public String getSession_ID() {
		return _str_session_;
	}

	@Override
	public void close() throws IOException {
		closeable_HttpClient.close();
	}
}
