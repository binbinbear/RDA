package com.vmware.horizontoolset.policy.polfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.horizontoolset.policy.polfile.PolEntry.PolEntryType;

/**
 * 
 * Read/write Windows .pol file.
 * 
 * @author nanw
 * 
 */
public class PolFile {
	private enum PolEntryParseState {
		Key, ValueName, Start
	}

	private static final int PolHeader = 0x50526567;
	private static final int PolVersion = 0x01000000;

	private Map<String, PolEntry> entries = new HashMap<>();

	public List<PolEntry> entries() {
		List<PolEntry> pl = new ArrayList<>(entries.values());
		Collections.sort(pl);
		return pl;
	}

	public String fileName = "";

	public PolFile() {
	}

	public PolFile(String fileName) {
		this.fileName = fileName;
	}
	
	public void set(PolEntry pe) {
		String k = (pe.keyName + "\\" + pe.valueName).toUpperCase();
		this.entries.put(k, pe);
	}

	public void setString(String key, String value, String data) {
		this.setString(key, value, data, false);
	}

	public void setString(String key, String value, String data, boolean bExpand) {
		PolEntry pe = new PolEntry(key, value);

		if (bExpand)
			pe.setExpandString(data);
		else
			pe.setString(data);

		this.set(pe);
	}

	public void setDWORD(String key, String value, int data) {
		this.setDWORD(key, value, data, true);
	}

	public void setDWORD(String key, String value, int data,
			boolean bLittleEndian) {
		PolEntry pe = new PolEntry(key, value);

		if (bLittleEndian)
			pe.setDWORD(data);
		else
			pe.setDWORDBigEndian(data);

		this.set(pe);
	}

	public void setQWORD(String key, String value, long data) {
		PolEntry pe = new PolEntry(key, value);

		pe.setQWORD(data);

		this.set(pe);
	}

	public void setMultiString(String key, String value, String[] data) {
		PolEntry pe = new PolEntry(key, value);
		pe.setMultiString(data);

		this.set(pe);
	}

	public void setBinary(String key, String value, byte[] data) {
		PolEntry pe = new PolEntry(key, value);
		pe.setBinary(data);

		this.set(pe);
	}

	public PolEntry getValue(String key, String value) {
		String k = (key + "\\" + value).toUpperCase();
		return entries.get(k);
	}

	public String getString(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.getString();
	}

	public String[] getMultiString(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.getMultiString();
	}

	public int getDWORD(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.getDWORD();
	}

	public long getQWORD(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.getQWORD();
	}

	public byte[] getBinary(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.getBinary();
	}

	public boolean contains(String key, String value) {
		return this.getValue(key, value) != null;
	}

	public boolean contains(String key, String value, PolEntryType type) {
		PolEntry pe = this.getValue(key, value);
		return pe != null && pe.type == type;
	}

	public PolEntryType getValueType(String key, String value) {
		PolEntry pe = this.getValue(key, value);
		return pe == null ? null : pe.type;
	}

	public PolEntry deleteValue(String key, String value) {
		String k = (key + "\\" + value).toUpperCase();
		return entries.remove(k);
	}

	public void load(String fileName) {
		this.fileName = fileName;
		load();
	}
	
	public void load() {
		File file = new File(fileName);
		byte[] bytes = new byte[(int) file.length()];
		int len = 0;
		try (FileInputStream fs = new FileInputStream(file)) {
			// Read the source file into a byte array.
			int bytesToRead = bytes.length;

			while (bytesToRead > 0) {
				int n = fs.read(bytes, len, bytesToRead);
				if (n == 0)
					break;

				len += n;
				bytesToRead -= n;
			}
		} catch (IOException e) {
			throw new PolFileException(e);
		}

		load(bytes);
	}
	
