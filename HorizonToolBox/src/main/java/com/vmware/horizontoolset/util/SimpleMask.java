package com.vmware.horizontoolset.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class SimpleMask {

	public static String mask(String plaintext) {

		byte[] buf;
		try {
			buf = plaintext.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		Random rand = new Random();
		byte r = (byte) (rand.nextInt(200) + 50);

		byte[] encoded = new byte[buf.length + 1];
		encoded[0] = (byte) r;
		for (int i = 0; i < buf.length; i++) {
			byte n = (byte) (buf[i] ^ (byte) r);
			encoded[i + 1] = n;
		}
		reverse(encoded);
		return "#" + new String(Base64.encodeBase64(encoded));
	}

	public static String unmask(String encoded) {
		if (encoded == null)
			return null;
		if (encoded.isEmpty())
			return "";

		if (!encoded.startsWith("#"))
			return encoded;

		encoded = encoded.substring(1);
		byte[] bytes = Base64.decodeBase64(encoded.getBytes());
		reverse(bytes);
		byte[] decoded = new byte[bytes.length - 1];
		byte r = bytes[0];
		for (int i = 0; i < decoded.length; i++) {
			decoded[i] = (byte) (bytes[i + 1] ^ r);
		}
		try {
			return new String(decoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private static void reverse(byte[] buf) {

		for (int i = 0; i < buf.length / 2; i++) {
			byte t = buf[i];
			buf[i] = buf[buf.length - i - 1];
			buf[buf.length - i - 1] = t;
		}
	}

	public static void main(String[] args) {
		String test = "hello你好abc";
		String mask = mask(test);
		System.out.println("Mask: " + mask);
		String decoded = unmask(mask);
		System.out.println("Decoded: " + decoded);
	}
}