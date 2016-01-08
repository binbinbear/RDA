package com.vmware.horizon.auditing.db;

public class StorageItem {
	private String value;

	private String[] keys;

	public StorageItem(String[] keys, String value){
		this.keys= keys;
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String[] getKeys() {
		return keys;
	}
}
