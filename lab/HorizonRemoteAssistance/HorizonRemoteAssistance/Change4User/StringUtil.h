#ifndef _H_STRING_UTIL
#define _H_STRING_UTIL

#include <string>

class CStringUtil
{
private:
	CStringUtil();
	virtual ~CStringUtil();

public:
	static std::string& LTrim(std::string &s);
	static std::string& RTrim(std::string &s);
	static std::string& Trim(std::string &s);
	static std::wstring& LTrim(std::wstring &s);
	static std::wstring& RTrim(std::wstring &s);
	static std::wstring& Trim(std::wstring &s);
	static std::string TransferWStringToUnicodePage(std::wstring& s);
	static std::wstring TransferUnicodePageToWString(std::string& s);
	static std::wstring StringToWString(const std::string& strString, const std::string strLocale = "");
	static std::string WStringToString(const std::wstring& strString, const std::wstring strLocale = L"");
};
#endif