package com.vmware.horizontoolset.util.polfile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BitUtil {

	static final Charset ENCODING = Charset.forName("UTF-16LE");
	
	public static boolean isLittleEndian() {
		//ByteOrder b = ByteOrder.nativeOrder();

		//return b.equals(ByteOrder.LITTLE_ENDIAN);
		
		//JVM is always big-endian. 
		//While we are working on Windows only, we can assume Windows are all little-endian.
		//http://stackoverflow.com/questions/6449468/can-i-safely-assume-that-windows-installations-will-always-be-little-endian

		return false;
	}

	public static int toUInt32(byte[] bytes) {
        if (BitUtil.isLittleEndian() == false) {
        	bytes = Arrays.copyOf(bytes, bytes.length);
        	BitUtil.reverseBytes(bytes);
        }
        
		return toUInt32NoBLEndianConversion(bytes);
	}

	public static long toUInt64(byte[] bytes) {
		if (BitUtil.isLittleEndian() == false) {
        	bytes = Arrays.copyOf(bytes, bytes.length);
        	BitUtil.reverseBytes(bytes);
        }
		
		return toUInt64NoBLEndianConversion(bytes);
	}

	public static int toUInt32NoBLEndianConversion(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.wrap(bytes);
	    buffer.limit(4);
	    return buffer.getInt();
	}

	public static long toUInt64NoBLEndianConversion(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.wrap(bytes);
	    buffer.limit(8); 
	    return buffer.getLong();
	}
	
	public static byte[] getBytes(int val) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
	    buffer.putInt(val);
	    return buffer.array();
	}

	public static byte[] getBytes(long val) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.putLong(val);
	    return buffer.array();
	}


    public static void reverseBytes(byte[] bytes) {
    	int i = 0, j = bytes.length - 1;
    	while (i < j) {
    		byte b = bytes[i];
    		bytes[i] = bytes[j];
    		bytes[j] = b;
    		i++;
    		j--;
    	}
    }
    
    public static String byteArrayToHex(byte[] a) {
	   StringBuilder sb = new StringBuilder(a.length * 2);
	   for(byte b: a)
	      sb.append(String.format("%02x", b & 0xff));
	   return sb.toString();
	}
    

	public static byte[] getUnicodeBytes(String s) {
		byte[] bytes = s.getBytes(ENCODING);
		//bytes = Arrays.copyOfRange(bytes, 2, bytes.length);
		//reverseBytes(bytes);	//reverse from big-endian to little-endian
		return bytes;
	}
	
	public static char getUnicodeChar(byte[] bytes, int offset) {
		return new String(bytes, offset, 2, ENCODING).charAt(0);
	}
}
