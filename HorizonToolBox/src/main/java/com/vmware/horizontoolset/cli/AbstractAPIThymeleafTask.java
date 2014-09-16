package com.vmware.horizontoolset.cli;


import java.io.FileWriter;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.Credential;
import com.vmware.horizontoolset.viewapi.ViewAPIService;
import com.vmware.horizontoolset.viewapi.ViewApiFactory;

public abstract class AbstractAPIThymeleafTask implements Task{
	private static Logger log = Logger.getLogger(AbstractAPIThymeleafTask.class);
	protected ViewAPIService service;
	protected ThymeleafEngine engine;
	
	protected ThymeleafContext context;
	
	
	public AbstractAPIThymeleafTask(String server, Credential credential){
		service = ViewApiFactory.createNewAPIService(server, credential.getUsername(), credential.getPassword(), credential.getDomain());
		engine = new ThymeleafEngine();
		context = new ThymeleafContext();
	}
	
	public void setVariable(String name, Object value){
		this.context.setVariable(name, value);
	}
	
	
	public void generateReport(String templatePath, String reportPath){
		System.out.println("Start to generate Report");
		FileWriter writer = null;
		try {
			writer = new FileWriter(reportPath);
			this.engine.writeHTML(templatePath, context, writer);
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warn("Can't create report file:"+reportPath, e);
		}
		
	}
}
