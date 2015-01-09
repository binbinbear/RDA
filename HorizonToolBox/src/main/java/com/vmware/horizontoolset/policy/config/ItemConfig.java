package com.vmware.horizontoolset.policy.config;

//配置文件中，item的数据模型
public class ItemConfig {
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
	public String data;
	
	// add by lixiaoyi
	public String itemType;  // checkbox， inputbox， selectbox 不同类型，disabled时候是不同形式（不考虑disabled的话，则不需要这项）
	
/*	public ItemEntry(String keyName, String valueName) {
		bytes = new byte[0];
		type = PolEntryType.REG_NONE;
		this.keyName = keyName;
		this.valueName = valueName;
	}*/
	
	public ItemConfig(String keyName, String valueName, PolEntryType type) {
		bytes = new byte[0];
		this.type = type;
		this.keyName = keyName;
		this.valueName = valueName;
	}

}
