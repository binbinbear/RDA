package com.vmware.eucenablement.m2av.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.vmware.eucenablement.horizontoolset.av.api.VolumeAPI;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.AppStack;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Computer;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Message;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Type;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.User;
import com.vmware.eucenablement.jTable.JTableData;
import com.vmware.eucenablement.jTable.JTableRecord;
import com.vmware.eucenablement.service.M2avService;
import com.vmware.eucenablement.service.PoolStack;
import com.vmware.eucenablement.service.VCenterService;
import com.vmware.eucenablement.service.VolumeService;
import com.vmware.view.api.operator.DesktopPool;
import com.vmware.view.api.operator.Machine;
import com.vmware.vim25.mo.Datastore;

/**
 * Servlet implementation class AppVolumesMigrationRestServlet
 * 
 * all requestes were handled by service
 */

public class M2avServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public M2avServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String func = request.getParameter("f");
		HttpSession session = request.getSession();

		Writer out = response.getWriter();
		Object ret = null;

		switch (func) {
		case "login": {
			try {
				M2avService.handleLogin(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		case "logout": {
			session.invalidate();
			response.sendRedirect("./M2avLogin.html");
			return;
		}
		case "viewLogin": {
			M2avService.viewLogin(request, response);
			return;
		}
		case "vcenterLogin": {
			M2avService.vcenterLogin(request, response);
			return;
		}
		case "listPools": {
			if (M2avService.viewAPI(session) == null) {
				return;
			}
			List<DesktopPool> pools = M2avService.viewAPI(session).op.desktopPools.get();
			List<String> poolNames = new ArrayList<>(pools.size());
			for (DesktopPool p : pools) {
				poolNames.add(p.getSummary().name);
			}
			ret = poolNames;
			break;
		}
		case "listAppStacks": {
			// list appStacks
			List<AppStack> appStacks = M2avService.handleListAppStacks(session);
			if (null == appStacks || appStacks.isEmpty()) {
				return;
			}
			List<String> appStackNames = new ArrayList<>();
			for (AppStack appStack : appStacks) {
				appStackNames.add(appStack.file_location + "#" + appStack.name);
			}
			ret = appStackNames;
			break;
		}

		case "listTargets": {
			String poolId = request.getParameter("poolId");
			String targetType = request.getParameter("targetType");

			JTableData data = new JTableData();

			DesktopPool pool = M2avService.viewAPI(session).op.getDesktopPool(poolId);

			if (targetType.equals("user")) {

				// list pool users
				List<String> entitledUsers = pool.entitledUsers.get();

				for (String u : entitledUsers) {
					JTableRecord r = new JTableRecord();
					r.desktop = u;
					if (null == M2avService.getUser(r.desktop, session)) {
						r.status = "No Agent";
						r.appStacks.add("N/A");
					} else {
						r.status = "Enable";
						List<AppStack> appStacks = M2avService.getAppStacks(r.desktop, Type.USER, session);
						if (null != appStacks)
							if (!appStacks.isEmpty())
								for (AppStack appStack : appStacks)
									r.appStacks.add(appStack.name);
							else
								r.appStacks.add("N/A");

						else
							r.appStacks.add("N/A");
					}
					data.Records.add(r);
				}

			} else {
				// list pool computers
				List<Machine> machines = pool.machines.get();
				for (Machine m : machines) {

					JTableRecord r = new JTableRecord();
					String dns_name = m.getSummaryView().base.dnsName;
					String dnsName = null;
					if (dns_name.contains(".")) {
						r.desktop = dns_name.substring(0, dns_name.indexOf('.'));
						dnsName = dns_name.substring(0, dns_name.lastIndexOf('.'));
					} else {
						r.desktop = dns_name;
						dnsName = dns_name;
					}
					if (null == M2avService.getComputer(dnsName, session)) {
						r.status = "No Agent";
						r.appStacks.add("N/A");
					} else {
						r.status = "Enable";
						List<AppStack> appStacks = M2avService.getAppStacks(dnsName, Type.COMPUTER, session);
						if (null != appStacks)
							if (!appStacks.isEmpty())
								for (AppStack appStack : appStacks)
									r.appStacks.add(appStack.name);
							else
								r.appStacks.add("N/A");

						else
							r.appStacks.add("N/A");
					}
					data.Records.add(r);
				}
			}
			data.TotalRecordCount = data.Records.size();
			ret = data;
			break;
		}
		case "assignments": {

			String appStack = request.getParameter("appStack").trim();
			if (appStack != null && !"".equals(appStack)) {
				appStack = appStack.substring(0, appStack.lastIndexOf("#"));
			}
			String poolId = request.getParameter("poolId").trim();
			String targetType = request.getParameter("targetType").trim();
			String real = request.getParameter("type").trim();
			DesktopPool pool = M2avService.viewAPI(session).op.getDesktopPool(poolId);
			if ("user".equals(targetType)) {
				// list pool users
				List<String> entitledUsers = pool.entitledUsers.get();
				List<String> names = new ArrayList<>();
				for (String user : entitledUsers) {
					String name = user.trim();
					User vollumeUser = M2avService.getUser(name, session);
					if (null != vollumeUser) {
						names.add(vollumeUser.getDomainName());
					}
				}
				if (names.isEmpty())
					ret = "All of this users have no agent";
				else
					ret = M2avService.assignmentUser(appStack, names, real, session);

			} else {
				// list pool computers
				List<Machine> machines = pool.machines.get();
				List<String> list_names = new ArrayList<>();
				for (Machine m : machines) {
					String dns_name = m.getSummaryView().base.dnsName;
					String name = null;
					if (dns_name.contains(".")) {
						name = dns_name.substring(0, dns_name.lastIndexOf('.')).toUpperCase();
					} else {
						name = m.getSummaryView().base.dnsName.toUpperCase();
					}
					Computer computer = M2avService.getComputer(name.toUpperCase(), session);
					if (null != computer)
						list_names.add(computer.getDomainName());
				}
				if (list_names.isEmpty()) {

					ret = "All of this computers have no agent";
				} else {

					ret = M2avService.assignmentComputer(appStack, list_names, real, session);
					PoolStack.put(poolId.toLowerCase(), M2avService.getAppStack(appStack, session).name);
				}
			}
			break;
		}
		case "unassignments": {
			String appStack = request.getParameter("appStack").trim();
			if (appStack != null && !"".equals(appStack)) {
				appStack = appStack.substring(0, appStack.lastIndexOf("#"));
			}
			String poolId = request.getParameter("poolId").trim();
			String targetType = request.getParameter("targetType").trim();
			String real = request.getParameter("type").trim();
			DesktopPool pool = M2avService.viewAPI(session).op.getDesktopPool(poolId);

			if ("user".equals(targetType)) {
				// list pool users
				List<String> entitledUsers = pool.entitledUsers.get();
				List<String> list_names = new ArrayList<>();
				for (String str_user : entitledUsers) {
					String name = str_user.trim();
					User user = M2avService.getUser(name, session);
					if (null != user) {
						list_names.add(user.getDomainName());
					}
					if (list_names.isEmpty())
						ret = "All of this users have no agent";
					else
						ret = M2avService.unassignmentUser(appStack, list_names, real, session);
				}

			} else {
				// list pool computers
				List<Machine> machines = pool.machines.get();
				List<String> list_names = new ArrayList<>();
				for (Machine m : machines) {
					String dns_name = m.getSummaryView().base.dnsName;
					String name = null;
					if (dns_name.contains(".")) {
						name = dns_name.substring(0, dns_name.lastIndexOf('.')).toUpperCase();
					} else {
						name = m.getSummaryView().base.dnsName;
					}
					Computer computer = M2avService.getComputer(name, session);
					if (null != computer)
						list_names.add(computer.getDomainName());
				}
				if (list_names.isEmpty()) {
					ret = "All of this computers have no agent";
				} else {
					ret = M2avService.unassignmentComputer(appStack, list_names, real, session);
					PoolStack.delete(poolId.toLowerCase(), M2avService.getAppStack(appStack, session).name);
				}
			}
			break;
		}
		case "listDatastores": {
			// return list of Data stores in vCenter where AppVolumes is located
			List<Datastore> dataStories = M2avService.getvCenterDatastores(session);
			if (null != dataStories) {
				List<String> names = new ArrayList<String>();
				for (Datastore dataStore : dataStories) {
					names.add(dataStore.getName());
				}
				ret = names;
			}
			break;
		}
		case "copyAppStack": {
			String srcAppStack = request.getParameter("srcAppStack").trim();
			if (srcAppStack != null && !"".equals(srcAppStack) && srcAppStack.contains("#")) {
				srcAppStack = srcAppStack.substring(0, srcAppStack.lastIndexOf('#'));

			}
			String tgtAppStack = request.getParameter("tgtAppStack").trim();
			String storageName = request.getParameter("storageName").trim();
			String tgtPath = request.getParameter("tgtPath").trim();
			// String description = request.getParameter("description").trim();

			VolumeAPI volumeAPI = VolumeService.instance(session).volume;
			AppStack srcApp = volumeAPI.getAppStack(srcAppStack);
			M2avService.copyAppStack(session, srcApp, storageName, tgtPath, tgtAppStack);
			List<String> successes = new ArrayList<String>();
			try {
				Thread.sleep(10 * 1000);
			} catch (Exception e) {

			}
			// Import the new copied AppStack
			VCenterService api = VCenterService.instance(session);
			String datacenter = api.getDatacenter(storageName);
			ret = volumeAPI.importAppStack(datacenter, storageName, tgtPath, false);
			volumeAPI.refreshAppStacks();
			try {
				Thread.sleep(10 * 1000);
			} catch (Exception e) {

			}
			successes.add("AppStack copied successfully!");
			ret = new Message(successes, null);
			break;
		}
		case "refresh": {
			M2avService.refreshAppStacks(request, response);
			break;
		}
		default:
			break;
		}
		Gson gson = new Gson();
		String resp = gson.toJson(ret);
		out.write(resp);
	}
}
