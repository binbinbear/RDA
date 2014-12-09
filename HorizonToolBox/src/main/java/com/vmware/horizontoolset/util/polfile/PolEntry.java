package com.vmware.horizontoolset.util.polfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolEntry implements Comparable<PolEntry> {

	public static enum PolEntryType {
		REG_NONE(0), 
		REG_SZ(1), 
		REG_EXPAND_SZ(2), 
		REG_BINARY(3), 
		REG_DWORD(4), 
		REG_DWORD_BIG_ENDIAN(5), 
		REG_MULTI_SZ(7), 
		REG_QWORD(11);

		public final int id;

		private PolEntryType(int id) {
			this.id = id;
		}
		
		public static PolEntryType fromId(int id) {
			for (PolEntryType t : PolEntryType.values()) {
				if (t.id == id)
					return t;
			}
			return null;
		}
	}

	byte[] bytes;

	public PolEntryType type;
	public String keyName;
	public String valueName;

	public int getDWORD() {

		switch (this.type) {
		case REG_NONE:
		case REG_SZ:
		case REG_MULTI_SZ:
		case REG_EXPAND_SZ:
			String val = getString();
			try {
				return Integer.parseInt(val);
			} catch (NumberFormatException e) {
				throw new PolFileException("Invalid DWORD: " + val, e);
			}

		case REG_DWORD:
			if (bytes.length != 4)
				throw new PolFileException();
			return BitUtil.toUInt32(bytes);

		case REG_DWORD_BIG_ENDIAN:
			if (bytes.length != 4)
				throw new PolFileException();
			return BitUtil.toUInt32(bytes);

		case REG_QWORD:
			if (bytes.length != 8)
				throw new PolFileException();
			long lvalue = BitUtil.toUInt64(bytes);

			if (lvalue > Integer.MAX_VALUE || lvalue < Integer.MIN_VALUE)
				throw new PolFileException("QWORD value '" + lvalue
						+ "' cannot fit into an UInt32 value.");

			return (int) lvalue;

		case REG_BINARY:
			if (bytes.length != 4)
				throw new PolFileException();
			return BitUtil.toUInt32NoBLEndianConversion(bytes);
		default:
			throw new PolFileException(
					"Reached default cast that should be unreachable in PolEntry.UIntValue");
		}
	}

	public void setDWORD(int value) {
		this.type = PolEntryType.REG_DWORD;
		bytes = BitUtil.getBytes(value);
		if (BitUtil.isLittleEndian() == false) {
			BitUtil.reverseBytes(bytes);
		}
	}

	public long getQWORD() {

		switch (this.type) {
		case REG_NONE:
		case REG_SZ:
		case REG_MULTI_SZ:
		case REG_EXPAND_SZ:
			try {
				return Long.parseLong(this.getString());
			} catch (NumberFormatException e) {
				throw new PolFileException(e);
			}
		case REG_DWORD:
			if (bytes.length != 4)
				throw new PolFileException();
			return (long) BitUtil.toUInt32(bytes);
		case REG_DWORD_BIG_ENDIAN:
			if (bytes.length != 4)
				throw new PolFileException();
			return (long) BitUtil.toUInt32(bytes);
		case REG_QWORD:
			if (bytes.length != 8)
				throw new PolFileException();
			return BitUtil.toUInt64(bytes);
		case REG_BINARY:
			if (bytes.length != 8)
				throw new PolFileException();
			return BitUtil.toUInt64NoBLEndianConversion(bytes);
		default:
			throw new PolFileException(
					"Reached default cast that should be unreachable in PolEntry.ULongValue");
		}
	}

	public void setQWORD(long val) {
		this.type = PolEntryType.REG_QWORD;

		bytes = BitUtil.getBytes(val);
		if (BitUtil.isLittleEndian() == false)
			BitUtil.reverseBytes(bytes);
	}

	public String getString() {

		StringBuilder sb = new StringBuilder(bytes.length * 2);

		switch (this.type) {
		case REG_NONE:
			return "";
		case REG_MULTI_SZ:
			String[] mstring = getMultiString();
			for (int i = 0; i < mstring.length; i++) {
				if (i > 0)
					sb.append("\\0");
				sb.append(mstring[i]);
			}

			return sb.toString();

		case REG_DWORD:
		case REG_DWORD_BIG_ENDIAN:
		case REG_QWORD:
			return String.valueOf(getQWORD());

		case REG_BINARY:
			return BitUtil.byteArrayToHex(bytes);

		case REG_SZ:
		case REG_EXPAND_SZ:
			String s = new String(bytes, BitUtil.ENCODING);
			if (s.endsWith("\0"))
				s = s.substring(0, s.length() - "\0".length());
			return s;
		default:
			throw new PolFileException(
					"Reached default cast that should be unreachable in PolEntry.StringValue");
		}
	}

	public void setString(String val) {

		this.type = PolEntryType.REG_SZ;

		bytes = BitUtil.getUnicodeBytes(val + "\0");
	}

	public String[] getMultiString() {

		switch (this.type) {
		case REG_NONE:
			throw new PolFileException(
					"getMultiString cannot be used on the REG_NONE type.");
		case REG_DWORD:
		case REG_DWORD_BIG_ENDIAN:
		case REG_QWORD:
		case REG_BINARY:
		case REG_SZ:
		case REG_EXPAND_SZ:
			return new String[] { this.getString() };
		case REG_MULTI_SZ:
			List<String> list = new ArrayList<>();

			StringBuilder sb = new StringBuilder(256);

			for (int i = 0; i < (bytes.length - 1); i += 2) {
				char curChar = BitUtil.getUnicodeChar(bytes, i);
				if (curChar == '\0') {
					if (sb.length() == 0)
						break;
					list.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(curChar);
				}
			}

			return list.toArray(new String[list.size()]);
		default:
			throw new PolFileException(
					"Reached default cast that should be unreachable in PolEntry.MultiStringValue");
		}
	}

	public void setMultiString(String[] val) {
		this.type = PolEntryType.REG_MULTI_SZ;

		try (ByteArrayOutputStream buf = new ByteArrayOutputStream();) {

			for (int i = 0; i < val.length; i++) {
				if (i > 0)
					buf.write(BitUtil.getUnicodeBytes("\0"));

				if (val[i] != null)
					buf.write(BitUtil.getUnicodeBytes(val[i]));
			}

			buf.write(BitUtil.getUnicodeBytes("\0\0"));

			bytes = buf.toByteArray();
		} catch (IOException e) {
		}
	}

	public byte[] getBinary() {
		return bytes;
	}

	public void setBinary(byte[] val) {
		this.type = PolEntryType.REG_BINARY;
		bytes = val;
	}

	public void setDWORDBigEndian(int val) {
		this.type = PolEntryType.REG_DWORD_BIG_ENDIAN;
		bytes = BitUtil.getBytes(val);
		if (BitUtil.isLittleEndian() == true)
			BitUtil.reverseBytes(bytes);
	}

	public void setExpandString(String val) {
		this.setString(val);
		this.type = PolEntryType.REG_EXPAND_SZ;
	}

	public PolEntry(String keyName, String valueName) {
		bytes = new byte[0];
		type = PolEntryType.REG_NONE;
		this.keyName = keyName;
		this.valueName = valueName;
	}

	@Override
	public int compareTo(PolEntry other) {
		int result = keyName.compareToIgnoreCase(other.keyName);

		if (result != 0)
			return result;

		boolean firstSpecial, secondSpecial;

		firstSpecial = this.valueName.startsWith("**");
		secondSpecial = other.valueName.startsWith("**");

		if (firstSpecial == true && secondSpecial == false) {
			return -1;
		}
		if (secondSpecial == true && firstSpecial == false) {
			return 1;
		}

		return valueName.compareToIgnoreCase(other.valueName);
	}
}