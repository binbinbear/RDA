package com.vmware.eucenablement.util;

public class TimeInterval {
	public static final long ONE_SECOND = 1000L;
	public static final long ONE_MINUTE = 60000L;
	public static final long ONE_HOUR = 3600000L;
	public static final long ONE_DAY = 86400000L;
	long interval;
	long minPrecision = 1000L;
	int slotsToDisplay = 2;

	public TimeInterval(long start, long end) {
		interval = (end - start);
	}

	public TimeInterval(long start) {
		interval = (System.currentTimeMillis() - start);
	}

	public void setPrecision(int slotsToDisplay, long minPrecision) {
		this.minPrecision = minPrecision;
		this.slotsToDisplay = slotsToDisplay;
	}

	public String getDescription() {
		if (interval < 0)
			return "(unknown)";
		if (interval < 1000)
			return "Just now";

		long n = interval / 86400000;
		if (n > 0) {
			if (n == 1)
				return "1 day ago";
			return "" + n + " days ago";
		}

		n = interval / 3600000;
		if (n > 0) {
			if (n == 1)
				return "1 hour ago";
			return "" + n + " hours ago";
		}
		
		n = interval / 60000;
		if (n > 0) {
			if (n == 1)
				return "1 minute ago";
			return "" + n + " minutes ago";
		}
		
		return "Just now";
	}

	public String toString() {
		if (interval < 0L) {
			return "(unknown)";
		}
		if (interval < 1000L) {
			return "Just now";
		}
		String s = "";
		long tmp = interval;

		int displayed = 0;

		long n = tmp / 86400000L;
		if (n > 0L) {
			s = s + n + "d ";
			displayed++;
			if (displayed >= slotsToDisplay) {
				return s;
			}
		}
		if (minPrecision == 86400000L) {
			return s;
		}
		tmp %= 86400000L;
		n = tmp / 3600000L;
		if ((n > 0L) || (s.length() > 0)) {
			s = s + n + "h ";
			displayed++;
			if (displayed >= slotsToDisplay) {
				return s;
			}
		}
		if (minPrecision == 3600000L) {
			return s;
		}
		tmp %= 3600000L;
		n = tmp / 60000L;
		if ((n > 0L) || (s.length() > 0)) {
			s = s + n + "m ";
			displayed++;
			if (displayed >= slotsToDisplay) {
				return s;
			}
		}
		if (minPrecision == 60000L) {
			return s;
		}
		tmp %= 60000L;
		n = tmp / 1000L;
		if ((n > 0L) || (s.length() > 0)) {
			s = s + n + "s ";
			displayed++;
			if (displayed >= slotsToDisplay) {
				return s;
			}
		}
		if (minPrecision == 1000L) {
			return s;
		}
		tmp %= 1000L;
		n = tmp;
		if ((n > 0L) || (s.length() > 0)) {
			s = s + n + "mi";
			displayed++;
			if (displayed >= slotsToDisplay) {
				return s;
			}
		}
		return s;
	}
}