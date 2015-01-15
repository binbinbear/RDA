package com.vmware.eucenablement.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.eucenablement.horizontoolset.av.api.VolumeAPI;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.AppStack;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Computer;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Message;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Type;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.User;
import com.vmware.eucenablement.m2av.servlet.M2avServlet;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;

public class M2avService {
	private static final Logger _LOG = Logger.getLogger(M2avServlet.class);

	// login to app volume server
	/*
	 * CODE REVIEW: Use POST method, thus password don't need to exposed in the
	 * URL
	 */
	public static void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String volumeServer = request.getParameter("volume[host]");
		String volumeName = request.getParameter("volume[name]");
		String volumePassword = request.getParameter("volume[password]");
		String loginResult = "loginFailed";
		HttpSession session = request.getSession();
		VolumeService.login(session, volumeServer, null, volumeName, volumePassword);
		if (VolumeService.instance(session) != null) {
			session.setAttribute("user", volumeName);
			loginResult = "loginSuccess";
			response.sendRedirect("./indexTab.html?msg=" + loginResult);
			return;
		}
		response.sendRedirect("./login.html?msg=" + loginResult);
		return;

	}

	// view login handle
	public static void viewLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String viewServer = request.getParameter("view[IP]");
		String viewName = request.getParameter("view[name]");
		String viewPassword = request.getParameter("view[password]");
		String domain = request.getParameter("view[domain]");
		String loginResult = "loginFailed";
		HttpSession session = request.getSession();
		_LOG.info(viewName + " login to " + viewServer);
		ViewService.login(session, viewServer, viewName, viewPassword, domain);
		if (ViewService.instance(session) != null) {
			loginResult = "loginSuccess";
		}
		response.sendRedirect("./empower.html?msg=" + loginResult);
		return;
	}

	// vcenter login handle
	public static void vcenterLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// code to handle vCenter information
		String vcenterServer = request.getParameter("vcenter[IP]");
		String vcenterName = request.getParameter("vcenter[name]");
		String vcenterPassword = request.getParameter("vcenter[password]");
		String refer = request.getParameter("refer").trim();
		String loginResult = "loginFailed";
		HttpSession session = request.getSession();
		VCenterService.login(session, vcenterServer, vcenterName, vcenterPassword);
		if (VCenterService.instance(session) != null) {
			loginResult = "loginSuccess";
		}
		if ("import".equals(refer)) {
			response.sendRedirect("./appimport.html?msg=" + loginResult);
			return;
		}
		response.sendRedirect("./appcopy.html?msg=" + loginResult);
		return;
	}

	// get view service agent
	public static ViewService viewAPI(HttpSession session) {
		return ViewService.instance(session);
	}

	// get app stacks
	public static List<AppStack> handleListAppStacks(HttpSession session) {
		List<AppStack> appStacks = null;
		if (VolumeService.instance(session) == null) {
			return null;
		}
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		appStacks = volumeAPI.listAppStacks();
		return appStacks;
	}

	// refresh app stacks
	public static void refreshAppStacks(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		if (VolumeService.instance(session) == null || ViewService.instance(session) == null) {
			response.sendRedirect("./login.html");
			return;
		}
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		volumeAPI.refreshAppStacks();
		response.sendRedirect("./empower.html");
		return;
	}

	// assign(attach) app stack to users
	public static Message assignmentUser(String appStack, List<String> dnsNames, String real, HttpSession session) {
		boolean isReal = false;
		if (null == real | "".equals(real) | "1".equals(real))
			isReal = true;
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		return volumeAPI.assignStack2User(appStack, dnsNames, isReal);
	}

	// unassign(detach) app stack from user
	public static Message unassignmentUser(String appStack, List<String> dnsNames, String real, HttpSession session) {
		boolean isReal = false;
		if (null == real | "".equals(real) | "1".equals(real))
			isReal = true;
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		return volumeAPI.unassignStack2User(appStack, dnsNames, isReal);
	}

	// assign(attach) app stack to computers
	public static Message assignmentComputer(String appStack, List<String> dnsNames, String real, HttpSession session) {
		boolean isReal = false;
		if (null == real | "".equals(real) | "1".equals(real))
			isReal = true;
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		return volumeAPI.assignStack2Computer(appStack, dnsNames, isReal);
	}

	// unassign(detach) app stack from computer
	public static Message unassignmentComputer(String appStack, List<String> dnsNames, String real, HttpSession session) {
		boolean isReal = false;
		if (null == real | "".equals(real) | "1".equals(real))
			isReal = true;
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;
		return volumeAPI.unassignStack2Computer(appStack, dnsNames, isReal);
	}

	// get computer installed app volume agent
	public static Computer getComputer(String dnsName, HttpSession session) {
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;

		return volumeAPI.getComputer(dnsName);
	}

	// get users
	public static User getUser(String dnsName, HttpSession session) {
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;

		return volumeAPI.getUser(dnsName);
	}

	// get exist app stack
	public static List<AppStack> getAppStacks(String dnsNames, Type type, HttpSession session) {
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;

		return volumeAPI.assignmentsAppStacks(dnsNames, type);
	}

	public static AppStack getAppStack(String fileLocation, HttpSession session) {
		VolumeAPI volumeAPI = VolumeService.instance(session).volume;

		return volumeAPI.getAppStack(fileLocation);

	}

	/**
	 * Get Datastore list from the vCenter where AppVolumes is installed
	 * 
	 * @param session
	 * @return List of Datastore
	 */
	public static List<Datastore> getvCenterDatastores(HttpSession session) {
		try {
			VCenterService api = VCenterService.instance(session);
			Folder rootFolder = api.si.getRootFolder();
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datastore");
			if (mes == null || mes.length == 0) {
				return null;
			}
			Datastore[] datastoreArray = Arrays.copyOf(mes, mes.length, Datastore[].class);
			ArrayList<Datastore> dataStories = new ArrayList<Datastore>(Arrays.asList(datastoreArray));
			return dataStories;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Copy AppStack vmdk files from source location to target location
	 * 
	 * @param session
	 * @param srcAppStack
	 * @param dstDatastore
	 * @param dstFilePath
	 * @param dstFileName
	 * @return
	 */
	public static boolean copyAppStack(HttpSession session, AppStack srcAppStack, String dstDatastore, String dstFilePath, String dstFileName) {
		boolean result = false;
		try {
			String srcVDK = "";
			if (null != srcAppStack) {
				srcVDK = srcAppStack.file_location;
			} else {
				return result;
			}
			dstFileName = dstFileName + ".vmdk";
			result = VCenterService.instance(session).copyVirtualDisk(srcVDK, dstDatastore, dstFilePath, dstFileName);
		} catch (Exception e) {
			return result;
		}
		return result;
	}
}
