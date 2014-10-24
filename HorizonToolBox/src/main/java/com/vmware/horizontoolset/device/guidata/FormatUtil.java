package com.vmware.horizontoolset.device.guidata;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

	static String formatTime(long t) {
		if (t == -1)
			return "";
		SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		return fmt.format(new Date(t));
	}
	
	public static void main(String[] args) {
		String s = formatTime(System.currentTimeMillis());
		System.out.println(s);
	}
}
