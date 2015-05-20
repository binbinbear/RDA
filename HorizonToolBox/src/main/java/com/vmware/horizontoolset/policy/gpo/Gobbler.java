/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2014 VMware Inc.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.vmware.horizontoolset.policy.gpo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tiliu
 */
public class Gobbler implements Runnable {

    private final InputStream in;
    private final List<String> output;
    private final Thread thread;
    
    private Gobbler(InputStream in) {
        this.in = in;
        output = new ArrayList<>();
        thread = new Thread(this, "Gobbler");
    }
    
    public static Gobbler attach(InputStream in) {
    	Gobbler g = new Gobbler(in);
    	g.thread.start();
    	return g;
    }
    
    public void join() throws InterruptedException {
        thread.join();
    }
    
    List<String> consumeOuput() {
        synchronized (output) {
            List<String> ret = new ArrayList<>(output);
            output.clear();
            return ret;
        }
    }
    
    @Override
    public void run() {
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(in))) {
            while (true) {
                String s = rdr.readLine();
                if (s == null)
                    break;
                
                synchronized (output) {
                    output.add(s);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Gobbler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
