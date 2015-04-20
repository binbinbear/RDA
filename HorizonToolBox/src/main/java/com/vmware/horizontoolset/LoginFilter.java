package com.vmware.horizontoolset;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.vmware.horizontoolset.util.SessionUtil;

@Component  
public class LoginFilter extends GenericFilterBean  {  

	private static Logger log = Logger.getLogger(LoginFilter.class);

	private static final String horizontoolset = "/toolbox";
	//Not null
	private String[] allows =new String[1];
	
	public LoginFilter(){
		log.debug("Login Filter is created");
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		log.debug("LoginFilter do Filter:");
		SessionUtil.setLocale(((HttpServletRequest)request).getSession(), request.getLocale());
        if (requiresAuthentication((HttpServletRequest) request)) {
        	String requestType =( (HttpServletRequest) request).getHeader("X-Requested-With");
            if (requestType!=null && !requestType.isEmpty() && requestType.equalsIgnoreCase("XMLHttpRequest")) {
            	((HttpServletResponse) response).setStatus(404);
            	((HttpServletResponse) response).setHeader("sessionstatus", "timeout");
                return;
            } 
            
        	log.debug("Redirect to login");
        	String loginURL = LoginController.LoginURL;
        	
        	((HttpServletResponse) response).sendRedirect(horizontoolset+loginURL);
        	return;
        }
        
        
        chain.doFilter(request, response);
	}

	private boolean requiresAuthentication(HttpServletRequest request) {
		for (int i=0;i<allows.length;i++){
			if (request.getServletPath().contains(allows[i])){
				//log.debug("No need to filter since it's allowed");
				return false;
			}
		}
		
		Object service = SessionUtil.getViewAPIService(request.getSession());
		
		if (service == null){
			//log.debug("Not a login user, require auth");
			return true;
		}
		//log.debug("Don't need auth");
		return false;
	}

	public String[] getAllows() {
		return allows;
	}

	public void setAllows(String[] allows) {
		this.allows = allows;

	}


}  