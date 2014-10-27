package com.vmware.horizontoolset.dbgenerator;

import static org.junit.Assert.*;


import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.junit.Test;

public class DBTest {

	// simple DS for test (not for production!)
	/**
	 * 14 Agent AGENT_ENDED User STENGDOMAIN\Administrator has logged off
	 * machine DEMO 2014-09-04 22:38:45.763
	 * com.vmware.vdi.events.client.EventLogger INFO DEMO.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-500 stengpool
	 * fdc926d0-23bc-4db7-a5b4-012ed0978c8e NULL NULL NULL NULL NULL NULL 15
	 * 
	 * Admin ADMIN_ADD_DESKTOP_ENTITLEMENT stengdomain.fvt\steng was entitled to
	 * Pool stengpool by STENGDOMAIN\administrator 2014-09-04 22:39:46.717
	 * com.vmware.vdi.admin.be.DesktopManager AUDIT_SUCCESS
	 * VwConnSvrWin2K8.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-500 stengpool NULL NULL NULL
	 * NULL NULL NULL NULL 16 Broker BROKER_USERLOGGEDOUT User
	 * STENGDOMAIN\administrator has logged out 2014-09-04 22:39:59.997
	 * net.propero.modules.properOps.UserSessionTracker AUDIT_SUCCESS
	 * VwConnSvrWin2K8.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-500 NULL NULL NULL NULL NULL
	 * NULL NULL NULL 17 Broker BROKER_USERLOGGEDIN User STENGDOMAIN\steng has
	 * logged in 2014-09-04 22:40:10.930
	 * net.propero.modules.properOps.UserSessionTracker AUDIT_SUCCESS
	 * VwConnSvrWin2K8.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-1107 NULL NULL NULL NULL NULL
	 * NULL NULL NULL 18 Broker BROKER_DESKTOP_REQUEST User STENGDOMAIN\steng
	 * requested Pool stengpool 2014-09-04 22:40:20.927
	 * com.vmware.vdi.broker.DesktopsHandler INFO
	 * VwConnSvrWin2K8.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-1107 stengpool NULL NULL NULL
	 * NULL NULL NULL NULL
	 * 
	 * 19 Broker BROKER_MACHINE_ALLOCATED User STENGDOMAIN\steng requested Pool
	 * stengpool, allocated machine stengagent 2014-09-04 22:40:20.947
	 * com.vmware.vdi.sessionclientapi.FarmImp INFO
	 * VwConnSvrWin2K8.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-1107 stengpool
	 * fdc926d0-23bc-4db7-a5b4-012ed0978c8e NULL NULL NULL NULL NULL NULL
	 * 
	 * 
	 * 20
	 * 
	 * Agent AGENT_PENDING The agent running on machine DEMO has accepted an
	 * allocated session for user STENGDOMAIN\steng 2014-09-04 22:40:22.433
	 * com.vmware.vdi.events.client.EventLogger INFO DEMO.stengdomain.fvt 1
	 * S-1-5-21-3943029085-2919150323-1598096966-1107 stengpool
	 * fdc926d0-23bc-4db7-a5b4-012ed0978c8e NULL NULL NULL NULL NULL NULL
	 **/
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getRandomNum(int length) { // length表示生成字符串的长度
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}


