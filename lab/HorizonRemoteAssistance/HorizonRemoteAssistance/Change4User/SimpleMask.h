#pragma once

#include <string>

class CSimpleMask
{
public:
	CSimpleMask(void);
	~CSimpleMask(void);

public:
	static std::string mask(std::string plaintext);
	static std::wstring Unmask(std::string strEncoded);
};
