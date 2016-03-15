package com.vmware.horizontoolset.devicefilter;

import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.util.StringUtil;



public class DeviceFilterItem {

	public DeviceFilterItem(DeviceFilterEnum type, String reg){
		this.type = type;
		this.reg = reg;
	}
	private DeviceFilterEnum type;
	public DeviceFilterEnum getType() {
		return type;
	}
	public void setType(DeviceFilterEnum type) {
		this.type = type;
	}
	private String reg;
	public String getReg() {
		return reg;
	}
	public void setReg(String reg) {
		this.reg = reg;
	}

	private static Logger log = Logger.getLogger(DeviceFilterItem.class);
	//true for matched, false for mis-matched
	public boolean checkMatched(Map<String, String> env){

		String key = this.type.toString();
		String value = env.get(key);
		log.info("key:"+ key + " value in environment:"+ value+ " reg in filter:"+ reg);
		if (this.type.equals(DeviceFilterEnum.MAC_Address)){
			if (!StringUtil.isEmpty(value) && !StringUtil.isEmpty(reg)){
				value = value.toLowerCase().replaceAll(":", "-");
				reg = reg.toLowerCase().replaceAll(":", "-");
				if (value.matches(reg)){
					log.info("RULE mached for MAC address");
					return true;
				}
				//change : to -

			}
		}else if (this.type.equals(DeviceFilterEnum.IP_Address)){
			if (reg.contains("/")){
				//regard this as a CIDR network
				return checkIPinCIDR(value, reg);

			}else{
				if (!StringUtil.isEmpty(value) && value.matches(reg)){
					log.info("rule matched");
					return true;
				}

			}

		}
		else{
			if (!StringUtil.isEmpty(value) && value.matches(reg)){
				log.info("rule matched");
				return true;
			}
		}

		log.info("rule not matched");
		return false;

	}

	private static boolean checkIPinCIDR(String ip, String cidr){

		String[] parts = cidr.split("/");
		String ipfirst = parts[0];
		String masksecond = parts[1];

		// Step 1. Convert IPs into ints (32 bits).
		// E.g. 157.166.224.26 becomes 10011101  10100110  11100000 00011010
		int addr = ipToLong(ipfirst);

		// Step 2. Get CIDR mask
		int mask = (-1) << (32 - Integer.valueOf(masksecond));

		// Step 3. Find lowest IP address
		int lowest = addr & mask;

		// Step 4. Find highest IP address
		int highest = lowest + (~mask);


		int ipInt = ipToLong(ip);
		return ipInt>=lowest && ipInt<=highest;

	}

	private static int ipToLong(String strIP) {
        int[] ip = new int[4];
        String[] ipSec = strIP.split("\\.");
        for (int k = 0; k < 4; k++) {
            ip[k] = Integer.valueOf(ipSec[k]);
        }

        return (( ip[0] << 24 ) & 0xFF000000)
		           | (( ip[1] << 16 ) & 0xFF0000)
		           | (( ip[2] << 8 ) & 0xFF00)
		           |  ( ip[3] & 0xFF);
    }

	  private static String longToIP(int longIP) {
	        StringBuffer sb = new StringBuffer("");
	        sb.append(String.valueOf(longIP >>> 24));
	        sb.append(".");
	        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
	        sb.append(".");
	        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
	        sb.append(".");
	        sb.append(String.valueOf(longIP & 0x000000FF));

	        return sb.toString();
	    }


}
