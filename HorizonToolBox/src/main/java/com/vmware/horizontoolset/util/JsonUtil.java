/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author tiliu
 */
public class JsonUtil {
    
    public static <T> T load(String fileName, Class<T> klass) throws IOException {
        File file = new File(fileName);

        Gson gson = new Gson();
        try (FileReader rdr = new FileReader(file)) {
            return gson.fromJson(rdr, klass);
        }
    }

    public static void save(String fileName, Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter out = new FileWriter(fileName);) {
            out.write(gson.toJson(o));
        } catch (Exception e) {
        }
    }
    
    public static String javaToJson(Object obj){
    	Gson gson = new Gson();
    	String jsonString = gson.toJson(obj);
    	return jsonString;
    }
}
