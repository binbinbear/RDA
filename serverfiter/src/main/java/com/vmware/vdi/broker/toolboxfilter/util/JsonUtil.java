/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.vdi.broker.toolboxfilter.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonUtil {
    private static Logger logger = Logger.getLogger(JsonUtil.class);
    public static <T> T load(String fileName, Class<T> klass) throws IOException {
        File file = new File(fileName);

        Gson gson = new Gson();
        try (FileReader rdr = new FileReader(file)) {
            return gson.fromJson(rdr, klass);
        }
    }

    public static void save(String fileName, Object o) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter out = new FileWriter(fileName);) {
            out.write(gson.toJson(o));
        } 
    }
    
    public static String javaToJson(Object obj){
    	Gson gson = new Gson();
    	String jsonString = gson.toJson(obj);
    	return jsonString;
    }
    
    public static <T> T jsonToJava(String json, Class<T> clazz) {
    	if (json == null)
    		return null;
    	try{
    		Gson gson = new Gson();
            T result = (T) gson.fromJson(json, clazz);
            return result;
    	}catch(Exception ex){
    		logger.error("Can't change json to java, json:"+ json + " class:"+clazz.getCanonicalName(), ex);
    	}
    	
    	return null;
    	
    	
    }
}
