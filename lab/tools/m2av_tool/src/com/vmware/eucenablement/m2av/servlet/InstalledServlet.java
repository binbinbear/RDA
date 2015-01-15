package com.vmware.eucenablement.m2av.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.vmware.eucenablement.service.PoolStack;

public class InstalledServlet extends HttpServlet {

	private static final long serialVersionUID = -3031246284725436614L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String poolId = req.getParameter("poolId");
		resp.setHeader("Content-type", "text/json;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);
		Gson gson = new Gson();
		try {
			String result = gson.toJson(PoolStack.get(poolId));
			System.out.println(result);
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
