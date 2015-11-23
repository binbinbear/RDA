Welcome to VMware Horizon Toolbox 2.0! 
VMware Horizon Toolbox 2.0 is a Web portal that acts as an extension to View Administrator in VMware Horizon™ 6. 

Horizon Toolbox should be installed on your Horizon view connection server 6.0 or above.
	  
Start & Stop
Horizon Toolbox starts automatically. But you can use “tomcat8w.exe” to start/stop it.  You can find “tomcat8w.exe” in “C:\Program Files\VMware\HorizonToolbox\HorizonToolbox2.0.0\bin” folder if you use the default installation folder.

Optional Configurations
  HTTPS port and SSL certificate
	Edit your firewall inbound rule for allowing 18443 port.
	If you want to change HTTPS port or SSL certificate, please edit "HorizonToolbox2.0\conf\server.xml": 
        <Connector port="18443" protocol="org.apache.coyote.http11.Http11NioProtocol" maxThreads="150" SSLEnabled="true" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="conf/ 
  Horizon View Connection Server configuration
	If your Horizon View connection server is not installed in the default folder, please edit "HorizonToolbox2.0\webapps\toolbox\WEB-INF\applicationContext.xml":
	"<value>C:\Program Files\VMware\VMware View\Server</value>" according to your folder path.
	If your Horizon View administrator UI(https://[connectionserver]/admin) does not use the default "443" port, please edit "HorizonToolbox2.0\webapps\toolbox\WEB-INF\spring-servlet.xml":
	"<value>localhost</value>" according to your port, like "<value>localhost:1443</value>"

  Please restart "Horizon Toolbox" after you have saved your configuration.

Usage
  Access
	Open https://[YourServer]:18443/toolbox/ with IE9/10, Chrome, Firefox or Safari. IE8 is not supported. 18443 is the default port and can be changed. You can login with your "Horizon Administrator" account (or read-only administrator account).   
  Auditing
	Sessions
   	  This is the default page, showing the historical concurrent sessions’ trend. This function relies on EVENT DB.
   	  This page also shows current live virtual desktop sessions by desktop pools, and virtual application sessions by RDS (Remote Desktop Service) Farms.
	Users Usage
   	  This page shows the accumulated using time for last 2 days/7 days/1 month/6 months. When there are more than 32 users, only the top 32 users are shown.
   	  This page also shows the connections for last 2 days/7 days/1 month with connection time, disconnection time, user name, pool name or farm name, machine name.
   	  This function relies on EVENT DB.
	Snapshots
   	  This page shows parent virtual machines of linked clone desktop pools and descendant snapshots in a tree view. 
   	  The snapshots not in use by linked clone pools are marked in grey, so that View administrator can remove the snapshots not in use. 
   	Clients
   	  This page shows statistics for operation systems and versions of View clients in different types of view styles.
   	  This function relies on CEIP (customer experience improvement program).
   	  This page also shows the detailed information for all broker sessions, including user name, Client IP addresses, login time and log out time. 
  Remote Assistance     
	Remote Assistance provides the capability for administrator or IT helpdesk to remotely view and/or control end-user's desktop, in Horizon View environment. It's also called session shadowing. 
	Please read https://[your_server]:18443/toolbox/static/ra/help.html  for more information.
  Console access     
	This page lists all VMs for desktop pools, and you can filter VMs by VM name or DNS name. Clicking the VM name, the vSphere console for that VM will pop out. 
  Power Policy
	This page lists all desktop pools and their power policies. Power Policy can power on all VMs in a desktop pool with some schedule, like 8:00 AM on all work days. You can setup power policy for each desktop pool.	

Trouble Shooting
  Toolbox logs
	C:\Program Files\VMware\HorizonToolbox\HorizonToolbox2.0\webapps\toolbox\horizontoolbox.log
	And 
	C:\Program Files\VMware\HorizonToolbox\HorizonToolbox2.0.0\logs\*.log
  Auditing related      
	"Event Database" should be configured in "View Administrator". Refer to this link if you don't know how to configure Event Database:
      https://pubs.vmware.com/horizon-view-60/index.jsp?topic=%2Fcom.vmware.horizon-view.installation.doc%2FGUID-1360BFDF-9F90-47FD-8B6C-E842CF951A53.html       
  Remote assistance related
	Client side logs:
 	C:\ProgramData\VMware\Horizon Remote Assistance\requestor_<UserName>.log
	For example:
 	C:\ProgramData\VMware\Horizon Remote Assistance\requestor_nanw.log