	public static String ModuleAndEvent(int i, String s1, String s2) {
		String s;
		if (i == 1) {
			s = "The agent running on machine ";
			s = s + s1;
			s = s + " has accepted an allocated session for user ";
			s = s + s2;
		} else {
			s = "User ";
			s = s + s1;
			s = s + " has logged off machine ";
			s = s + s2;
		}
		return s;
	}
	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		try {
			String[] useid = new String[50];
			for(int j=0;j<50;j++)
				useid[j]=getRandomString(4);
			String[] maid = new String[50];
			for(int j=0;j<50;j++)
				maid[j]=getRandomString(4);
			
			Random rand = new Random();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.set(2014, 9, 1);
			long start = cal.getTimeInMillis();
			cal.set(2014, 9, 30);
			long end = cal.getTimeInMillis();
			Date d ;
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			System.out.println("DB Driver has registered successfully！");
			String url = "jdbc:sqlserver://10.112.117.174:1433;DatabaseName=View";
			String user = "administrator";
			String password = "ca$hc0w";
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("DB has been connected successfully");

			String[] EventType = { "AGENT_PENDING", "AGENT_ENDED" };
			String Source = "com.vmware.vdi.events.client.EventLogger";
			String Node = "DEMO.stengdomain.fvt";
			String DesktopId = "stengpool";
			
			
			
			
			
		
			String sql,sql1,sql2;
			PreparedStatement pstmt = null;

			for (int i = 0; i < 2; i++) {
				try {
					// String use=getRandomString(4);
					sql = "INSERT INTO event(Module,EventType,ModuleAndEventText,Time,Source,Severity,Node,Acknowledged,DesktopId) VALUES(?,?,?,?,?,?,?,?,?)";
					pstmt = conn.prepareStatement(sql); // 实例化PreapredStatement对象
					
					String use = useid[(int)(Math.random()*50)];
					String ma = maid[(int)(Math.random()*50)];
					d= new Date(start + (long)(rand.nextDouble() * (end - start)));
					Timestamp tt = new Timestamp(d.getTime());
					// pstmt.setString(1, getRandomString(new
					// Random().nextInt(7) + 1));
					pstmt.setString(1, "Agent");
					pstmt.setString(2, EventType[0]);
					pstmt.setString(3, ModuleAndEvent(1, ma, use));
					pstmt.setTimestamp(4, tt);
					pstmt.setString(5, Source);
					pstmt.setString(6, "INFO");
					pstmt.setString(7, Node);
					pstmt.setInt(8, 1);
					pstmt.setString(9, DesktopId);
					pstmt.executeUpdate();
					System.out.println("Success");

					sql1 = "SELECT TOP 1 EventID FROM event order by EventID desc";
					pstmt = conn.prepareStatement(sql1);
					ResultSet rs = pstmt.executeQuery();
					int ii = 0;
					if (rs.next())
						ii = rs.getInt(1);
					System.out.println(ii);

					sql2 = "INSERT INTO event_data(EventID,Name,StrValue) VALUES(?,?,?)";
					pstmt = conn.prepareStatement(sql2);
					pstmt.setString(2, "UserDisplayName");
					pstmt.setInt(1, ii);
					pstmt.setString(3, use);
					pstmt.executeUpdate();
					pstmt.setString(2, "MachineName");
					pstmt.setString(3, ma);

					pstmt.executeUpdate();
					System.out.println("Success2");

					sql = "INSERT INTO event(Module,EventType,ModuleAndEventText,Time,Source,Severity,Node,Acknowledged,DesktopId) VALUES(?,?,?,?,?,?,?,?,?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, "Agent");
					pstmt.setString(2, EventType[1]);
					pstmt.setString(3, ModuleAndEvent(2, use, ma));
					tt.setHours(tt.getHours() + new Random().nextInt(3) + 1);
					pstmt.setTimestamp(4, tt);
					pstmt.setString(5, Source);
					pstmt.setString(6, "INFO");
					pstmt.setString(7, Node);
					pstmt.setInt(8, 1);
					pstmt.setString(9, DesktopId);
					pstmt.executeUpdate();
					System.out.println("Success3");

					sql2 = "INSERT INTO event_data(EventID,Name,StrValue) VALUES(?,?,?)";
					pstmt = conn.prepareStatement(sql1);
					rs = pstmt.executeQuery();
					ii = 0;
					if (rs.next())
						ii = rs.getInt(1);
					System.out.println(ii);
					pstmt = conn.prepareStatement(sql2);
					pstmt.setString(2, "UserDisplayName");
					pstmt.setInt(1, ii);
					pstmt.setString(3, use);
					pstmt.executeUpdate();
					pstmt.setString(2, "MachineName");
					pstmt.setString(3, ma);
					pstmt.executeUpdate();
					System.out.println("Success4");
					

				}
		catch (SQLException e) {
					System.out.println("<p style=\"font-size:20px;color:red\">"
							+ "出错啦，请联系管理员！" + "错误信息：" + e.toString() + ""
							+ "</p>");
				}
			}
			pstmt.close();
			conn.close(); // 数据库关闭
		} 
	catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		}
	}
}
