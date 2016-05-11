#include "StringUtil.h"
#include <Windows.h>

CStringUtil::CStringUtil()
{

}

CStringUtil::~CStringUtil()
{

}

std::string& CStringUtil::LTrim(std::string &s)
{
	return s.erase(0, s.find_first_not_of(" \t\n\r"));
}

std::string& CStringUtil::RTrim(std::string &s)
{
	return s.erase(s.find_last_not_of(" \t\n\r")+1);
}

std::string& CStringUtil::Trim(std::string &s)
{
	return RTrim(LTrim(s));
}

std::wstring& CStringUtil::LTrim(std::wstring &s)
{
	return s.erase(0, s.find_first_not_of(L" \t\n\r"));
}

std::wstring& CStringUtil::RTrim(std::wstring &s)
{
	return s.erase(s.find_last_not_of(L" \t\n\r")+1);
}

std::wstring& CStringUtil::Trim(std::wstring &s)
{
	return RTrim(LTrim(s));
}

std::string CStringUtil::TransferWStringToUnicodePage(std::wstring& s)
{
	std::string sUnicodePage = "";
	char octPerWchar[16] = {0};
	for (int i = 0; i < s.length(); i++)
	{
		memset(octPerWchar, 0, 16);
		sprintf(octPerWchar, "\\u%04x", s[i]);
		sUnicodePage += octPerWchar;
	}

	return sUnicodePage;
}

std::wstring CStringUtil::TransferUnicodePageToWString(std::string& s)
{
	std::wstring w = L"";
	bool bUnicodePage = false;
	if (s.length() >= 5)
	{
		if ((s[0] == '\\') && (s[1] == 'u') && ((s.length() % 6) == 0))
		{
			bUnicodePage = true;
		}
	}

	if (bUnicodePage)
	{
		int len = s.length();
		for (int i = 0; i < len; i = i + 6)
		{
			wchar_t wChar;
			std::string sHex = s.substr(i+2, i+5);
			sprintf((char*)sHex.c_str(), "%x", &wChar);
		}
	}
	else
	{
		int len = strlen(s.c_str())+1;  
		wchar_t * wDest = new wchar_t[len];  
		memset(wDest, 0, len * sizeof(wchar_t));
		MultiByteToWideChar(CP_UTF8, 0, s.c_str(), len, wDest, len);  
		w = wDest;
		delete []wDest;
		wDest = NULL;
	}

	return w;
}

std::wstring CStringUtil::StringToWString(const std::string& strString, const std::string strLocale /*= ""*/)
{
	std::wstring strConvert;
	wchar_t* pchConvert = new wchar_t[strString.size() + 1];
	if(pchConvert)
	{
		memset(pchConvert, 0, (strString.size() + 1) * sizeof(wchar_t));
		char* pchCurrentLocale = setlocale(LC_ALL, NULL);
		setlocale(LC_ALL, strLocale.c_str());
		mbstowcs(pchConvert, strString.c_str(), strString.size());
		setlocale(LC_ALL, pchCurrentLocale);
		strConvert = pchConvert;
		delete [] pchConvert;
	}
	return strConvert;
}

std::string CStringUtil::WStringToString(const std::wstring& strString, const std::wstring strLocale /*= L""*/)
{
	std::string strConvert;
	unsigned int uConvertLen = (strString.size() + 1) * sizeof(wchar_t);
	unsigned int uLocaleLen = (strLocale.size() + 1) * sizeof(wchar_t);
	char* pchConvert = new char[uConvertLen];
	char* pchLocale = new char[uLocaleLen];
	do 
	{
		if(pchConvert == NULL || pchLocale == NULL)
		{
			break;
		}
		memset(pchConvert, 0, uConvertLen);
		memset(pchLocale, 0, uLocaleLen);

		char* pchCurrentLocale = setlocale(LC_ALL, NULL);
		wcstombs(pchLocale, strLocale.c_str(), uLocaleLen);
		setlocale(LC_ALL, pchLocale);
		wcstombs(pchConvert, strString.c_str(), uConvertLen);
		setlocale(LC_ALL, pchCurrentLocale);

		strConvert = pchConvert;
	}while(false);

	if(pchConvert)
	{
		delete [] pchConvert;
	}
	if(pchLocale)
	{
		delete [] pchLocale;
	}

	return strConvert;
}