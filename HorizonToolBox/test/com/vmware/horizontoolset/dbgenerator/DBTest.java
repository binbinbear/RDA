  package com.vmware.horizontoolset.dbgenerator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class DBTest {

	@Test
	public void test() {

        // simple DS for test (not for production!)
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("administrator");
        dataSource.setUrl("jdbc:h2:10.112.117.174");
        dataSource.setPassword("asdf");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        /**
        14	Agent	AGENT_ENDED	User STENGDOMAIN\Administrator has logged off machine DEMO	2014-09-04 22:38:45.763	com.vmware.vdi.events.client.EventLogger	INFO	DEMO.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-500	stengpool	fdc926d0-23bc-4db7-a5b4-012ed0978c8e	NULL	NULL	NULL	NULL	NULL	NULL
        15	Admin	ADMIN_ADD_DESKTOP_ENTITLEMENT	stengdomain.fvt\steng was entitled to Pool stengpool by STENGDOMAIN\administrator	2014-09-04 22:39:46.717	com.vmware.vdi.admin.be.DesktopManager	AUDIT_SUCCESS	VwConnSvrWin2K8.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-500	stengpool	NULL	NULL	NULL	NULL	NULL	NULL	NULL
        16	Broker	BROKER_USERLOGGEDOUT	User STENGDOMAIN\administrator has logged out	2014-09-04 22:39:59.997	net.propero.modules.properOps.UserSessionTracker	AUDIT_SUCCESS	VwConnSvrWin2K8.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-500	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL
        17	Broker	BROKER_USERLOGGEDIN	User STENGDOMAIN\steng has logged in	2014-09-04 22:40:10.930	net.propero.modules.properOps.UserSessionTracker	AUDIT_SUCCESS	VwConnSvrWin2K8.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-1107	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL
        18	Broker	BROKER_DESKTOP_REQUEST	User STENGDOMAIN\steng requested Pool stengpool	2014-09-04 22:40:20.927	com.vmware.vdi.broker.DesktopsHandler	INFO	VwConnSvrWin2K8.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-1107	stengpool	NULL	NULL	NULL	NULL	NULL	NULL	NULL
        19	Broker	BROKER_MACHINE_ALLOCATED	User STENGDOMAIN\steng requested Pool stengpool, allocated machine stengagent	2014-09-04 22:40:20.947	com.vmware.vdi.sessionclientapi.FarmImp	INFO	VwConnSvrWin2K8.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-1107	stengpool	fdc926d0-23bc-4db7-a5b4-012ed0978c8e	NULL	NULL	NULL	NULL	NULL	NULL
        20	Agent	AGENT_PENDING	The agent running on machine DEMO has accepted an allocated session for user STENGDOMAIN\steng	2014-09-04 22:40:22.433	com.vmware.vdi.events.client.EventLogger	INFO	DEMO.stengdomain.fvt	1	S-1-5-21-3943029085-2919150323-1598096966-1107	stengpool	fdc926d0-23bc-4db7-a5b4-012ed0978c8e	NULL	NULL	NULL	NULL	NULL	NULL
       **/
       
       //     jdbcTemplate.update(
       //             "INSERT INTO customers(first_name,last_name) values(?,?)",
      //              name[0], name[1]);

	
	}

}