	public void load(byte[] bytes) {
		
		int len = bytes.length;
		// registry.pol files are an 8-byte fixed header followed by some number
		// of entries in the following format:
		// [KeyName;ValueName;<type>;<size>;<data>]
		// The brackets, semicolons, KeyName and ValueName are little-endian
		// Unicode text.
		// type and size are 4-byte little-endian unsigned integers. Size cannot
		// be greater than 0xFFFF, even though it's
		// stored as a 32-bit number. type will be one of the values REG_SZ, etc
		// as defined in the Win32 API.
		// Data will be the number of bytes indicated by size. The next 2 bytes
		// afterward must be unicode "]".
		//
		// All Strings (KeyName, ValueName, and data when type is REG_SZ or
		// REG_EXPAND_SZ) are terminated by a single
		// null character.
		//
		// Multi Strings are Strings separated by a single null character, with
		// the whole list terminated by a double null.

		if (len < 8)
			throw new PolFileException("len < 8");

		int header = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8)
				| bytes[3];
		int version = (bytes[4] << 24) | (bytes[5] << 16) | (bytes[6] << 8)
				| bytes[7];

		if (header != PolFile.PolHeader || version != PolFile.PolVersion)
			throw new PolFileException("Incorrect Pol header or version.");

		PolEntryParseState parseState = PolEntryParseState.Start;
		int i = 8;

		StringBuilder keyName = new StringBuilder(50);
		StringBuilder valueName = new StringBuilder(50);

