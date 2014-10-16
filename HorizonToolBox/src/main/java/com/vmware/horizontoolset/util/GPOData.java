package com.vmware.horizontoolset.util;

import java.lang.reflect.Field;
import java.util.HashMap;

public class GPOData {
	
	public static String getData(String key) {
		for(Field f : GPOData.class.getDeclaredFields()) {
			if (f.getName().equalsIgnoreCase(key)) {
				try {
					return (String)f.get(null);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}
	
	public static final String[] GPO_LIST = {"clipboard",
		"block_all_devices",
		"Build_to_lossless",
		"audio",
		"noise_offset_filter"};
	
	public static final String CLIPBOARD_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String CLIPBOARD_VALUENAME = "pcoip.server_clipboard_state";

    public static final String BLOCK_ALL_DEVICES_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String BLOCK_ALL_DEVICES_VALUENAME = "pcoip.server_clipboard_state";

    public static final String BUILD_TO_LOSSLESS_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String BUILD_TO_LOSSLESS_VALUENAME = "pcoip.server_clipboard_state";

    public static final String AUDIO_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String AUDIO_VALUENAME = "pcoip.server_clipboard_state";

    public static final String NOISE_OFFSET_FILTER_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String NOISE_OFFSET_FILTER_VALUENAME = "pcoip.server_clipboard_state";
    
    public static final String MAX_LINK_RATE_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String MAX_LINK_RATE_VALUENAME = "pcoip.max_link_rate";
    
    
    public static final String MTU_SIZE_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String MTU_SIZE_VALUENAME = "pcoip.mtu_size";
    
    public static final String TCPPORT_KEYNAME = "\"HKLM\\Software\\policies\\teradici\\pcoip\\pcoip_admin_defaults\"";
    public static final String TCPPORT_VALUENAME = "pcoip.tcpport";
    /***
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.max_link_rate
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.mtu_size, value=1000
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.tcpport, value=5000
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.udpport;pcoip.udpport_range, value=5000;10
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.max_link_rate, value=1024
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.max_link_rate, value=2048
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.mtu_size, value=1500
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.enable_fips_mode, value=0
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.enable_aes_128_gcm;pcoip.enable_aes_256_gcm;pcoip.enable_salsa20_256_round12, value=0;1;1
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.CAD_insert_substitution_scan_code, value=327
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.enable_vchan, value=0
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.max_link_rate, value=3000
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.audio_bandwidth_limit, value=300
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.enable_fips_mode, value=0
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuenamemin=pcoip.minimum_image_quality, valuenamemax=pcoip.maximum_initial_image_quality, valuenamerate=pcoip.maximum_frame_rate
     * \"HKLM\Software\policies\teradici\pcoip\pcoip_admin_defaults\", valuename=pcoip.server_clipboard_state, value=1
     */
    public static void main(String[] args){
    	System.out.print(GPOData.getData("clipboard_KEYNAME"));
    }
}
