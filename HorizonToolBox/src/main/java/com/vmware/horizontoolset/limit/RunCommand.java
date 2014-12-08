package com.vmware.horizontoolset.limit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RunCommand {

    public final String cmd;
    public Throwable error;
    public String output;
    
	public RunCommand(String cmd) {
		this.cmd = cmd;
	}

	public int run() {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		
		CollectOutput g = new CollectOutput();
		
		try {
			Process p = pb.start();
		
			g.attach(p.getInputStream());
			
			int ret = p.waitFor();
			
			output = g.getOutput();
			
			return ret;
		} catch (Throwable t) {
			error = t;
			return -1;
		}
	}

	public String getOutput() {
		return output;
	}

    private static class CollectOutput implements Runnable {
	
	    private InputStream in;
	    private Thread thread;
	    private ByteArrayOutputStream out = new ByteArrayOutputStream();
	    
	    private void attach(InputStream in) {
	    	this.in = in;
			thread = new Thread(this, "CollectOutput");
			thread.start();
		}

	    public String getOutput() {
	    	try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	byte[] bytes = out.toByteArray();
	    	return new String(bytes); 
	    }
	    
	    @Override
	    public void run() {
	        try {
	            while (true) {
	            	int i = in.read();
	            	if (i == -1)
	            		break;
	            	
	            	out.write(i);
	            }
	        } catch (IOException ex) {
	            //ex.printStackTrace();
	        }
	    }
	}
    
    public static void main(String[] args) {
    	try {
    		RunCommand rc = new RunCommand("c:/RuntimeDumper.exe");
    		int ret = rc.run();
    		String output = rc.getOutput();
    		System.out.println("RET=" + ret + ", output=" + output + ", error=" + rc.error);
    		
    	} catch (Throwable t) {
    		t.printStackTrace();
    	}
    }
}
