Welcome to Horizon Toolbox! 
Horizon Toolbox should be installed on your view connection server.
Prerequisite:
     "JRE_HOME" environment variable with JRE 7 or later.
     For example, set "JRE_HOME" to be "C:\Program Files\VMware\VMware View\Server\jre\"

Installation guide:	 		 
	 Step 1: Unzip "HorizonToolbox1.5.zip" to any folder, for example, the target folder is "C:/HorizonToolbox1.5/". 
     Step 2: Open "Command Prompt" and go to "bin" folder in your target folder, for example, "cd C:/HorizonToolbox1.5/bin"
     Step 3: Execute "service.bat install", you should see "The service 'Tomcat8' has been installed."
     	Trouble shoot: If you see an error message "JRE_HOME not found", please make sure that the environment variable "JRE_HOME" take effect.
	 Step 4(Optional): Edit your fire wall inbound rule for allowing 18443 port. 
	 	Tip: You can change the default 18443 port to any other port.
	  
Startup:
     Double click "tomcat8w.exe",  you will see a GUI.
     Switch to "Java" tab, and adjust the "Maximum memory pool" to "512" or bigger, Click "Apply"
     Switch to "General" tab, click "Start"
     You can close this GUI after your service is started.
     
Shutdown:
	Double click "tomcat8w.exe" to launch the GUI, click "stop"

UnInstall: 
    Step 1: "Command Prompt" and go to "bin" folder in your target folder, for example, "cd C:/HorizonToolbox1.5/bin"
    Step 2: Execute "service.bat remove" 
    Step 3: Optional: Delete "HorizonToolbox1.5" folder 

Optional configurations:
    If you want to specify HTTPS port or SSL certificate:
        Edit "HorizonToolbox1.5\conf\server.xml", modify: 
        <Connector port="18443" protocol="org.apache.coyote.http11.Http11NioProtocol" maxThreads="150" SSLEnabled="true" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="conf/toolbox.keystore" keystorePass="123456" /> 
       
    If your View connection server is not installed in the default folder:
    	Edit "HorizonToolbox1.5\webapps\toolbox\WEB-INF\applicationContext.xml", 
	    Modify "<value>C:\Program Files\VMware\VMware View\Server</value>" according to your folder path.

	If your View administrator UI(https://[connectionserver]/admin) does not use the default "443" port:
    	Edit "HorizonToolbox1.5\webapps\toolbox\WEB-INF\spring-servlet.xml",  
    	Modify "<value>localhost</value>" according to your port, like "<value>localhost:1443</value>"
    
    Please shutdown and restart Horizon Toolbox with "tomcat8w.exe" after you have saved your configuration. 

Usage guide:
1, Login with IE9/10, Chrome, Firefox or Safari. 
https://[YourServer]:18443/toolbox/Login
   You can login with either "Read only administrators" or "Administrators". 
   "Read only administrators" can't enable CEIP, can't setup device access policy.
2, Auditing
   2.1 Sessions
   This is the default page, showing the historical concurrent sessions trend. This function relies on EVENT DB.
   This page also shows current live virtual desktop sessions by desktop pools, and virtual application sessions by RDS (Remote Desktop Service) Farms.
   2.2 Users Usage
   This page shows the accumulated using time for last 2 days/7 days/1 month. When ther are more than 32 users, only the top 32 users are shown.
   This page also shows the connections for last 2 days/7 days/1 month with connection time, disconnection time, user name, pool name or farm name, machine name 
   This function relies on EVENT DB.
   2.3 Snapshots
   This page shows parent virtual machines of linked clone desktop pools and descendant snapshots in a tree view. 
   The snapshots not in use by linked clone pools are marked in grey,  so that View administrator can remove the snapshots not in use. 
   2.4 Clients
   This page shows statistics for operation systems and versions of View clients in different types of view styles.
   This function relies on CEIP(customer experience improvement program)
3, Remote Assistance     
   Remote Assistance provides the capability for administrator or IT helpdesk to remotely view and/or control end-user's desktop, in Horizon View environment. 
   How to setup
	A. Download Horizon_Remote_Assistance_Installer.exe
	B. Install component for end-user desktop
		Run the installer in end-user desktop, e.g. virtual machine which has View agent, and choose the "Install for end-user" option to install the end-user component.
		Normally you can do this on the master template of a View desktop pool, or configure the Group Policy in AD Controller, e.g. for pool specific OU.
	C. Install component for Helpdesk.
		Run the same installer in desktop from where the administrator or IT helpdesk will do the remote assistance. 
		Choose the "Install for helpdesk" option to install this component.

	User Scenario
	User creates support request: 
		Desktop end-user clicks "Horizon Remote Assistance" icon on his/her View desktop, to initiate the remote assist request.
	Admin support: 
		Administrator sees requests from the web portal. and by clicking the start button associated with a request to launch the support.
	User confirmation: 
		A message box is shown in user desktop, and user confirmation is needed to establish the connection.
	Further, full control can be requested by administrator, and still user confirmation is needed.
	
4, Device Access Policy
	Device Access Policy provides a whitelist to control devices that can access Horizon View. 
	When View client from a device accesses the configured View desktop, it appears in the Access Log table. 
	If the client device is not in the whitelist, the connection will be reset. 
	Administrator can add a device to the whitelist, by clicking the Add icon from the Access Log table.

	How to setup
	A. Download DeviceFilter.exe
	B. Install DeviceFilter.exe in View Desktop
		Put the the DeviceFilter.exe on View Desktops for which you want to support Device Filter policy. 
		Configure the OnConnect and OnReconnect event to point to DeviceFilter.exe.
		Refer to this document about how to configure the OnConnect and OnReconnect eventL
		http://pubs.vmware.com/view-50/index.jsp?topic=/com.vmware.view.administration.doc/GUID-AB42F842-BD66-4856-9E61-1A392BF93B6F.html

		The command line should be:
		DeviceFilter.exe -client
		Normally you can do this on the master template of a View desktop pool, or configure the Group Policy in AD Controller, e.g. for pool specific OU.

	C. Make sure the View connection server has Horizon Toolbox installed.
		That's all. The whitelist can be managed by https://your_View_connection_server/toolbox/deviceFilter
		


    