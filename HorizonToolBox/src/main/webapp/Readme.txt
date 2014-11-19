Welcome to Horizon Toolbox!
Installation guide:
     Step 1: Copy the toolbox.war to "C:\Program Files\VMware\VMware View\Server\broker\webapps" on the View Connection Server. 
     Step 2: Wait for 2 minutes, and then open "https://[ViewServer]/toolbox/" with your browser
     
Usage guide:
1, Login
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
		


    