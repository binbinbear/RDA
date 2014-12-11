/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.vmware.horizontoolset.policy.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tiliu
 */
public class Output {
	
    public final String[] stdout;
    public final String[] stderr;
    public final int exitValue;
    
    Output(String[] stdout, String[] stderr, int exitValue) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitValue = exitValue;
    }
    
    public Map<String, String> toMap() {
    	Map<String, String> map = new HashMap<>();
    	
		for (String line : stdout) {
			int i = line.indexOf(':');
			if (i < 0)
				continue;
			
			String k = line.substring(0, i).trim();
			String v = line.substring(i + 1).trim();
			
			map.put(k, v);
		}
		return map;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for (String s : stdout)
    		sb.append("[OUT] ").append(s).append("\r\n");
    	for (String s : stderr)
    		sb.append("[ERR] ").append(s).append("\r\n");
        sb.append("Exit code: ").append(exitValue);
    	return sb.toString();
    }

    public void writeToFile(String fileName, boolean append) {
        try (FileWriter w = new FileWriter(fileName, append)) {
            w.write(toString());
        } catch (IOException ex) {
        }
    }
    
    public boolean outputContains(String flag) {
        for (String s : stdout) {
            if (s.contains(flag))
                return true;
        }
        return false;
    }
    
    public boolean outputContainsLine(String flag) {
        for (String s : stdout) {
            s = s.trim();
            if (s.equals(flag))
                return true;
        }
        return false;
    }
}