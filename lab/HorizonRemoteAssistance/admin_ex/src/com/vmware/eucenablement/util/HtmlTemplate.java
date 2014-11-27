package com.vmware.eucenablement.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HtmlTemplate {

	private final String template;
	private String content;
	
	private Map<String, String> vars = new LinkedHashMap<>();
	
	public HtmlTemplate(String template) {
		this.template = template;
	}

	public static HtmlTemplate load(Class<?> klass, String name) {
		
		String packageStr = klass.getPackage().getName();
		packageStr = packageStr.replace('.', '/');
		//if (!name.startsWith(packageStr))
			name = packageStr + '/' + name;
		
		StringBuilder sb = new StringBuilder();
		
		try (InputStream in = klass.getClassLoader().getResourceAsStream(name);
				InputStreamReader isr = new InputStreamReader(in);
				) {
			char[] buf = new char[16384];
			while (true) {
				int n = isr.read(buf);
				if (n == -1)
					break;
				sb.append(buf, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new HtmlTemplate(sb.toString());
	}

	public void replace(String varName, String replacement) {
		String old = vars.put(varName, replacement);
		if (!replacement.equals(old))
			content = null;
	}

	public String toString() {
		if (content == null) {
			content = template;
			for (Entry<String, String> en : vars.entrySet()) {
				
				String k = "${" + en.getKey() + '}';
				
				String old = content;
				content = content.replace(k, en.getValue());
				
				if (old.equals(content))
					throw new IllegalArgumentException("Var is set, but not found in template. Var=" + en.getKey());
			}
		}
		return content;
	}
}
