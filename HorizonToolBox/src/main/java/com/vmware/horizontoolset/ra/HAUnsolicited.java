package com.vmware.horizontoolset.ra;

import javax.servlet.http.HttpSession;

import java.io.BufferedReader;    
import java.io.ByteArrayOutputStream;
import java.io.IOException;    
import java.io.InputStream;    
import java.io.InputStreamReader; 
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.log4j.Logger;
import com.vmware.horizontoolset.util.SimpleMask;


public class HAUnsolicited {
	private static Logger log = Logger.getLogger(HAUnsolicited.class);
	
	private String adapterPath = null;
	private String userName = null;
	private String password = null;
	private String domain = null;
	private String remoteIP = null;
	
	public String retDescription = null;
	public String ticketContent = null;
	
	public HAUnsolicited(String adapterPath, String userName, String password, String domain, String remoteIP) {
		this.adapterPath = adapterPath;
		this.userName = userName;
		this.password = password;
		this.domain = domain;
		this.remoteIP = remoteIP;
	}
	
	private void Rest() {
		this.adapterPath = null;
		userName = null;
		password = null;
		domain = null;
		remoteIP = null;
	};
	
	boolean IsValidTicketInput() {
		if ((adapterPath == null) ||
				(userName == null) ||
				(password == null) ||
				(domain == null) ||
				(remoteIP == null)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean CreateRATicket() {
		if(IsValidTicketInput() == false) {
			log.error("Wrong parameters for RA Ticket");
			retDescription = "Wrong parameters for RA Ticket.";
			return false;
		}
	   try {  
		   		// Prepare parameters
			   log.info("Parameters for RA Ticket: {" + this.adapterPath + ", " + this.userName + ", " +
						this.domain + ", " + this.remoteIP + "}");
			    List<String> list = new ArrayList<String>();  
			    list.add(adapterPath);
			    list.add(userName);
			    list.add(SimpleMask.mask(this.password));
			    list.add(this.domain);
			    String command = "HRAUnsolicited.exe -HOST " + this.remoteIP;
			    list.add(command);
			    
			    
			    // Run process
			    log.info("Start to run child process!");
			    ProcessBuilder pb = null;  
			    Process p = null;  
			    pb = new ProcessBuilder(list);   
			    p = pb.start(); 
			    
			    /// start
			    /*BufferedReader reader=null;  
		        try {  
		            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));  
		            String line=null;  
		            while((line=reader.readLine())!=null){  
		                log.info(line);  
		            }  
		            int result=p.waitFor();  
		            log.info(result);  
		        } catch (IOException e) {  
		            // TODO Auto-generated catch block  
		            e.printStackTrace();  
		        } catch (InterruptedException e) {  
		            // TODO Auto-generated catch block  
		            e.printStackTrace();  
		        }  
		        return false;*/
		        /// end
			 
			    CollectOutput g = new CollectOutput();
			    g.attach(p.getInputStream());
			    int ret = p.waitFor();
			    String output = g.getOutput();
			    
			    String ticketContent = AnalyzeRetMsg(output);
			    if(ticketContent == null) {
			    	log.info("Can not get the ticket content");
			    	retDescription = "Can not get the ticket content";
			    	return false;
			    } else {
			    	//log.info("Get the ticket: " + ticketContent);
			    	this.ticketContent = ticketContent;
			    	this.retDescription = "Get the ra ticket successfully!";
			    }
		   } catch (Exception e) {  
			   log.error("Caught exception: " + e.toString());
			   retDescription = "Caught exception: " + e.toString();
			   e.printStackTrace();  
			   return false;
		   } 
	   
		   return true;
	}
	
	private String AnalyzeRetMsg(String msg) {
	    String sHead = "MSGHEAD--";
        String sTail = "--MSGTAIL";
        String ticketContent = null;
		int posHeadIndex = -1;
		int posTailIndex = -1;
		
		posHeadIndex = msg.indexOf(sHead);
		if(posHeadIndex != -1) {
			posTailIndex = msg.indexOf(sTail, posHeadIndex);
		}
		
		if((posHeadIndex != -1) && (posTailIndex != -1))
		{
			ticketContent = msg.substring(posHeadIndex + sHead.length(), posTailIndex);
		}
		else {
			ticketContent = null;
		}
		
		return ticketContent;
	}
	
    private class CollectOutput implements Runnable {
    	
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
}
