package com.vmware.horizontoolset.util;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;


public class SimpleHttpClient {

	private static Logger log = Logger.getLogger(SimpleHttpClient.class);

	private HttpClient client;
	public SimpleHttpClient(){
		//accept un-trusted ssl certificate
		X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		X509TrustManager tm = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0,
					String arg1) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0,
					String arg1) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}

        };

        SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(ctx);
			sslSocketFactory.setHostnameVerifier(hostnameVerifier);
			client = new DefaultHttpClient();
			Scheme https = new Scheme("https", sslSocketFactory, 443);
            client.getConnectionManager().getSchemeRegistry().register(https);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("Can't create new client since:", e);
		}



	}


	public String post(String url, String message){

		if (client == null){
			log.error("Can't post since no client!!!!!!!!!!!!!!!");
			return "";
		}
		String result= null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);

			HttpEntity requestEntity = new StringEntity(message, "UTF-8");
			httpPost.setEntity(requestEntity);


			HttpResponse httpResponse = client.execute(httpPost);

			HttpEntity responsetEntity = httpResponse.getEntity();
			InputStream inputStream = responsetEntity.getContent();

			StringBuilder reponseXml = new StringBuilder();
			byte[] b = new byte[2048];
			int length = 0;
			while ((length = inputStream.read(b)) != -1) {
				reponseXml.append(new String(b, 0, length));
			}
			result = reponseXml.toString();

		} catch (Exception e) {
			log.warn("can't get domains", e);

		}
		return result;
	}

}