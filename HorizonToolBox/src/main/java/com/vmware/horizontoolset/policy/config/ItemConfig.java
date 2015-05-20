package com.vmware.horizontoolset.policy.config;

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
	
	public static enum ElementType{
		ELE_CHECKBOX,	 //TODO default value 统一写入配置文件
		ELE_CHECKBOX_V,  
		ELE_INPUTBOX,
		ELE_SELECTBOX,
		ELE_GRID1,
		ELE_GRID2,
		ELE_ADDITION,	  //case disabled， value==0
		ELE_ADDITION_0,   //case enabled，   value==defaultData
		ELE_GRID_TITLE
	}

	//byte[] bytes;
	
	public PolEntryType type;
	public String keyName;
	public String valueName;
	public String data;			// edit by user
	public String defaultData;
	
	public ElementType elementType;  // checkbox， inputbox， selectbox 不同类型，disabled时候是不同形式
	
	public ItemConfig(String keyName, String valueName, PolEntryType type, ElementType elementType) {
		this.type = type;
		this.keyName = keyName;
		this.valueName = valueName;
		this.elementType = elementType;
	}
	
	public ItemConfig(String keyName, String valueName, PolEntryType type, ElementType elementType, String defaultData) {
		this.type = type;
		this.keyName = keyName;
		this.valueName = valueName;
		this.elementType = elementType;
		this.defaultData = defaultData;
	}
	
	@Override
	public String toString(){
		return "keyName=" + keyName + ", valueName="+valueName + ", type=" + type.toString() + ", elementType=" + elementType;
	}

}
