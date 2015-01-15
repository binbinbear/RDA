package com.vmware.eucenablement.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if (requiresAuthentication((HttpServletRequest) req)) {
			String requestType = ((HttpServletRequest) req).getHeader("X-Requested-With");
			if (requestType != null && !requestType.isEmpty() && requestType.equalsIgnoreCase("XMLHttpRequest")) {
				((HttpServletResponse) resp).setStatus(404);
				((HttpServletResponse) resp).setHeader("sessionstatus", "timeout");
				return;
			}
			((HttpServletResponse) resp).sendRedirect("login.html");
			return;
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	private boolean requiresAuthentication(HttpServletRequest req) {
		HttpSession session = req.getSession();
		if (null != session.getAttribute("user") || req.getServletPath().contains("login.html") || req.getServletPath().contains("css")
				|| req.getServletPath().contains("jquery") || req.getServletPath().contains("jtable") || req.getServletPath().contains("img")
				|| req.getServletPath().contains("js") || (req.getServletPath().contains("m2av") && req.getParameter("f").contains("login"))
				|| req.getServletPath().contains("M2avHelp.html") || req.getServletPath().contains("resources") || req.getServletPath().contains("installed"))
			return false;
		return true;
	}

}
