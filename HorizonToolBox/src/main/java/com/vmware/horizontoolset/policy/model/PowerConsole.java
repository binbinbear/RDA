/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.policy.model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 * @author tiliu
 */



public class PowerConsole {

    private final ProcessBuilder pb;
    Process p;
    boolean closed = false;
    PrintWriter writer;
    Gobbler outGobbler;
    Gobbler errGobbler;
    
    PowerConsole(String[] commandList) {
        pb = new ProcessBuilder(commandList);
        try {
            p = pb.start();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot execute PowerShell.exe", ex);
        }
        writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(p.getOutputStream())), true);
        outGobbler = Gobbler.attach(p.getInputStream());
        errGobbler = Gobbler.attach(p.getErrorStream());
    }

    public void execute(String command) {
        if (!closed) {
            errGobbler.consumeOuput();
            writer.println(command);
            writer.flush();
        } else {
            throw new IllegalStateException("Power console has ben closed.");
        }
    }

    public void exit() {
        execute("exit");
        close();
    }
    
    public void close() {
        writer.close();
        try {
            //outGobbler.join();
            //errGobbler.join();
            p.waitFor();
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        /*   PowerConsole pc = new PowerConsole(new String[]{"/bin/bash"});
        
        PowerConsole pc = new PowerConsole(new String[]{"/bin/bash"});
        pc.execute("pwd");
        pc.execute("ls");
        pc.execute("cd /");
        pc.execute("ls -l");
        pc.execute("cd ~");
        pc.execute("find . -name 'test.*' -print");
        pc.close();
         */
        //      PowerConsole pc = new PowerConsole(new String[]{"cmd.exe"});
        PowerConsole pc = new PowerConsole(new String[]{"powershell.exe", "-NoExit", "-Command", "-"});
        System.out.println("========== Executing dir");
        pc.execute("dir"); 
        System.out.println("========== Executing cd\\");
        pc.execute("cd \\"); Thread.sleep(2000);
        System.out.println("========== Executing dir");
        pc.execute("dir"); Thread.sleep(2000);
        System.out.println("========== Executing cd \\temp");
        pc.execute("cd \\temp"); Thread.sleep(2000);
        System.out.println("========== Executing dir");
        pc.execute("dir"); Thread.sleep(2000);
        System.out.println("========== Executing cd \\bubba");
        pc.execute("cd \\bubba"); Thread.sleep(2000);
        System.out.println("========== Exiting .... bye.");
        pc.close();
    }
}