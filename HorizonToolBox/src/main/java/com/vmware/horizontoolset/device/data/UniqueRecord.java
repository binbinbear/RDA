package com.vmware.horizontoolset.device.data;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueRecord {

	public final long recordId;

	private static AtomicLong _counter = new AtomicLong();
	
	UniqueRecord() {
		recordId = _counter.incrementAndGet();
	}
	
	@Override
	public int hashCode() {
		return (int) recordId * 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WhitelistRecord other = (WhitelistRecord) obj;
		if (recordId != other.recordId)
			return false;
		return true;
	}
	
}
