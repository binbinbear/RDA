package com.vmware.horizontoolset.cli;

import com.vmware.horizontoolset.Credential;


public class AuditingCli {
	private static void printUsage(){
		System.out.println("AudtingCli [view server IP] [user name] [password] [domain] [template file path] [report file path]");
	}
	


	public static void main(String args[]){
		
		if (args==null || args.length !=6){
			printUsage();
			return;
		}
		String server = args[0];
		String username = args[1];
		String password = args[2];
		String domain = args[3];
		String templatepath = args[4];
		String reportPath = args[5];
		
	    System.out.println("Start to generate report:"+reportPath);
		Credential credential = new Credential(username, password, domain);
		
		SnapShotReportTask task = new SnapShotReportTask(server, credential,templatepath, reportPath);
		task.Execute();
	
	}
		
}
