package com.vmware.horizontoolset.util;

import java.io.InvalidClassException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PolEntry {
    private ArrayList<Byte> byteList;

    //private PolEntryType type;
    private int type;
    private String keyName;
    private String valueName;
    private int dWORDValue;

    private ArrayList<Byte> DataBytes;

    public String getKeyName(){
    	return this.keyName;
    }
    
    public void setKeyName(String keyName){
    	this.keyName = keyName;
    }

	public ArrayList<Byte> getDataBytes() {
		return this.byteList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	
	public int getDWORDValue() throws Exception{
		byte[] bytes = new byte[this.byteList.size()];
		for(int i=0;i<this.byteList.size();i++){  
			bytes[i]=(byte)this.byteList.get(i);  
        }
//		byte[] bytes = (byte[])this.byteList.toArray(new byte[this.byteList.size()]); 

        switch (this.type)
        {
            case PolEntryType.REG_NONE:
            case PolEntryType.REG_SZ:
            case PolEntryType.REG_MULTI_SZ:
            case PolEntryType.REG_EXPAND_SZ:
                int result;
//                if (UInt32.TryParse(this.StringValue, out result))
//                {
//                    return result;
//                }
//                else
//                {
//                    throw new InvalidCastException();
//                }
            case PolEntryType.REG_DWORD:
                if (bytes.length != 4) { throw new InvalidClassException(keyName); }
//                if (BitConverter.IsLittleEndian == false) { Array.Reverse(bytes); }
                return BitConverter.toInt32(bytes, 0);
            case PolEntryType.REG_DWORD_BIG_ENDIAN:
                if (bytes.length != 4) { throw new InvalidClassException(keyName); }
//                if (BitConverter.IsLittleEndian == true) { Array.Reverse(bytes); }
                return BitConverter.toInt32(bytes, 0);
            case PolEntryType.REG_QWORD:
                if (bytes.length != 8) { throw new InvalidClassException(keyName); }
//                if (BitConverter.IsLittleEndian == false) { Array.Reverse(bytes); }
                long lvalue = BitConverter.toInt64(bytes, 0);
//
//                if (lvalue > UInt32.MaxValue || lvalue < UInt32.MinValue)
//                {
//                    throw new OverflowException("QWORD value '" + lvalue.ToString() + "' cannot fit into an UInt32 value.");
//                }
//
                return (int)lvalue;
            case PolEntryType.REG_BINARY:
                if (bytes.length != 4) { throw new InvalidClassException(keyName); }
                return BitConverter.toInt32(bytes, 0);
            default:
                throw new Exception("Reached default cast that should be unreachable in PolEntry.UIntValue");
        }
	}
	public void setDWORDValue( int value ){
		this.setType(PolEntryType.REG_DWORD);
		this.byteList.clear();
		byte[] arrBytes = BitConverter.getBytes(value);
//		if (BitConverter.isLittleEndian == false) { Array.Reverse(arrBytes); }
		for (byte arrByte : arrBytes){
			this.byteList.add(arrByte);
		}
	}
	
	public static int int32Converter(byte b[], int start) {
	    return ((b[start] << 24) & 0xff000000 |(b[start + 1] << 16) & 0xff0000
	            | (b[start + 2] << 8) & 0xff00 | (b[start + 3]) & 0xff);
	}
	public static long int64Converter(byte buf[], int start) {
	    return ((buf[start] & 0xFFL) << 56) | ((buf[start + 1] & 0xFFL) << 48)
	            | ((buf[start + 2] & 0xFFL) << 40)
	            | ((buf[start + 3] & 0xFFL) << 32)
	            | ((buf[start + 4] & 0xFFL) << 24)
	            | ((buf[start + 5] & 0xFFL) << 16)
	            | ((buf[start + 6] & 0xFFL) << 8)
	            | ((buf[start + 7] & 0xFFL) << 0);
	}
}
