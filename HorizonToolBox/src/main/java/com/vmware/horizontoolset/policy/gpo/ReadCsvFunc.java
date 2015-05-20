package com.vmware.horizontoolset.policy.gpo;

import java.util.List;
import java.util.Map;

public interface ReadCsvFunc {
	public List<Map<String, String>> operate(String fileName);
}
