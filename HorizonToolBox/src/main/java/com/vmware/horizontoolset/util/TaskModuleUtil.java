package com.vmware.horizontoolset.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.vmware.horizon.auditing.report.Event;
import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;

public class TaskModuleUtil {

	public static String server;
	public static Credential credential;
	private static Map<String,Credential> mapCredential = new HashMap<String, Credential>();
	
	
	public synchronized static void onLogin(String server, Credential newCredential, boolean anyError, HttpSession session) {
		if (credential == null || !anyError) {
			TaskModuleUtil.server = server;
			credential = newCredential;
			Credential cred = new Credential(credential.getUsername(), credential.getPassword(), credential.getDomain());
			mapCredential.put(session.getId(), cred);
		}
	}
	
	public synchronized static Credential getLoginInfo(HttpSession session) {
		Credential cred =  mapCredential.get(session.getId());
		if(cred == null) {
			return credential;
		} else {
			return cred;
		}
	}

	private static <T> T wrapSkipMethod(final T o, Class<T> baseInterface, final String methodToSkip) {
		
		@SuppressWarnings("unchecked")
		T t = (T)Proxy.newProxyInstance(baseInterface.getClassLoader(),
		        new Class[]{baseInterface},
		        new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if (method.getName().equals(methodToSkip))
							return null;
						return method.invoke(o, args);
					}
		});
		
		return t;
	}
	
	/**
	 * Get transient View API object. 
	 * Never cache the instance and must close after use.
	 * 
	 * @param session
	 * @return
	 */
	public static ViewAPIService getViewAPIService(HttpSession session){
		
		ViewAPIService api = SessionUtil.getViewAPIService(session);
		if (api != null)
			return wrapSkipMethod(api, ViewAPIService.class, "close");
		
		if (credential == null)
			return null;
		
		return ViewApiFactory.createNewAPIService(server, credential.getUsername(), credential.getPassword(), credential.getDomain());
	}
	
}
