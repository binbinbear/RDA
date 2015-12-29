package com.vmware.vdi.broker.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.vmware.vdi.broker.toolboxfilter.MultipleReadRequestWrapper;
import com.vmware.vdi.broker.toolboxfilter.XmlRequestProcessor;


public class ToolboxFilter implements Filter {

    private static final Logger log = Logger.getLogger(ToolboxFilter.class);



    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

    	log.info("Filter action by toolbox!");

        if (request instanceof HttpServletRequest) {

            HttpServletRequest req = (HttpServletRequest) request;
            if ("POST".equals(req.getMethod())) {
            	log.info("Filter post by toolbox!");
            	MultipleReadRequestWrapper reqwrapper = new MultipleReadRequestWrapper(req);
            	 XmlRequestProcessor requestProcessor = new XmlRequestProcessor(
            			 reqwrapper);

                 if (!requestProcessor.isAllowed()){
                	 log.info("Filter forbides by toolbox!");
                 	 ((HttpServletResponse) response).setStatus(403);
                 	 return;
                 }
                 log.info("Filter allow by toolbox!");
                 chain.doFilter(reqwrapper, response);
                 return;
        	}
        }


        chain.doFilter(request, response);
    }


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {


	}



	@Override
	public void destroy() {


	}


}
