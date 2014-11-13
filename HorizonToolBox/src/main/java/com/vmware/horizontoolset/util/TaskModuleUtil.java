package com.vmware.horizontoolset.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpSession;

import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;

public class TaskModuleUtil {

	public static String server;
	public static Credential credential;
	
	public synchronized static void onLogin(String server, Credential newCredential, boolean anyError) {
		if (credential == null || !anyError) {
			TaskModuleUtil.server = server;
			credential = newCredential;
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
