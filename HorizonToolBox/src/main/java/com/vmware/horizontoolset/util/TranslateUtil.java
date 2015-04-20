package com.vmware.horizontoolset.util;

import java.util.HashMap;
import java.util.Locale;

public class TranslateUtil {
	/*
	 * todo
	 * HashMap is not security ,if use supportLocal to add data,you should use ConcurentHashMap
	 */
	private static HashMap<String, String> supportLocale = new HashMap<String, String>();
	private static final String defaultLanguage = "en";
	public TranslateUtil() {
		createSupportLocale();
	}

	public static void createSupportLocale() {
		supportLocale.put("en", "en");
		supportLocale.put("en_us", "en");
		supportLocale.put("zh", "zh");
		supportLocale.put("zh_cn", "zh");
	}

	public static void addSupportLocal(Locale locale,String language) {
		if (locale==null) {
			return;
		}
		String localUniform = locale.toString().toLowerCase();
		if(language==null||language=="");{
			language=defaultLanguage;
		}
		supportLocale.put(localUniform, language);
		
	}
	public String getLocaleLanguage(Locale locale){
		if (locale==null) {
			return defaultLanguage;
		}
		String localeUniform = locale.toString().toLowerCase();
		return supportLocale.containsKey(localeUniform)? supportLocale.get(localeUniform):defaultLanguage;
	}
}
