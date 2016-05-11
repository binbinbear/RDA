// Change4User.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#pragma comment(lib, "Wtsapi32.lib")
#pragma comment(lib, "Userenv.lib")
#include <Userenv.h>
#include <Wtsapi32.h>
#include <string>
#include <iostream>
#include "StringUtil.h"


BOOL enable_privs(HANDLE hToken)
{
	TOKEN_PRIVILEGES tp;
	LUID luid;

	if ( !LookupPrivilegeValue( 
		NULL,            // lookup privilege on local system
		SE_TCB_NAME,   // privilege to lookup 
		&luid ) )        // receives LUID of privilege
	{
		printf("LookupPrivilegeValue error: %u\n", GetLastError() ); 
		std::cout << "LookupPrivilegeValue error: " << GetLastError() << std::endl;
		return FALSE; 
	}

	tp.PrivilegeCount = 1;
	tp.Privileges[0].Luid = luid;
	tp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
	
	// Enable the privilege or disable all privileges.

	if ( !AdjustTokenPrivileges(
		hToken, 
		FALSE, 
		&tp, 
		sizeof(TOKEN_PRIVILEGES), 
		(PTOKEN_PRIVILEGES) NULL, 
		(PDWORD) NULL) )
	{ 
		printf("AdjustTokenPrivileges error: %u\n", GetLastError() ); 
		std::cout << "AdjustTokenPrivileges error: " << GetLastError() << std::endl;
		return FALSE; 
	} 

	if (GetLastError() == ERROR_NOT_ALL_ASSIGNED)

	{
		printf("The token does not have the specified privilege. \n");
		std::cout << "The token does not have the specified privilege."  << std::endl;
		return FALSE;
	} 
	std::cout << "The token done successfully."  << std::endl;

	return TRUE;
}

bool LoginBasedOnUserAccount(std::wstring sUser, std::wstring sPassword, std::wstring sDomain, HANDLE& tokenHandle)
{
	bool returnValue = LogonUserW(sUser.c_str(), sDomain.c_str(), sPassword.c_str(),
		2, 0, &tokenHandle);
	if (returnValue)
	{
		std::cout << "Log in success" << std::endl;
	}
	else 
	{
		std::cout << "Log in failed" << ::GetLastError() << std::endl;
	}

	return returnValue;
}

DWORD GetCurrentActiveSessionId()
{
	WTS_SESSION_INFO* pSession = NULL;
	DWORD session_id = -1;
	DWORD session_count = 0;
	std::cout << "GetCurrentActiveSessionId " << std::endl;
	
	if (WTSEnumerateSessions(WTS_CURRENT_SERVER_HANDLE, 0, 1, &pSession, &session_count))
	{
		std::cout << "WTSEnumerateSessions succcessfully!" << std::endl;
	}
	else
	{
		//log error
		std::cout << "WTSEnumerateSessions failed!" << std::endl;
		return session_id;
	}

	for (int i = 0; i < session_count; i++)
	{
		int nTmpSessionId = pSession[i].SessionId;
		WTS_CONNECTSTATE_CLASS wts_connect_state = WTSDisconnected;
		WTS_CONNECTSTATE_CLASS* ptr_wts_connect_state = NULL;

		DWORD bytes_returned = 0;
		if (::WTSQuerySessionInformation(
			WTS_CURRENT_SERVER_HANDLE,
			nTmpSessionId,
			WTSConnectState,
			reinterpret_cast<LPTSTR*>(&ptr_wts_connect_state),
			&bytes_returned))
		{
			wts_connect_state = *ptr_wts_connect_state;
			::WTSFreeMemory(ptr_wts_connect_state);
			if (wts_connect_state != WTSActive) continue;
		}
		else
		{
			//log error
			continue;
		}

		session_id = pSession[i].SessionId;
		break;
	}

	std::cout << "current Active Session id: " << session_id << std::endl;
	return session_id;
}

//Function to run a process as active user from windows service
bool ImpersonateActiveUserAndRun(std::wstring sUser, std::wstring sPassword, std::wstring sDomain, std::wstring sCommand)
{
	bool bResult = false;

	HANDLE hImpersonationToken = NULL;
	int nTick = ::GetTickCount();
	if (LoginBasedOnUserAccount(sUser, sPassword, sDomain, hImpersonationToken) == false)
	{
		std::cout << "Cannot login toolbox server" << std::endl;
		return false;
	}

	std::cout << "Take tickcount: " << (::GetTickCount() - nTick) << std::endl;
	// raise priv
	//enable_privs(hImpersonationToken);

	std::cout << "Before DuplicateTokenEx " << std::endl;
	HANDLE hUserToken = NULL;

	if (!DuplicateTokenEx(hImpersonationToken,
		//0,
		//MAXIMUM_ALLOWED,
		TOKEN_ASSIGN_PRIMARY | TOKEN_ALL_ACCESS | MAXIMUM_ALLOWED,
		NULL,
		SecurityImpersonation,
		TokenPrimary,
		&hUserToken))
	{
		//log error
		std::cout << "DuplicateTokenEx, error: " << ::GetLastError() << std::endl;
		return false;
	}

	std::cout << "DuplicateTokenEx successfully" << std::endl;

	STARTUPINFO StartupInfo = {0};  
	PROCESS_INFORMATION processInfo = {0};  

	StartupInfo.cb = sizeof(STARTUPINFO);  
	//std::string command = "HRAUnsolicitedAdapter.exe";
	LPVOID lpEnvironment = NULL;
	if (!CreateEnvironmentBlock(&lpEnvironment, hUserToken, TRUE))  
	{  
		std::cout << "CreateEnvironmentBlock failed! Error: " << ::GetLastError() << std::endl;
		return false;
	}  

	bResult = CreateProcessAsUser(  
		hUserToken,   
		0,   
		(LPWSTR)sCommand.c_str(),   
		NULL,   
		NULL,   
		FALSE,   
		NORMAL_PRIORITY_CLASS | CREATE_UNICODE_ENVIRONMENT,  
		lpEnvironment, // __in_opt    LPVOID lpEnvironment,  
		0,   
		&StartupInfo,   
		&processInfo);  


	DestroyEnvironmentBlock(lpEnvironment);

	CloseHandle(hImpersonationToken);
	CloseHandle(hUserToken);

	RevertToSelf();

	std::cout << "WTSFreeMemory done" << std::endl;

	return bResult;
}

int _tmain(int argc, _TCHAR* argv[])
{
	std::cout << std::endl;
	std::cout << "Chang4User parameters: " << std::endl;
	for (int i = 0; i < argc; i++)
	{
		std::cout << CStringUtil::WStringToString(argv[i]).c_str() << "; ";
	}
	std::cout << std::endl;

	if (argc == 5)
	{
		std::wstring sUser = argv[1];
		std::wstring sPassword = argv[2];
		std::wstring sDomain = argv[3];
		std::wstring sCommand = argv[4];

		std::cout << "start ImpersonateActiveUserAndRun " << std::endl;

		ImpersonateActiveUserAndRun(sUser, sPassword, sDomain, sCommand);
	}
	else
	{
		std::cout << "Wrong parameter count!" << std::endl;
	}
	
	return 0;
}


