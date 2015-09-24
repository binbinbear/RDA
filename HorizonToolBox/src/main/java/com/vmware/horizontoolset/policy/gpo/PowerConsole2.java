/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.policy.gpo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author tiliu
 */

public class PowerConsole2 {
   
    public static Output execute(String...commands) {
        return execute(Arrays.asList(commands));
    }
    
    public static Output execute(List<String> commands) {
        
        String[] processParameters;
        if (commands.size() == 1 ) {
            processParameters = new String[]{"powershell.exe", "-NoProfile", "-NonInteractive", "-Command", commands.get(0)};
        } else {
            processParameters = new String[]{"powershell.exe", "-NoExit", "-Command", "-"};
        }
        
    	ProcessBuilder pb = new ProcessBuilder(processParameters);
        Process p;
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot execute PowerShell.exe", ex);
        }
        
        Gobbler outGobbler = Gobbler.attach(p.getInputStream());
        Gobbler errGobbler = Gobbler.attach(p.getErrorStream());
        
        if (commands.size() != 1) {
            try (PrintWriter out = new PrintWriter(p.getOutputStream(), true)) {
                for (String cmd : commands)
                    out.println(cmd);
                out.println("exit");
            }
        } else {
            try {
                p.getOutputStream().close();
            } catch (IOException ex) {
            }
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
    
    public static void main(String[] args) {
		//add by wx 9-15
	    String sysDriver = System.getProperty("user.home");
	    sysDriver = sysDriver.substring(0, sysDriver.indexOf(":"));
		Output o = execute("test-path " + sysDriver + ":\\Windows");
		
        //Output o = execute("test-path c:\\Windows");
        System.out.println(o);
    }
}