		while (i < (len - 1)) {
			char curChar = BitUtil.getUnicodeChar(bytes, i);

			switch (parseState) {
			case Start:
				if (curChar != '[')
					throw new PolFileException("Expect '['. Actual:" + curChar
							+ ", i=" + i);
				i += 2;
				parseState = PolEntryParseState.Key;
				continue;
			case Key:
				if (curChar == '\0') {
					if (i > (len - 4))
						throw new PolFileException("i>len-4. i=" + i + ", len="
								+ len);

					curChar = BitUtil.getUnicodeChar(bytes, i + 2);

					if (curChar != ';')
						throw new PolFileException("Expect ';'. Actual:"
								+ curChar + ", i=" + i);

					// We've reached the end of the key name. Switch to parsing
					// value name.

					i += 4;
					parseState = PolEntryParseState.ValueName;
				} else {
					keyName.append(curChar);
					i += 2;
				}
				continue;
			case ValueName:
				if (curChar == '\0') {
					if (i > (len - 16))
						throw new PolFileException();

					curChar = BitUtil.getUnicodeChar(bytes, i + 2);
					if (curChar != ';')
						throw new PolFileException("Expect ';'. Actual:"
								+ curChar + ", i=" + i);

					// We've reached the end of the value name. Now read in the
					// type and size fields, and the data bytes
//					int typeId = (int) (bytes[i + 7] << 24 | bytes[i + 6] << 16
//							| bytes[i + 5] << 8 | bytes[i + 4]);
					int typeId = readUnsignedInt(bytes, i + 4);
					PolEntryType type = PolEntryType.fromId(typeId);
					if (type == null)
						throw new PolFileException("Incorrect PolEntryType: "
								+ type);

					curChar = BitUtil.getUnicodeChar(bytes, i + 8);
					if (curChar != ';')
						throw new PolFileException("Expect ';'. Actual:"
								+ curChar + ", i=" + i);

//					int size = bytes[i + 13] << 24 | bytes[i + 12] << 16
//							| bytes[i + 11] << 8 | bytes[i + 10];
					int size = readUnsignedInt(bytes, i + 10);
					if ((size > 0xFFFF) || (size < 0))
						throw new PolFileException("Incorrect size: " + size
								+ ", i=" + i);

					curChar = BitUtil.getUnicodeChar(bytes, i + 14);
					if (curChar != ';')
						throw new PolFileException("Expect ';'. Actual:"
								+ curChar + ", i=" + i);

					i += 16;

					if (i > (len - (size + 2)))
						throw new PolFileException("i>(len-(size+2)). i=" + i
								+ ", len=" + len + ", size=" + size);

					curChar = BitUtil.getUnicodeChar(bytes, i + size);
					if (curChar != ']')
						throw new PolFileException("Expect ']'. Actual:"
								+ curChar + ", i=" + i);

					PolEntry pe = new PolEntry(keyName.toString(),
							valueName.toString());
					pe.type = type;

					for (int j = 0; j < size; j++)
						pe.bytes = Arrays.copyOfRange(bytes, i, i + size);

					this.set(pe);

					i += size + 2;

					keyName.setLength(0);
					valueName.setLength(0);
					parseState = PolEntryParseState.Start;
				} else {
					valueName.append(curChar);
					i += 2;
				}
				continue;
			default:
				throw new PolFileException("Unreachable code");
			}
		}
	}

	private static int readUnsignedInt(byte[] bytes, int i) {
		int c1 = bytes[i + 3];
		if (c1 < 0)
			c1 += 256;
		int c2 = bytes[i + 2];
		if (c2 < 0)
			c2 += 256;
		int c3 = bytes[i + 1];
		if (c3 < 0)
			c3 += 256;
		int c4 = bytes[i];
		if (c4 < 0)
			c4 += 256;
		
		int n = c1 << 24 | c2 << 16
				| c3 << 8 | c4;
		return n;
	}

	public void save() {

		try (FileOutputStream fs = new FileOutputStream(fileName)) {
			save(fs);
		} catch (IOException e) {
			throw new PolFileException(e);
		}
	}

	public void save(String file) {
		this.fileName = file;
		save();
	}
	
	public byte[] saveToBuffer() {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			save(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new PolFileException(e);
		}
	}
	
	public void save(OutputStream out) throws IOException {
		// Because we maintain the byte array for each PolEntry in memory,
		// writing back to the file
		// is a simple operation, creating entries of the format:
		// [KeyName;ValueName;type;size;data] after the fixed 8-byte header.
		// The only things we must do are add null terminators to KeyName and
		// ValueName, which are
		// represented by C# Strings in memory, and make sure Size and Type are
		// written in little-endian
		// byte order.

		out.write(new byte[] { 0x50, 0x52, 0x65, 0x67, 0x01, 0x00, 0x00,
				0x00 }, 0, 8);
		byte[] openBracket = BitUtil.getUnicodeBytes("[");
		byte[] closeBracket = BitUtil.getUnicodeBytes("]");
		byte[] semicolon = BitUtil.getUnicodeBytes(";");
		byte[] nullChar = new byte[] { 0, 0 };

		byte[] bytes;

		for (PolEntry pe : this.entries.values()) {
			out.write(openBracket); // 2
			bytes = BitUtil.getUnicodeBytes(pe.keyName);
			out.write(bytes);
			out.write(nullChar); // 2

			out.write(semicolon); // 2
			bytes = BitUtil.getUnicodeBytes(pe.valueName);
			out.write(bytes);
			out.write(nullChar); // 2

			out.write(semicolon); // 2
			bytes = BitUtil.getBytes(pe.type.id);
			if (BitUtil.isLittleEndian() == false)
				BitUtil.reverseBytes(bytes);
			out.write(bytes); // 4

			out.write(semicolon); // 2
			bytes = BitUtil.getBytes(pe.bytes.length);
			if (BitUtil.isLittleEndian() == false)
				BitUtil.reverseBytes(bytes);
			out.write(bytes); // 4

			out.write(semicolon); // 2
			out.write(pe.bytes);
			out.write(closeBracket); // 2
		}
	}

	public void _dump() {
		for (PolEntry pe : entries()) {
			System.out.println("Key=" + pe.keyName);
			System.out.println("Value=" + pe.valueName);
			System.out.println("Type=" + pe.type);

            switch (pe.type)
            {
                case REG_NONE:
                case REG_SZ:
                case REG_MULTI_SZ:
                case REG_EXPAND_SZ:
                	System.out.println("Data=" + pe.getString());
                    break;
                case REG_DWORD:
                	System.out.println("Data=" + pe.getDWORD());
                    break;
                case REG_DWORD_BIG_ENDIAN:
                	System.out.println("Data=" + "(DWORD_BIG_ENDIAN) " + pe.getDWORD());
                    break;
                case REG_QWORD:
                	System.out.println("Data=" + pe.getQWORD());
                    break;
                case REG_BINARY:
                	System.out.print("Data=");
                    byte[] bytes = pe.getBinary();
                    for (int i = 0; i < bytes.length; i++)
                    {
                    	System.out.print(bytes[i]);
                    	System.out.print(' ');
                    }
                    System.out.println();
                    break;
                default:
                	System.out.println("Unknown type: " + pe.type);
            }
            System.out.println("-----");
		}
	}
	
	public static void main(String[] args) {
		byte[] bytes = {(byte)0xD0, 0x03, 0x00, 0x00};
		
		int n = readUnsignedInt(bytes, 0);
		
		System.out.println(n);
	}
}
