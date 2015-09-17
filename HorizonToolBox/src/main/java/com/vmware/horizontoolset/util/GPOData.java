package com.vmware.horizontoolset.util;

import java.lang.reflect.Field;

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
    
    public static final String DEVICE_KEYNAME = "";
    public static final String DEVICE_VALUENAME = "";
    
    public static final String POOL_KEYNAME = "";
    public static final String POOL_VALUENAME = "";


    public static final String PRODUCTIONLOGS_KEYNAME = "";
    public static final String PRODUCTIONLOGS_VALUENAME = "";


    public static final String DEBUGLOGS_KEYNAME = "";
    public static final String DEBUGLOGS_VALUENAME = "";


    public static final String LOGSIZE_KEYNAME = "";
    public static final String LOGSIZE_VALUENAME = "";


    public static final String LOGDIRECTORY_KEYNAME = "";
    public static final String LOGDIRECTORY_VALUENAME = "";


    public static final String SENDLOGS_KEYNAME = "";
    public static final String SENDLOGS_VALUENAME = "";


    public static final String INTERVAL_KEYNAME = "";
    public static final String INTERVAL_VALUENAME = "";


    public static final String OVERALLCPU_KEYNAME = "";
    public static final String OVERALLCPU_VALUENAME = "";


    public static final String OVERALLMEMORY_KEYNAME = "";
    public static final String OVERALLMEMORY_VALUENAME = "";


    public static final String PROCESSCPU_KEYNAME = "";
    public static final String PROCESSCPU_VALUENAME = "";


    public static final String PROCESSMEMORY_KEYNAME = "";
    public static final String PROCESSMEMORY_VALUENAME = "";


    public static final String PROCESSCHECK_KEYNAME = "";
    public static final String PROCESSCHECK_VALUENAME = "";


    public static final String CERTIFICATEREVOCATION_KEYNAME = "";
    public static final String CERTIFICATEREVOCATION_VALUENAME = "";


    public static final String CACHEDREVOCATION_KEYNAME = "";
    public static final String CACHEDREVOCATION_VALUENAME = "";


    public static final String CHECKTIMEOUT_KEYNAME = "";
    public static final String CHECKTIMEOUT_VALUENAME = "";


    public static final String LOSSLESS_KEYNAME = "";
    public static final String LOSSLESS_VALUENAME = "";


    public static final String MAXIMUM_KEYNAME = "";
    public static final String MAXIMUM_VALUENAME = "";


    public static final String MTU_KEYNAME = "";
    public static final String MTU_VALUENAME = "";


    public static final String FLOOR_KEYNAME = "";
    public static final String FLOOR_VALUENAME = "";


    public static final String ENDISAUDIO_KEYNAME = "";
    public static final String ENDISAUDIO_VALUENAME = "";


    public static final String LIMIT_KEYNAME = "";
    public static final String LIMIT_VALUENAME = "";


    public static final String SSL_KEYNAME = "";
    public static final String SSL_VALUENAME = "";


    public static final String ENCRYPTION_KEYNAME = "";
    public static final String ENCRYPTION_VALUENAME = "";


    public static final String USB_KEYNAME = "";
    public static final String USB_VALUENAME = "";


    public static final String TCP_KEYNAME = "";
    public static final String TCP_VALUENAME = "";


    public static final String UDP_KEYNAME = "";
    public static final String UDP_VALUENAME = "";


    public static final String CHANNELS_KEYNAME = "";
    public static final String CHANNELS_VALUENAME = "";


    public static final String IMAGE_KEYNAME = "";
    public static final String IMAGE_VALUENAME = "";


    public static final String FIPS_KEYNAME = "";
    public static final String FIPS_VALUENAME = "";


    public static final String VSPHERE_KEYNAME = "";
    public static final String VSPHERE_VALUENAME = "";


    public static final String SYNCHRONIZATION_KEYNAME = "";
    public static final String SYNCHRONIZATION_VALUENAME = "";


    public static final String ALTERNATE_KEYNAME = "";
    public static final String ALTERNATE_VALUENAME = "";


    public static final String CAD_KEYNAME = "";
    public static final String CAD_VALUENAME = "";


    public static final String TRANSPORT_KEYNAME = "";
    public static final String TRANSPORT_VALUENAME = "";


    public static final String VERBOSITY_KEYNAME = "";
    public static final String VERBOSITY_VALUENAME = "";


    public static final String TIMEINDAYS_KEYNAME = "";
    public static final String TIMEINDAYS_VALUENAME = "";


    public static final String SIZEINMB_KEYNAME = "";
    public static final String SIZEINMB_VALUENAME = "";


    public static final String EXCLUDE_KEYNAME = "";
    public static final String EXCLUDE_VALUENAME = "";


    public static final String SPLITDEVICE_KEYNAME = "";
    public static final String SPLITDEVICE_VALUENAME = "";


    public static final String OTHER_KEYNAME = "";
    public static final String OTHER_VALUENAME = "";


    public static final String HID_KEYNAME = "";
    public static final String HID_VALUENAME = "";


    public static final String INPUTDEVICES_KEYNAME = "";
    public static final String INPUTDEVICES_VALUENAME = "";


    public static final String OUTPUTDEVICES_KEYNAME = "";
    public static final String OUTPUTDEVICES_VALUENAME = "";


    public static final String KEYBOARD_KEYNAME = "";
    public static final String KEYBOARD_VALUENAME = "";


    public static final String VIDEODEVICES_KEYNAME = "";
    public static final String VIDEODEVICES_VALUENAME = "";


    public static final String SMARTCARDS_KEYNAME = "";
    public static final String SMARTCARDS_VALUENAME = "";


    public static final String AUTODEVICE_KEYNAME = "";
    public static final String AUTODEVICE_VALUENAME = "";


    public static final String EXCLUDEVP_KEYNAME = "";
    public static final String EXCLUDEVP_VALUENAME = "";


    public static final String INCLUDEVP_KEYNAME = "";
    public static final String INCLUDEVP_VALUENAME = "";


    public static final String EXCLUDEDF_KEYNAME = "";
    public static final String EXCLUDEDF_VALUENAME = "";


    public static final String INCLUDEDF_KEYNAME = "";
    public static final String INCLUDEDF_VALUENAME = "";


    public static final String EXCLLUDEALL_KEYNAME = "";
    public static final String EXCLLUDEALL_VALUENAME = "";


    public static final String MMR_KEYNAME = "";
    public static final String MMR_VALUENAME = "";


    public static final String MULTIMEDIA_KEYNAME = "";
    public static final String MULTIMEDIA_VALUENAME = "";


    public static final String DIRECTRDP_KEYNAME = "";
    public static final String DIRECTRDP_VALUENAME = "";


    public static final String SINGLESIGNON_KEYNAME = "";
    public static final String SINGLESIGNON_VALUENAME = "";


    public static final String TIMEOUT_KEYNAME = "";
    public static final String TIMEOUT_VALUENAME = "";


    public static final String CREDENTIALFILTER_KEYNAME = "";
    public static final String CREDENTIALFILTER_VALUENAME = "";


    public static final String USINGDNS_KEYNAME = "";
    public static final String USINGDNS_VALUENAME = "";


    public static final String DISABLETZNAME_KEYNAME = "";
    public static final String DISABLETZNAME_VALUENAME = "";


    public static final String TOGGLE_KEYNAME = "";
    public static final String TOGGLE_VALUENAME = "";


    public static final String ONCONNECT_KEYNAME = "";
    public static final String ONCONNECT_VALUENAME = "";


    public static final String ONRECONNECT_KEYNAME = "";
    public static final String ONRECONNECT_VALUENAME = "";


    public static final String ONDISCONNECT_KEYNAME = "";
    public static final String ONDISCONNECT_VALUENAME = "";


    public static final String SHOWICON_KEYNAME = "";
    public static final String SHOWICON_VALUENAME = "";


    public static final String FRAMEWORKCHANNEL_KEYNAME = "";
    public static final String FRAMEWORKCHANNEL_VALUENAME = "";


    public static final String UNITY_KEYNAME = "";
    public static final String UNITY_VALUENAME = "";


    public static final String MAXFRAMES_KEYNAME = "";
    public static final String MAXFRAMES_VALUENAME = "";


    public static final String MAXIMAGEHEIGHT_KEYNAME = "";
    public static final String MAXIMAGEHEIGHT_VALUENAME = "";


    public static final String MAXIMAGEWIDTH_KEYNAME = "";
    public static final String MAXIMAGEWIDTH_VALUENAME = "";


    public static final String DEFAULTIMAGEHEIGHT_KEYNAME = "";
    public static final String DEFAULTIMAGEHEIGHT_VALUENAME = "";


    public static final String DEFAULTIMAGEWIDTH_KEYNAME = "";
    public static final String DEFAULTIMAGEWIDTH_VALUENAME = "";


    public static final String DISABLERTAV_KEYNAME = "";
    public static final String DISABLERTAV_VALUENAME = "";


    public static final String PORTNUMBER_KEYNAME = "";
    public static final String PORTNUMBER_VALUENAME = "";


    public static final String SESSIONTIMEOUT_KEYNAME = "";
    public static final String SESSIONTIMEOUT_VALUENAME = "";


    public static final String DISCLAIMERENABLED_KEYNAME = "";
    public static final String DISCLAIMERENABLED_VALUENAME = "";


    public static final String DISCLAIMERTEXT_KEYNAME = "";
    public static final String DISCLAIMERTEXT_VALUENAME = "";


    public static final String APPLICTIONSENABLED_KEYNAME = "";
    public static final String APPLICTIONSENABLED_VALUENAME = "";


    public static final String AUTOCONNECT_KEYNAME = "";
    public static final String AUTOCONNECT_VALUENAME = "";


    public static final String ALWAYSCONNECT_KEYNAME = "";
    public static final String ALWAYSCONNECT_VALUENAME = "";


    public static final String SCREENSIZE_KEYNAME = "";
    public static final String SCREENSIZE_VALUENAME = "";


    public static final String PCOIPPORT_KEYNAME = "";
    public static final String PCOIPPORT_VALUENAME = "";


    public static final String RDPPORT_KEYNAME = "";
    public static final String RDPPORT_VALUENAME = "";


    public static final String BLASTPORT_KEYNAME = "";
    public static final String BLASTPORT_VALUENAME = "";


    public static final String IPADDRESS_KEYNAME = "";
    public static final String IPADDRESS_VALUENAME = "";


    public static final String CHANNELPORT_KEYNAME = "";
    public static final String CHANNELPORT_VALUENAME = "";


    public static final String USBENABLED_KEYNAME = "";
    public static final String USBENABLED_VALUENAME = "";


    public static final String MMRENABLED_KEYNAME = "";
    public static final String MMRENABLED_VALUENAME = "";


    public static final String RESETENABLED_KEYNAME = "";
    public static final String RESETENABLED_VALUENAME = "";


    public static final String USBAUTOCONNECT_KEYNAME = "";
    public static final String USBAUTOCONNECT_VALUENAME = "";


    public static final String CACHETIMEOUT_KEYNAME = "";
    public static final String CACHETIMEOUT_VALUENAME = "";


    public static final String DISSESSIONTIMEOUT_KEYNAME = "";
    public static final String DISSESSIONTIMEOUT_VALUENAME = "";
    
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
