/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.policy.gpo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author tiliu
 */

public class RunCommand {
   
    public static Output execute(String...commands) {
        return execute(Arrays.asList(commands), null);
    }
    
    public static Output execute(File workingDirectory, String...commands) {
        return execute(Arrays.asList(commands), workingDirectory);
    }
    
    public static Output execute(List<String> commands, File workingDirectory) {
        
    	if (commands.isEmpty())
    		throw new IllegalArgumentException("commands");
    	
    	ProcessBuilder pb = new ProcessBuilder(commands);
    	
    	if (workingDirectory != null)
    		pb.directory(workingDirectory);
    	
        Process p;
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot run command: " + commands.get(0));
        }
        
        Gobbler outGobbler = Gobbler.attach(p.getInputStream());
        Gobbler errGobbler = Gobbler.attach(p.getErrorStream());
        
        try {
            p.getOutputStream().close();
        } catch (IOException ex) {
        }
        
        try {
            p.waitFor();
        } catch (InterruptedException e) {
        }
        try {
            outGobbler.join();
            errGobbler.join();
        } catch (InterruptedException e) {
        }
        int exitValue = -1;
        try {
            exitValue = p.exitValue();
        } catch (Exception e) {
        }
        
        List<String> tmp = outGobbler.consumeOuput();
        String[] stdout = tmp.toArray(new String[tmp.size()]);
        tmp = errGobbler.consumeOuput();
        String[] stderr = tmp.toArray(new String[tmp.size()]);
        
        return new Output(stdout, stderr, exitValue);
    }
}