package com.vmware.horizontoolset.cli;

import java.io.Writer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class ThymeleafEngine {
	private FileTemplateResolver templateResolver;
	private TemplateEngine templateEngine;
	
	public ThymeleafEngine(){
		 templateResolver = new FileTemplateResolver();
		 // XHTML is the default mode, but we will set it anyway for better understanding of code
	     templateResolver.setTemplateMode("XHTML");

	     templateResolver.setCacheTTLMs(3600000L);
	     
	     templateEngine = new TemplateEngine();
	     templateEngine.setTemplateResolver(templateResolver);
	}
	
	public void writeHTML(String templatePath, ThymeleafContext context, final Writer writer){
        
        templateEngine.process(templatePath, context,  writer);
	}
}
