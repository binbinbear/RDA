// Change4User.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
//
//#pragma comment(lib, "Wtsapi32.lib")
//#pragma comment(lib, "Userenv.lib")
//#include <Userenv.h>
//#include <Wtsapi32.h>
//#include <string>
//#include <iostream>
//
//
//BOOL enable_privs(HANDLE hToken)
//{
//	TOKEN_PRIVILEGES tp;
//	LUID luid;
//
//	if ( !LookupPrivilegeValue( 
//		NULL,            // lookup privilege on local system
//		SE_TCB_NAME,   // privilege to lookup 
//		&luid ) )        // receives LUID of privilege
//	{
//		printf("LookupPrivilegeValue error: %u\n", GetLastError() ); 
//		std::cout << "LookupPrivilegeValue error: " << GetLastError() << std::endl;
//		return FALSE; 
//	}
//
//	tp.PrivilegeCount = 1;
//	tp.Privileges[0].Luid = luid;
//	tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
//	
//	// Enable the privilege or disable all privileges.
//
//	if ( !AdjustTokenPrivileges(
//		hToken, 
//		FALSE, 
//		&tp, 
//		sizeof(TOKEN_PRIVILEGES), 
//		(PTOKEN_PRIVILEGES) NULL, 
//		(PDWORD) NULL) )
//	{ 
//		printf("AdjustTokenPrivileges error: %u\n", GetLastError() ); 
//		std::cout << "AdjustTokenPrivileges error: " << GetLastError() << std::endl;
//		return FALSE; 
//	} 
//
//	if (GetLastError() == ERROR_NOT_ALL_ASSIGNED)
//
//	{
//		printf("The token does not have the specified privilege. \n");
//		std::cout << "The token does not have the specified privilege."  << std::endl;
//		return FALSE;
//	} 
//	std::cout << "The token done successfully."  << std::endl;
//
//	return TRUE;
//
//	/*
//	struct {
//		DWORD count;
//		LUID_AND_ATTRIBUTES privilege[1];
//	} token_privileges;
//
//	std::cout << "start enable_privs" << std::endl;
//	token_privileges.count = 1;
//	token_privileges.privilege[0].Attributes = SE_PRIVILEGE_ENABLED;
//	//token_privileges.privilege[1].Attributes = SE_PRIVILEGE_ENABLED;
//	//token_privileges.privilege[2].Attributes = SE_PRIVILEGE_ENABLED;
//	//token_privileges.privilege[3].Attributes = SE_PRIVILEGE_ENABLED;
//
//	/*
//	if (!LookupPrivilegeValue(0, SE_INCREASE_QUOTA_NAME, &token_privileges.privilege[0].Luid))
//	{
//		std::cout << "SE_INCREASE_QUOTA_NAME failed!" << std::endl;
//		return FALSE;
//	}
//	if (!LookupPrivilegeValue(0, SE_ASSIGNPRIMARYTOKEN_NAME, &token_privileges.privilege[1].Luid))
//	{
//		std::cout << "SE_ASSIGNPRIMARYTOKEN_NAME failed!" << std::endl;
//		return FALSE;
//	}
//	if (!LookupPrivilegeValue(0, SE_DEBUG_NAME, &token_privileges.privilege[2].Luid))
//	{
//		std::cout << "SE_DEBUG_NAME failed!" << std::endl;
//		return FALSE;
//	}
//	
//	if (!LookupPrivilegeValue(0, SE_TCB_NAME, &token_privileges.privilege[0].Luid))
//	{
//		std::cout << "SE_TCB_NAME failed!" << std::endl;
//		return FALSE;
//	}
//	//if (!OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES, &token))
//	//{
//	//	std::cout << "OpenProcessToken failed!" << std::endl;
//	//	return FALSE;
//	//}
//	if (!AdjustTokenPrivileges(token, 0, (PTOKEN_PRIVILEGES)&token_privileges, 0, 0, 0))
//	{
//		std::cout << "OpenProcessToken failed!" << ::GetLastError() << std::endl;
//		return FALSE;
//	}
//	if (GetLastError() != ERROR_SUCCESS)
//	{
//		std::cout << "enable_privs failed!" << ::GetLastError() << std::endl;
//		return FALSE;
//	}
//
//	std::cout << "start enable_privs" << std::endl;
//	return TRUE;
//	*/
//}
//
//
//void RaisePrivilege()
//{
//
//	HANDLE hToken;
//	TOKEN_PRIVILEGES tp;
//	tp.PrivilegeCount = 1;
//	tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
//	//
//	if( OpenProcessToken(GetCurrentProcess(), TOKEN_ALL_ACCESS, &hToken) )
//	{
//		//if( LookupPrivilegeValue(NULL, SE_DEBUG_NAME, &tp.Privileges[0].Luid) )
//		//{
//		//	AdjustTokenPrivileges(hToken, FALSE, &tp, NULL, NULL, 0);
//		//}
//
//		if( LookupPrivilegeValue(NULL, SE_TCB_NAME, &tp.Privileges[0].Luid) )
//		{
//			AdjustTokenPrivileges(hToken, FALSE, &tp, NULL, NULL, 0);
//		}
//
//		//if( LookupPrivilegeValue(NULL, SE_INCREASE_QUOTA_NAME, &tp.Privileges[0].Luid) )
//		//{
//		//	AdjustTokenPrivileges(hToken, FALSE, &tp, NULL, NULL, 0);
//		//}
//
//		//if( LookupPrivilegeValue(NULL, SE_ASSIGNPRIMARYTOKEN_NAME, &tp.Privileges[0].Luid) )
//		//{
//		//	AdjustTokenPrivileges(hToken, FALSE, &tp, NULL, NULL, 0);
//		//}
//	}
//
//	if(hToken)
//		CloseHandle(hToken);
//
//	//enable_privs();
//}
//
//BOOL RunCmdAsCurrentUser()
//{
//	int nStartedNum = 0;
//
//	while(nStartedNum < 3)
//	{
//		HANDLE hPrimaryToken = NULL; 
//
//		RaisePrivilege();
//		WTSQueryUserToken (WTSGetActiveConsoleSessionId (), &hPrimaryToken) ;
//
//		STARTUPINFOA StartupInfo = {0};  
//		PROCESS_INFORMATION processInfo = {0};  
//
//		StartupInfo.cb = sizeof(STARTUPINFO);  
//		std::string command = "HRAUnsolicitedAdapter.exe";
//		LPVOID lpEnvironment = NULL;
//
//		if (!CreateEnvironmentBlock(&lpEnvironment, hPrimaryToken, TRUE))  
//		{  
//			return 0;
//		}  
//
//		bool bTmp = CreateProcessAsUserA(  
//			hPrimaryToken,   
//			0,   
//			(LPSTR)command.c_str(),   
//			NULL,   
//			NULL,   
//			FALSE,   
//			NORMAL_PRIORITY_CLASS | CREATE_UNICODE_ENVIRONMENT,  
//			lpEnvironment, // __in_opt    LPVOID lpEnvironment,  
//			0,   
//			&StartupInfo,   
//			&processInfo);  
//
//		Sleep(150*1000);
//	}
//
//	return FALSE;
//}
//
////Function to run a process as active user from windows service
//void ImpersonateActiveUserAndRun(WCHAR* path, WCHAR* args)
//{
//	RaisePrivilege();
//
//	DWORD session_id = -1;
//	DWORD session_count = 0;
//
//	WTS_SESSION_INFOA *pSession = NULL;
//
//
//	if (WTSEnumerateSessions(WTS_CURRENT_SERVER_HANDLE, 0, 1, &pSession, &session_count))
//	{
//		std::cout << "WTSEnumerateSessions succcessfully!";
//		//log success
//	}
//	else
//	{
//		//log error
//		std::cout << "WTSEnumerateSessions failed!";
//		return;
//	}
//
//	for (int i = 0; i < session_count; i++)
//	{
//		session_id = pSession[i].SessionId;
//
//		WTS_CONNECTSTATE_CLASS wts_connect_state = WTSDisconnected;
//		WTS_CONNECTSTATE_CLASS* ptr_wts_connect_state = NULL;
//
//		DWORD bytes_returned = 0;
//		if (::WTSQuerySessionInformation(
//			WTS_CURRENT_SERVER_HANDLE,
//			session_id,
//			WTSConnectState,
//			reinterpret_cast<LPTSTR*>(&ptr_wts_connect_state),
//			&bytes_returned))
//		{
//			wts_connect_state = *ptr_wts_connect_state;
//			::WTSFreeMemory(ptr_wts_connect_state);
//			if (wts_connect_state != WTSActive) continue;
//		}
//		else
//		{
//			//log error
//			continue;
//		}
//
//		HANDLE hImpersonationToken = NULL;
//		std::cout << "session id is " << session_id;
//
//		if ((!WTSQueryUserToken(session_id, &hImpersonationToken))|| (NULL == hImpersonationToken))
//		{
//			//log error
//			std::cout << "WTSQueryUserToken, error: " << ::GetLastError();
//			continue;
//		}
//
//
//		std::cout << "WTSQueryUserToken successfully: " << (int)hImpersonationToken << std::endl;
//
//		enable_privs(hImpersonationToken);
//
//		//Get real token from impersonation token
//		/*
//		DWORD neededSize1 = 0;
//		HANDLE *realToken = new HANDLE;
//		if (GetTokenInformation(hImpersonationToken, (::TOKEN_INFORMATION_CLASS) TokenLinkedToken, realToken, sizeof(HANDLE), &neededSize1))
//		{
//			CloseHandle(hImpersonationToken);
//			hImpersonationToken = *realToken;
//		}
//		else
//		{
//			//log error
//			std::cout << "GetTokenInformation, error: " << ::GetLastError() << std::endl;
//			continue;
//		}
//		*/
//
//
//		std::cout << "GetTokenInformation successfully";
//		HANDLE hUserToken;
//
//		if (!DuplicateTokenEx(hImpersonationToken,
//			//0,
//			//MAXIMUM_ALLOWED,
//			TOKEN_ASSIGN_PRIMARY | TOKEN_ALL_ACCESS | MAXIMUM_ALLOWED,
//			NULL,
//			SecurityImpersonation,
//			TokenPrimary,
//			&hUserToken))
//		{
//			//log error
//			std::cout << "DuplicateTokenEx, error: " << ::GetLastError() << std::endl;
//			continue;
//		}
//
//		std::cout << "DuplicateTokenEx successfully";
//
//		STARTUPINFOA StartupInfo = {0};  
//		PROCESS_INFORMATION processInfo = {0};  
//
//		StartupInfo.cb = sizeof(STARTUPINFO);  
//		std::string command = "HRAUnsolicitedAdapter.exe";
//		LPVOID lpEnvironment = NULL;
//
//		if (!CreateEnvironmentBlock(&lpEnvironment, hUserToken, TRUE))  
//		{  
//			return;
//		}  
//
//		bool bTmp = CreateProcessAsUserA(  
//			hUserToken,   
//			0,   
//			(LPSTR)command.c_str(),   
//			NULL,   
//			NULL,   
//			FALSE,   
//			NORMAL_PRIORITY_CLASS | CREATE_UNICODE_ENVIRONMENT,  
//			lpEnvironment, // __in_opt    LPVOID lpEnvironment,  
//			0,   
//			&StartupInfo,   
//			&processInfo);  
//
//
//
//		// Get user name of this process
//		//LPTSTR pUserName = NULL;
//		/*
//		WCHAR* pUserName;
//		DWORD user_name_len = 0;
//
//		if (WTSQuerySessionInformationW(WTS_CURRENT_SERVER_HANDLE, session_id, WTSUserName, &pUserName, &user_name_len))
//		{
//			//log username contained in pUserName WCHAR string
//			std::cout << "WTSQuerySessionInformationW successfully" << std::endl;
//		}
//
//		//Free memory                         
//		if (pUserName) WTSFreeMemory(pUserName);
//
//		ImpersonateLoggedOnUser(hUserToken);
//
//		STARTUPINFOW StartupInfo;
//		GetStartupInfoW(&StartupInfo);
//		StartupInfo.cb = sizeof(STARTUPINFOW);
//		//StartupInfo.lpDesktop = "winsta0\\default";
//
//		PROCESS_INFORMATION processInfo;
//
//		SECURITY_ATTRIBUTES Security1;
//		Security1.nLength = sizeof SECURITY_ATTRIBUTES;
//
//		SECURITY_ATTRIBUTES Security2;
//		Security2.nLength = sizeof SECURITY_ATTRIBUTES;
//
//		void* lpEnvironment = NULL;
//
//		// Get all necessary environment variables of logged in user
//		// to pass them to the new process
//		BOOL resultEnv = CreateEnvironmentBlock(&lpEnvironment, hUserToken, FALSE);
//		if (!resultEnv)
//		{
//			std::cout << "CreateEnvironmentBlock failed" << std::endl;
//			//log error
//			continue;
//		}
//
//		std::cout << "CreateEnvironmentBlock successfully" << std::endl;
//		WCHAR PP[1024]; //path and parameters
//		ZeroMemory(PP, 1024 * sizeof WCHAR);
//		wcscpy(PP, path);
//		wcscat(PP, L" ");
//		wcscat(PP, args);
//
//		// Start the process on behalf of the current user 
//		BOOL result = CreateProcessAsUserW(hUserToken, 
//			NULL,
//			PP,
//			//&Security1,
//			//&Security2,
//			NULL,
//			NULL,
//			FALSE, 
//			NORMAL_PRIORITY_CLASS | CREATE_NEW_CONSOLE,
//			//lpEnvironment,
//			NULL,
//			//"C:\\ProgramData\\some_dir",
//			NULL,
//			&StartupInfo,
//			&processInfo);
//
//		if (!result)
//		{
//			//log error
//			std::cout << "CreateProcessAsUserW failed" << std::endl;
//		}
//		else
//		{
//			//log success
//			std::cout << "CreateProcessAsUserW successfully" << std::endl;
//		}
//		
//
//		DestroyEnvironmentBlock(lpEnvironment);
//
//		CloseHandle(hImpersonationToken);
//		CloseHandle(hUserToken);
//		//CloseHandle(realToken);
//
//		RevertToSelf();
//	}
//
//	WTSFreeMemory(pSession);
//	std::cout << "WTSFreeMemory done" << std::endl;
//}
//*/
//
