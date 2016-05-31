#pragma once

#include <string>
using namespace std;

class ZBase64
{
public:
    static string Encode(const unsigned char* Data,int DataByte);
    static string Decode(const char* Data,int DataByte,int& OutByte);
};