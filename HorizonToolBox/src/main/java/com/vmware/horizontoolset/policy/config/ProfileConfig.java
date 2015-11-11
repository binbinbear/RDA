package com.vmware.horizontoolset.policy.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.vmware.horizontoolset.policy.config.ItemConfig.ElementType;
import com.vmware.horizontoolset.policy.config.ItemConfig.PolEntryType;
import com.vmware.horizontoolset.policy.config.PolicyConfig.PolicyType;
import com.vmware.horizontoolset.util.JsonUtil;

public class ProfileConfig {
	private Map<String,PolicyConfig> policies;

	public ProfileConfig(Map<String, PolicyConfig> policies) {
		this.policies = policies;
	}
	
	public ProfileConfig(){
		policies = new HashMap<String, PolicyConfig>();
	}
	
	public PolicyConfig getPolicy(String policyId){
		return policies.get(policyId);
	}

	public Map<String,PolicyConfig> getPoliciesMap(){
		return policies;
	}
	
	
	
	@Override
	public String toString(){
		return "policiesMap: "+policies.toString();
	}

	
	// create PolicyConfig.json
	public static void main(String[] args){
		ProfileConfig allPol = new ProfileConfig();
	
		//pConf1.policyId="Configure SSL connections to satisfy Security Tools";
		Map<String,ItemConfig> itemsMap1 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf1_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ask_clients_to_use_vcs_sni", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "0");        // checkbox       
		ItemConfig PCoIP_Conf1_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ca_cert_store_name", PolEntryType.REG_SZ, ElementType.ELE_INPUTBOX, "ROOT");					// ROOT
		ItemConfig PCoIP_Conf1_item3 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.cert_store_name", PolEntryType.REG_SZ, ElementType.ELE_INPUTBOX, "MY");						// name
		ItemConfig PCoIP_Conf1_item4 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_deny_legacy_connections", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "0");	// 最上方，checkBox
		ItemConfig PCoIP_Conf1_item5 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_min_key_length", PolEntryType.REG_DWORD, ElementType.ELE_SELECTBOX, "1024");			// 1024,2048,4096
		ItemConfig PCoIP_Conf1_item6 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_cert_type", PolEntryType.REG_DWORD, ElementType.ELE_SELECTBOX, "1");					    // select
		ItemConfig PCoIP_Conf1_item7 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.ssl_disable_aes_128", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "0");				// checkBox
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-6", PCoIP_Conf1_item1);        // checkbox       
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-5", PCoIP_Conf1_item2);        // ROOT           
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-3", PCoIP_Conf1_item3);        // name           
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-1", PCoIP_Conf1_item4);        // 最上方，checkBox   
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-4", PCoIP_Conf1_item5);        // 1024,2048,4096 
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-2", PCoIP_Conf1_item6);        // select         
		itemsMap1.put("configure_SSL_to_Security_Tools-Sub-7", PCoIP_Conf1_item7);        // checkBox   
		PolicyConfig PCoIP_Conf1 = new PolicyConfig("configure_SSL_to_Security_Tools", itemsMap1);
		allPol.policies.put("configure_SSL_to_Security_Tools", PCoIP_Conf1);
		
		//pConf2.policyId="Turn off Build-to-Lossless feature";
		PolicyConfig PCoIP_Conf2 = new PolicyConfig("turn_off_Build_To_Lossless", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_build_to_lossless",false);
		allPol.policies.put("turn_off_Build_To_Lossless", PCoIP_Conf2);
		
		//pConf3.policyId="Configure the maximum PCoIP session bandwidth-enabled";
		Map<String,ItemConfig> itemsMap3 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf3_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.max_link_rate", PolEntryType.REG_DWORD, ElementType.ELE_INPUTBOX, "900000");
		itemsMap3.put("configure_Max_PCoIP_Bandwidth-Sub-1", PCoIP_Conf3_item1);
		PolicyConfig PCoIP_Conf3 = new PolicyConfig("configure_Max_PCoIP_Bandwidth", itemsMap3);
		allPol.policies.put("configure_Max_PCoIP_Bandwidth", PCoIP_Conf3);
		
		//pConf4.policyId="Configure the PCoIP session MTU"
		Map<String,ItemConfig> itemsMap4 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf4_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.mtu_size", PolEntryType.REG_DWORD, ElementType.ELE_INPUTBOX, "1200");
		itemsMap4.put("configure_PCoIP__MTU-Sub-1", PCoIP_Conf4_item1);
		PolicyConfig PCoIP_Conf4 = new PolicyConfig("configure_PCoIP__MTU", itemsMap4);
		allPol.policies.put("configure_PCoIP__MTU", PCoIP_Conf4);
		
		//pConf5.policyId="Configure the PCoIP session bandwidth floor"
		Map<String,ItemConfig> itemsMap5 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf5_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.device_bandwidth_floor", PolEntryType.REG_DWORD, ElementType.ELE_INPUTBOX, "0");
		itemsMap5.put("configure_PCoIP_bandwidth_floor-Sub-1", PCoIP_Conf5_item1);
		PolicyConfig PCoIP_Conf5 = new PolicyConfig("configure_PCoIP_bandwidth_floor", itemsMap5);
		allPol.policies.put("configure_PCoIP_bandwidth_floor", PCoIP_Conf5);
		
		//pConf6.policyId="Enable/disable audio in the PCoIP session";
		PolicyConfig PCoIP_Conf6 = new PolicyConfig("pcoip_enable_audio", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_audio",true);
		allPol.policies.put("pcoip_enable_audio", PCoIP_Conf6);
				
		//pConf7.policyId="Enable/disable microphone noise and DC offset filter in PCoIP session";
		PolicyConfig PCoIP_Conf7 = new PolicyConfig("pcoip_enable_micin_noise_filter", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_micin_noise_filter",true);
		allPol.policies.put("pcoip_enable_micin_noise_filter", PCoIP_Conf7);		
				
		//pConf8.policyId="Configure the PCoIP session audio bandwidth limit"
		Map<String,ItemConfig> itemsMap8 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf8_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.audio_bandwidth_limit", PolEntryType.REG_DWORD, ElementType.ELE_INPUTBOX, "500");
		itemsMap8.put("pcoip_audio_bandwidth_limit-Sub-1", PCoIP_Conf8_item1);
		PolicyConfig PCoIP_Conf8 = new PolicyConfig("pcoip_audio_bandwidth_limit", itemsMap8);
		allPol.policies.put("pcoip_audio_bandwidth_limit", PCoIP_Conf8);
		
		//pConf9.policyId="Configure PCoIP session encryption algorithms"
		Map<String,ItemConfig> itemsMap9 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf9_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.enable_salsa20_256_round12", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "1");
		ItemConfig PCoIP_Conf9_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.enable_aes_128_gcm", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "1");
		ItemConfig PCoIP_Conf9_item3 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.enable_aes_256_gcm", PolEntryType.REG_DWORD, ElementType.ELE_CHECKBOX, "1");
		itemsMap9.put("pcoip_enable_aes-Sub-1", PCoIP_Conf9_item1);
		itemsMap9.put("pcoip_enable_aes-Sub-2", PCoIP_Conf9_item2);
		itemsMap9.put("pcoip_enable_aes-Sub-3", PCoIP_Conf9_item3);
		PolicyConfig PCoIP_Conf9 = new PolicyConfig("pcoip_enable_aes", itemsMap9);
		allPol.policies.put("pcoip_enable_aes", PCoIP_Conf9);
		
		//pConf10.policyId="Configure PCoIP USB allowed and unallowed device rules"
		Map<String,ItemConfig> itemsMap10 = new HashMap<String,ItemConfig>();
		ItemConfig PCoIP_Conf10_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.usb_auth_table", PolEntryType.REG_SZ, ElementType.ELE_SELECTBOX, "23XXXXXX");
		ItemConfig PCoIP_Conf10_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.usb_unauth_table", PolEntryType.REG_SZ, ElementType.ELE_SELECTBOX, "");
		itemsMap10.put("pcoip_usb_device_rules-Sub-1", PCoIP_Conf10_item1);
		itemsMap10.put("pcoip_usb_device_rules-Sub-2", PCoIP_Conf10_item2);
		PolicyConfig PCoIP_Conf10 = new PolicyConfig("pcoip_usb_device_rules", itemsMap10);
		allPol.policies.put("pcoip_usb_device_rules", PCoIP_Conf10);
		
		//Common
		Map<String,ItemConfig> itemsMap = null;
		PolicyConfig PCoIP_Conf = null;
		ItemConfig PCoIP_Conf_item1 = null;
		ItemConfig PCoIP_Conf_item2 = null;
		ItemConfig PCoIP_Conf_item3 = null;
		ItemConfig PCoIP_Conf_item4 = null;
		ItemConfig PCoIP_Conf_item5 = null;
		ItemConfig PCoIP_Conf_item6 = null;
		ItemConfig PCoIP_Conf_item7 = null;
		ItemConfig PCoIP_Conf_item8 = null;
		
		//Common policy process
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "policyValue", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "23XXXXXX");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "policyValue", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "");
		itemsMap.put("xx-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("xx-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("policyId", itemsMap);
		//allPol.policies.put("policyId", PCoIP_Conf);
		
		//pConf11.id="Configure the TCP port to which the PCoIP Server binds and listens"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.tcpport", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "4172");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.tcpport_range", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "1");
		itemsMap.put("pcoip_tcpport-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("pcoip_tcpport-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("pcoip_tcpport", itemsMap);
		allPol.policies.put("pcoip_tcpport", PCoIP_Conf);
		
		//pConf12.id="Configure the UDP port to which the PCoIP Server binds and listens"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.udpport", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "4172");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.udpport_range", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "10");
		itemsMap.put("pcoip_udpport-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("pcoip_udpport-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("pcoip_udpport", itemsMap);
		allPol.policies.put("pcoip_udpport", PCoIP_Conf);
		
		//pConf13.id="Configure PCoIP virtual channels"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.enable_vchan", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_SELECTBOX, "0");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.vchan_auth_list", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		PCoIP_Conf_item3 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.vchan_unauth_list", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("pcoip_vchan-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("pcoip_vchan-Sub-2", PCoIP_Conf_item2);
		itemsMap.put("pcoip_vchan-Sub-3", PCoIP_Conf_item3);
		PCoIP_Conf = new PolicyConfig("pcoip_vchan", itemsMap);
		allPol.policies.put("pcoip_vchan", PCoIP_Conf);
		
		//pConf14.id="Configure clipboard redirection"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.server_clipboard_state", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_SELECTBOX, "2");
		itemsMap.put("pcoip_server_clipboard_state-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_server_clipboard_state", itemsMap);
		allPol.policies.put("pcoip_server_clipboard_state", PCoIP_Conf);
		
		//pConf15.id="Configure PCoIP image quality levels"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.minimum_image_quality", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "40");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.maximum_initial_image_quality", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "80");
		PCoIP_Conf_item3 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.maximum_frame_rate", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "30");
		PCoIP_Conf_item4 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.use_client_img_settings", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX, "0");
		itemsMap.put("pcoip_img_quality-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("pcoip_img_quality-Sub-2", PCoIP_Conf_item2);
		itemsMap.put("pcoip_img_quality-Sub-3", PCoIP_Conf_item3);
		itemsMap.put("pcoip_img_quality-Sub-4", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("pcoip_img_quality", itemsMap);
		allPol.policies.put("pcoip_img_quality", PCoIP_Conf);
		
		//pConf16.policyId="Enable the FIPS 140-2 approved mode of operation";
		PCoIP_Conf = new PolicyConfig("pcoip_enable_fips_mode", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_fips_mode",true);
		allPol.policies.put("pcoip_enable_fips_mode", PCoIP_Conf);
		
		//pConf17.policyId="Enable access to a PCoIP session from a vSphere console";
		PCoIP_Conf = new PolicyConfig("pcoip_enable_console_access", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_console_access",true);
		allPol.policies.put("pcoip_enable_console_access", PCoIP_Conf);
		
		//pConf18.policyId="Turn on PCoIP user default input language synchoronization";
		PCoIP_Conf = new PolicyConfig("pcoip_enable_input_language_sync", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.enable_input_language_sync",true);
		allPol.policies.put("pcoip_enable_input_language_sync", PCoIP_Conf);
		
		//pConf19.id="Use alternate key for sending Secure Attention Sequence"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.CAD_insert_substitution_scan_code", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_SELECTBOX, "78");
		itemsMap.put("pcoip_alternate_key-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_alternate_key", itemsMap);
		allPol.policies.put("pcoip_alternate_key", PCoIP_Conf);
		
		//pConf20.policyId="Disable sending CAD when users press Ctrl+Alt+Del";
		PCoIP_Conf = new PolicyConfig("pcoip_disable_cad", "Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults","pcoip.disable_cad",true);
		allPol.policies.put("pcoip_disable_cad", PCoIP_Conf);
		
		//pConf21.id="Configure the PCoIP transport header"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.transport_session_priority", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_SELECTBOX, "4");
		itemsMap.put("pcoip_transport_session_priority-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_transport_session_priority", itemsMap);
		allPol.policies.put("pcoip_transport_session_priority", PCoIP_Conf);
		
		//pConf22.id="Configure PCoIP event log verbosity"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.event_filter_mode", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "2");
		itemsMap.put("pcoip_event_filter_mode-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_event_filter_mode", itemsMap);
		allPol.policies.put("pcoip_event_filter_mode", PCoIP_Conf);
		
		//pConf23.id="Configure PCoIP event log cleanup by time in days"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.event_days_to_keep_log", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "7");
		itemsMap.put("pcoip_event_days_to_keep_log-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_event_days_to_keep_log", itemsMap);
		allPol.policies.put("pcoip_event_days_to_keep_log", PCoIP_Conf);
		
		//pConf24.id="Configure PCoIP event log cleanup by size in MB"
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.event_size_to_keep_log_mb", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "100");
		itemsMap.put("pcoip_event_size_to_keep_log_mb-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("pcoip_event_size_to_keep_log_mb", itemsMap);
		allPol.policies.put("pcoip_event_size_to_keep_log_mb", PCoIP_Conf);
		
		//********************************************************
		//common policy
		
		// log directory
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "LogFileDirectory", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("log_file_directory-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("log_file_directory", itemsMap);
		allPol.policies.put("log_file_directory", PCoIP_Conf);
		
		// Maximum debug log size in Megabytes
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "MaxDebugLogSizeMB", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "10");
		itemsMap.put("max_debug_log_sizemb-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("max_debug_log_sizemb", itemsMap);
		allPol.policies.put("max_debug_log_sizemb", PCoIP_Conf);
		
		// Maximum number of debug logs
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "MaxDebugLogs", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "10");
		itemsMap.put("max_debug_logs-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("max_debug_logs", itemsMap);
		allPol.policies.put("max_debug_logs", PCoIP_Conf);
		
		// Number of days to keep production logs
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "MaxDaysKept", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "7");
		itemsMap.put("max_days_kept-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("max_days_kept", itemsMap);
		allPol.policies.put("max_days_kept", PCoIP_Conf);
		
		// Send logs to a Syslog server
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "SyslogSendSpec", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("sys_log_send_spec-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("sys_log_send_spec", itemsMap);
		allPol.policies.put("sys_log_send_spec", PCoIP_Conf);
		
		//-----------------------------------------------------
		// 06 CPU and Memory Sampling Interval in Seconds
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "SamplingIntervalSeconds", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "60");
		itemsMap.put("sampling_interval_seconds-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("sampling_interval_seconds", itemsMap);
		allPol.policies.put("sampling_interval_seconds", PCoIP_Conf);
		
		// 07 Overall CPU usage percentage to issue log info
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "OverallCpuPercentage", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "90");
		itemsMap.put("overall_cpu_percentage-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("overall_cpu_percentage", itemsMap);
		allPol.policies.put("overall_cpu_percentage", PCoIP_Conf);
		
		// 08 Overall memory usage percentage to issue log info
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "OverallMemoryPercentage", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "75");
		itemsMap.put("overall_memory_percentage-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("overall_memory_percentage", itemsMap);
		allPol.policies.put("overall_memory_percentage", PCoIP_Conf);
		
		// 09 Process CPU usage percentage to issue log info
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "ProcessCpuPercentage", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "90");
		itemsMap.put("process_cpu_percentage-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("process_cpu_percentage", itemsMap);
		allPol.policies.put("process_cpu_percentage", PCoIP_Conf);
		
		// 10 Process memory usage percentage to issue log info
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "ProcessMemoryPercentage", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "75");
		itemsMap.put("process_memory_percentage-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("process_memory_percentage", itemsMap);
		allPol.policies.put("process_memory_percentage", PCoIP_Conf);
		
		// 11 Process to check, comma separated name list
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Performance Alarms", "ProcessNameList", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("process_namelist-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("process_namelist", itemsMap);
		allPol.policies.put("process_namelist", PCoIP_Conf);
		
		//-----------------------------------------------------
		
		// 12 Only use cached revocation URLs
		PCoIP_Conf = new PolicyConfig("cert_revoc_check_cache_only", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Security","CertificateRevocationCheckCacheOnly",false,PolicyType.COMMON);
		allPol.policies.put("cert_revoc_check_cache_only", PCoIP_Conf);
		
		// 13 Revocation URL check timeout milliseconds
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Security", "CertificateRevocationCheckTimeout", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("cert_revoc_check_timeout-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("cert_revoc_check_timeout", itemsMap);
		allPol.policies.put("cert_revoc_check_timeout", PCoIP_Conf);
		
		// 14 Type of certificate revocation check
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Security", "CertificateRevocationCheckType", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "1");
		itemsMap.put("cert_revoc_check_type-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("cert_revoc_check_type", itemsMap);
		allPol.policies.put("cert_revoc_check_type", PCoIP_Conf);
		//-----------------------------------------------------
		
		// 15 Disk threshold for log and events in Megabytes
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM", "DiskThresholdForLogAndEventsMB", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "200");
		itemsMap.put("disk_threshold_for_log_and_eventsMB-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("disk_threshold_for_log_and_eventsMB", itemsMap);
		allPol.policies.put("disk_threshold_for_log_and_eventsMB", PCoIP_Conf);
		
		// 16 Enable extended logging
		PCoIP_Conf = new PolicyConfig("trace_enabled", "Software\\Policies\\VMware, Inc.\\VMware VDM","traceEnabled",false,PolicyType.COMMON);
		allPol.policies.put("trace_enabled", PCoIP_Conf);
		
		// 17 Override the default View Windows event generation
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM", "eventOverride", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("event_override-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("event_override", itemsMap);
		allPol.policies.put("event_override", PCoIP_Conf);

		
		//********************************************************
		// USB
		
		// [Client Downloadable Only Settings 文件夹]
		
		//Allow Audio Input Devices
		//AllowAudioIn
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowAudioIn", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowAudioIn-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowAudioIn", itemsMap);
		allPol.policies.put("AllowAudioIn", PCoIP_Conf);
		
		//Allow Audio Output Devices
		//AllowAudioOut
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowAudioOut", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowAudioOut-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowAudioOut", itemsMap);
		allPol.policies.put("AllowAudioOut", PCoIP_Conf);
		
		//Allow Auto Device Splitting
		//AllowAutoDeviceSplitting
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowAutoDeviceSplitting", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowAutoDeviceSplitting-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowAutoDeviceSplitting", itemsMap);
		allPol.policies.put("AllowAutoDeviceSplitting", PCoIP_Conf);
		
		//Allow other input devices
		//AllowHID
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowHID", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowHID-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowHID", itemsMap);
		allPol.policies.put("AllowHID", PCoIP_Conf);
		
		//Allow HID-Bootable
		//AllowHIDBootable
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowHIDBootable", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowHIDBootable-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowHIDBootable", itemsMap);
		allPol.policies.put("AllowHIDBootable", PCoIP_Conf);
		
		//Allow keyboard and Mouse Devices
		//AllowKeyboardMouse
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowKeyboardMouse", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowKeyboardMouse-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowKeyboardMouse", itemsMap);
		allPol.policies.put("AllowKeyboardMouse", PCoIP_Conf);
		
		//Allow Smart Cards
		//AllowSmartcard
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowSmartcard", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowSmartcard-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowSmartcard", itemsMap);
		allPol.policies.put("AllowSmartcard", PCoIP_Conf);
		
		//Allow Video Devices
		//AllowVideo
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "AllowVideo", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "d:true");
		itemsMap.put("AllowVideo-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AllowVideo", itemsMap);
		allPol.policies.put("AllowVideo", PCoIP_Conf);
		
		//Exclude Vid/Pid Device from Split
		//SplitExcludeVidPid
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "SplitExcludeVidPid", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("SplitExcludeVidPid-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("SplitExcludeVidPid", itemsMap);
		allPol.policies.put("SplitExcludeVidPid", PCoIP_Conf);
		
		//Split Vid/Pid Device
		//SplitVidPid
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "SplitVidPid", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("SplitVidPid-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("SplitVidPid", itemsMap);
		allPol.policies.put("SplitVidPid", PCoIP_Conf);

		//********************************************************
		// [View USB Configuration 文件夹]
		
		//Exclude All Devices
		//ExcludeAllDevices
		PCoIP_Conf = new PolicyConfig("ExcludeAllDevices", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB","ExcludeAllDevices",false,PolicyType.USB);
		allPol.policies.put("ExcludeAllDevices", PCoIP_Conf);
		
		//Exclude Device Family
		//ExcludeFamily
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "ExcludeFamily", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("ExcludeFamily-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("ExcludeFamily", itemsMap);
		allPol.policies.put("ExcludeFamily", PCoIP_Conf);
		
		//Exclude Vid/Pid Device
		//ExcludeVidPid
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "ExcludeVidPid", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("ExcludeVidPid-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("ExcludeVidPid", itemsMap);
		allPol.policies.put("ExcludeVidPid", PCoIP_Conf);
		
		//Include Device Family
		//IncludeFamily
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "IncludeFamily", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("IncludeFamily-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("IncludeFamily", itemsMap);
		allPol.policies.put("IncludeFamily", PCoIP_Conf);
		
		//Include Vid/Pid Device
		//IncludeVidPid
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\USB", "IncludeVidPid", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("IncludeVidPid-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("IncludeVidPid", itemsMap);
		allPol.policies.put("IncludeVidPid", PCoIP_Conf);
		
		//********************************************************
		// [Agent Configuration 文件夹]
		
		//AllowDirectRDP
		//AllowDirectRDP
		PCoIP_Conf = new PolicyConfig("AllowDirectRDP", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","AllowDirectRDP",false,PolicyType.USB);
		allPol.policies.put("AllowDirectRDP", PCoIP_Conf);
				
		//AllowSingleSignon
		//AllowSingleSignon
		PCoIP_Conf = new PolicyConfig("AllowSingleSignon", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","AllowSingleSignon",false,PolicyType.USB);
		allPol.policies.put("AllowSingleSignon", PCoIP_Conf);	
		
		//CredentialFilterExceptions
		//CredentialFilterExceptions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration", "CredentialFilterExceptions", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("CredentialFilterExceptions-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("CredentialFilterExceptions", itemsMap);
		allPol.policies.put("CredentialFilterExceptions", PCoIP_Conf);
		
		//DisableTimeZoneSynchronization
		//DisableTimeZoneSynchronization
		PCoIP_Conf = new PolicyConfig("DisableTimeZoneSynchronization", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","DisableTimeZoneSynchronization",false,PolicyType.USB);
		allPol.policies.put("DisableTimeZoneSynchronization", PCoIP_Conf);
		
		//Enable multi-media accleration
		//EnableMMR
		PCoIP_Conf = new PolicyConfig("EnableMMR", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","EnableMMR",false,PolicyType.USB);
		allPol.policies.put("EnableMMR", PCoIP_Conf);
		
		//Connect using DNS Name
		//ForceDNSName
		PCoIP_Conf = new PolicyConfig("ForceDNSName", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","Force DNS Name",false,PolicyType.USB);
		allPol.policies.put("ForceDNSName", PCoIP_Conf);
		
		//Force MMR to use software overlay
		//ForceMMRToUseSoftwareOverlay
		PCoIP_Conf = new PolicyConfig("ForceMMRToUseSoftwareOverlay", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","ForceMMRToUseSoftwareOverlay",false,PolicyType.USB);
		allPol.policies.put("ForceMMRToUseSoftwareOverlay", PCoIP_Conf);
			
		//ShowDiskActivityIcon
		//ShowDiskActivityIcon
		PCoIP_Conf = new PolicyConfig("ShowDiskActivityIcon", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","ShowDiskActivityIcon",false,PolicyType.USB);
		allPol.policies.put("ShowDiskActivityIcon", PCoIP_Conf);
			
		//Toggle Display Settings Control
		//ToggleDisplaySettingsControl
		PCoIP_Conf = new PolicyConfig("ToggleDisplaySettingsControl", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration","ToggleDisplaySettingsControl",false,PolicyType.USB);
		allPol.policies.put("ToggleDisplaySettingsControl", PCoIP_Conf);
		
		//ConnectionTicketTimeout
		//VdmConnectionTicketTimeout
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration", "VdmConnectionTicketTimeout", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "120");
		itemsMap.put("VdmConnectionTicketTimeout-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("VdmConnectionTicketTimeout", itemsMap);
		allPol.policies.put("VdmConnectionTicketTimeout", PCoIP_Conf);
		
		//********************************************************
		// grid1
		
		//CommandsToRunOnConnect
		//CommandsToRunOnConnect
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnConnect", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnConnect", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID1, "");
		itemsMap.put("CommandsToRunOnConnect-Title", PCoIP_Conf_item1);
		itemsMap.put("CommandsToRunOnConnect-Sub-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("CommandsToRunOnConnect", itemsMap);
		allPol.policies.put("CommandsToRunOnConnect", PCoIP_Conf);
		
		//CommandsToRunOnDisconnect
		//CommandsToRunOnDisconnect
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnDisconnect", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnDisconnect", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID1, "");
		itemsMap.put("CommandsToRunOnDisconnect-Title", PCoIP_Conf_item1);
		itemsMap.put("CommandsToRunOnDisconnect-Sub-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("CommandsToRunOnDisconnect", itemsMap);
		allPol.policies.put("CommandsToRunOnDisconnect", PCoIP_Conf);
		
		//CommandsToRunOnReconnect
		//CommandsToRunOnReconnect
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnReconnect", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\CommandsToRunOnReconnect", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID1, "");
		itemsMap.put("CommandsToRunOnReconnect-Title", PCoIP_Conf_item1);
		itemsMap.put("CommandsToRunOnReconnect-Sub-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("CommandsToRunOnReconnect", itemsMap);
		allPol.policies.put("CommandsToRunOnReconnect", PCoIP_Conf);
		
		
		//********************************************************
		// [Agent Security && VMware Unity 文件夹]
		
		//Accept SSL encrypted framework channel
		//AcceptTicketSSLAuth
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Security", "AcceptTicketSSLAuth", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "0");
		itemsMap.put("AcceptTicketSSLAuth-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AcceptTicketSSLAuth", itemsMap);
		allPol.policies.put("AcceptTicketSSLAuth", PCoIP_Conf);
		
		//Enable Unity Touch
		//EnableUnityTouch
		PCoIP_Conf = new PolicyConfig("EnableUnityTouch", "Software\\Policies\\VMware, Inc.\\VMware VDM\\VMware Unity","EnableUnityTouch",false,PolicyType.USB);
		allPol.policies.put("EnableUnityTouch", PCoIP_Conf);
		
		//********************************************************
		// [View RTAV Configuration 文件夹]
		
		//Disable RTAV
		//DisableRTAV
		PCoIP_Conf = new PolicyConfig("DisableRTAV", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV","DisableRTAV",false,PolicyType.USB);
		allPol.policies.put("DisableRTAV", PCoIP_Conf);
		
		//Resolution - Max image height in pixels
		//WebcamMaxResHeight
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV", "WebcamMaxResHeight", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("WebcamMaxResHeight-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("WebcamMaxResHeight", itemsMap);
		allPol.policies.put("WebcamMaxResHeight", PCoIP_Conf);
		
		//Resolution - Max image width in pixels
		//WebcamMaxResWidth
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV", "WebcamMaxResWidth", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("WebcamMaxResWidth-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("WebcamMaxResWidth", itemsMap);
		allPol.policies.put("WebcamMaxResWidth", PCoIP_Conf);
		
		//Resolution - Default image resolution height in pixels
		//WebcamDefaultResHeight
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV", "WebcamDefaultResHeight", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("WebcamDefaultResHeight-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("WebcamDefaultResHeight", itemsMap);
		allPol.policies.put("WebcamDefaultResHeight", PCoIP_Conf);
		
		//Resolution - Default image resolution width in pixels
		//WebcamDefaultResWidth
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV", "WebcamDefaultResWidth", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "0");
		itemsMap.put("WebcamDefaultResWidth-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("WebcamDefaultResWidth", itemsMap);
		allPol.policies.put("WebcamDefaultResWidth", PCoIP_Conf);
		
		//Max frames per second
		//WebcamMaxFrameRate
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\RTAV", "WebcamMaxFrameRate", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "15");
		itemsMap.put("WebcamMaxFrameRate-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("WebcamMaxFrameRate", itemsMap);
		allPol.policies.put("WebcamMaxFrameRate", PCoIP_Conf);
		
		//********************************************************
		// [Roaming & Synchronization 文件夹]
		
		//Manage user persona
		//UploadProfileInterval
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "UploadProfileInterval", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "10");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "VPEnabled", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "1");
		itemsMap.put("UploadProfileInterval-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("UploadProfileInterval-Addition-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("UploadProfileInterval", itemsMap);
		allPol.policies.put("UploadProfileInterval", PCoIP_Conf);
		
		//Persona repository location
		//CentralProfileOverride
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "CentralProfileStore", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "CentralProfileOverride", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX, "0");
		itemsMap.put("CentralProfileOverride-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("CentralProfileOverride-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("CentralProfileOverride", itemsMap);
		allPol.policies.put("CentralProfileOverride", PCoIP_Conf);
		
		//Remove local persona at log off
		//DeleteLocalProfile
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "DeleteLocalSettings (0x00001000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX, "0");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "DeleteLocalProfile (0x00000002)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "2");
		itemsMap.put("DeleteLocalProfile-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("DeleteLocalProfile-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("DeleteLocalProfile", itemsMap);
		allPol.policies.put("DeleteLocalProfile", PCoIP_Conf);
		
		//Roam local settings folders
		//RoamLocalSettings
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "RoamLocalSettings (0x00004000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "16384");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "RoamLocalSettings (0x00004000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "16384");
		itemsMap.put("RoamLocalSettings-Addition-1", PCoIP_Conf_item1);
		itemsMap.put("RoamLocalSettings-Addition-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("RoamLocalSettings", itemsMap);
		allPol.policies.put("RoamLocalSettings", PCoIP_Conf);
		
		//Files and folders to preload
		//ExcludedFiles
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedFiles", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "DynamicRoamingFiles (0x00080000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "524288");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "DynamicRoamingFiles (0x00080000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "524288");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedFiles", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("ExcludedFiles-Title", PCoIP_Conf_item1);
		itemsMap.put("ExcludedFiles-Addition-1", PCoIP_Conf_item2);
		itemsMap.put("ExcludedFiles-Addition-2", PCoIP_Conf_item3);
		itemsMap.put("ExcludedFiles-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("ExcludedFiles", itemsMap);
		allPol.policies.put("ExcludedFiles", PCoIP_Conf);
		
		//Files and folders to preload (exceptions)
		//ExcludedFilesExclusions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedFilesExclusions", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedFilesExclusions", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("ExcludedFilesExclusions-Title", PCoIP_Conf_item1);
		itemsMap.put("ExcludedFilesExclusions-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("ExcludedFilesExclusions", itemsMap);
		allPol.policies.put("ExcludedFilesExclusions", PCoIP_Conf);
		
		
		//Windows roaming profiles synchronization
		//IgnoredDirs
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\IgnoredDirs", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "BasicRoamingFiles (0x00040000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "262144");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "BasicRoamingFiles (0x00040000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "262144");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\IgnoredDirs", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("IgnoredDirs-Title", PCoIP_Conf_item1);
		itemsMap.put("IgnoredDirs-Addition-1", PCoIP_Conf_item2);
		itemsMap.put("IgnoredDirs-Addition-2", PCoIP_Conf_item3);
		itemsMap.put("IgnoredDirs-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("IgnoredDirs", itemsMap);
		allPol.policies.put("IgnoredDirs", PCoIP_Conf);
		
		
		//Windows roaming profiles synchronization (exception)
		//IgnoredDirsExclusions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\IgnoredDirsExclusions", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\IgnoredDirsExclusions", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("IgnoredDirsExclusions-Title", PCoIP_Conf_item1);
		itemsMap.put("IgnoredDirsExclusions-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("IgnoredDirsExclusions", itemsMap);
		allPol.policies.put("IgnoredDirsExclusions", PCoIP_Conf);
		
		
		//File and folders exclude from roaming
		//DontRoamFiles
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DontRoamFiles", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "DontRoamFiles (0x00200000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "2097152");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "DontRoamFiles (0x00200000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "2097152");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DontRoamFiles", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("DontRoamFiles-Title", PCoIP_Conf_item1);
		itemsMap.put("DontRoamFiles-Addition-1", PCoIP_Conf_item2);
		itemsMap.put("DontRoamFiles-Addition-2", PCoIP_Conf_item3);
		itemsMap.put("DontRoamFiles-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("DontRoamFiles", itemsMap);
		allPol.policies.put("DontRoamFiles", PCoIP_Conf);
		
		
		//File and folders exclude from roaming (exceptions)
		//RoamedFiles
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\RoamedFiles", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\RoamedFiles", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("RoamedFiles-Title", PCoIP_Conf_item1);
		itemsMap.put("RoamedFiles-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("RoamedFiles", itemsMap);
		allPol.policies.put("RoamedFiles", PCoIP_Conf);
		
		
		//Enable background download for laptops //没有子项
		//EnableBackgroundDownload
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Helper Flags", "EnableBackgroundDownload (0x4000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "16384");
		itemsMap.put("EnableBackgroundDownload-Addition-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("EnableBackgroundDownload", itemsMap);
		allPol.policies.put("EnableBackgroundDownload", PCoIP_Conf);
		
		
		//Filders to background download 	// background_folders
		//BackgroundFolders
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\BackgroundFolders", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "BackgroundLoadFolders (0x04000000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "67108864");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "BackgroundLoadFolders (0x04000000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "67108864");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\BackgroundFolders", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("BackgroundFolders-Title", PCoIP_Conf_item1);
		itemsMap.put("BackgroundFolders-Addition-1", PCoIP_Conf_item2);
		itemsMap.put("BackgroundFolders-Addition-2", PCoIP_Conf_item3);
		itemsMap.put("BackgroundFolders-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("BackgroundFolders", itemsMap);
		allPol.policies.put("BackgroundFolders", PCoIP_Conf);
		
		
		//Filders to background download (exceptions)
		//BackgroundFoldersExclusions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\BackgroundFoldersExclusions", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\BackgroundFoldersExclusions", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("BackgroundFoldersExclusions-Title", PCoIP_Conf_item1);
		itemsMap.put("BackgroundFoldersExclusions-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("BackgroundFoldersExclusions", itemsMap);
		allPol.policies.put("BackgroundFoldersExclusions", PCoIP_Conf);
		
		
		//Excluded Processes
		//ExcludedProcesses
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedProcesses", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\ExcludedProcesses", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("ExcludedProcesses-Title", PCoIP_Conf_item1);
		itemsMap.put("ExcludedProcesses-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("ExcludedProcesses", itemsMap);
		allPol.policies.put("ExcludedProcesses", PCoIP_Conf);
		
		
		//Cleanup CLFS Files	//没有子项
		//CleanupCLFSFiles
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "CleanupCLFSFiles (0x00020000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "131072");
		itemsMap.put("CleanupCLFSFiles-Addition-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("CleanupCLFSFiles", itemsMap);
		allPol.policies.put("CleanupCLFSFiles", PCoIP_Conf);
		
		
		//********************************************************
		// [Folder Redirection 文件夹]
		
		//File and folders excluded from Folder Redirection
		//FRExclusions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\FRExclusions", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\FRExclusions", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("FRExclusions-Title", PCoIP_Conf_item1);
		itemsMap.put("FRExclusions-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("FRExclusions", itemsMap);
		allPol.policies.put("FRExclusions", PCoIP_Conf);
		
		
		//File and folders excluded from Folder Redirection (exceptions)
		//FRExclusionsExceptions
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\FRExclusionsExceptions", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID_TITLE, "");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\FRExclusionsExceptions", "", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("FRExclusionsExceptions-Title", PCoIP_Conf_item1);
		itemsMap.put("FRExclusionsExceptions-Sub-1", PCoIP_Conf_item4);
		PCoIP_Conf = new PolicyConfig("FRExclusionsExceptions", itemsMap);
		allPol.policies.put("FRExclusionsExceptions", PCoIP_Conf);
		
		
		//Add the administrators group to redirected folders
		//AddAdminGroup
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "AddAdminGroup (0x40000000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "1073741824");
		itemsMap.put("AddAdminGroup-Addition-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AddAdminGroup", itemsMap);
		allPol.policies.put("AddAdminGroup", PCoIP_Conf);
		
		
		//Application Data(Roaming)
		//AppData
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "AppData", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("AppData-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AppData", itemsMap);
		allPol.policies.put("AppData", PCoIP_Conf);
		
		
		//Temporary Internet Files
		//Cache
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Cache", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Cache-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Cache", itemsMap);
		allPol.policies.put("Cache", PCoIP_Conf);
		
		
		//Cookies
		//Cookies
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Cookies", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Cookies-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Cookies", itemsMap);
		allPol.policies.put("Cookies", PCoIP_Conf);
		
		//Desktop
		//Desktop
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Desktop", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Desktop-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Desktop", itemsMap);
		allPol.policies.put("Desktop", PCoIP_Conf);
		
		//Favorites
		//Favorites
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Favorites", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Favorites-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Favorites", itemsMap);
		allPol.policies.put("Favorites", PCoIP_Conf);
		
		//History
		//History
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "History", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("History-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("History", itemsMap);
		allPol.policies.put("History", PCoIP_Conf);
		
		//My Music
		//MyMusic
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "My Music", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("MyMusic-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("MyMusic", itemsMap);
		allPol.policies.put("MyMusic", PCoIP_Conf);
		
		//My Pictures
		//MyPictures
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "My Pictures", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("MyPictures-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("MyPictures", itemsMap);
		allPol.policies.put("MyPictures", PCoIP_Conf);
		
		//My Video
		//MyVideo
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "My Video", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("MyVideo-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("MyVideo", itemsMap);
		allPol.policies.put("MyVideo", PCoIP_Conf);
		
		//Network Neighborhood
		//NetHood
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "NetHood", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("NetHood-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("NetHood", itemsMap);
		allPol.policies.put("NetHood", PCoIP_Conf);
		
		//My Documents
		//Personal
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Personal", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Personal-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Personal", itemsMap);
		allPol.policies.put("Personal", PCoIP_Conf);
		
		//Printer Neighborhood
		//PrintHood
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "PrintHood", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("PrintHood-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("PrintHood", itemsMap);
		allPol.policies.put("PrintHood", PCoIP_Conf);
		
		//Recent Items
		//Recent
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Recent", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Recent-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Recent", itemsMap);
		allPol.policies.put("Recent", PCoIP_Conf);
		
		//Send To
		//SendTo
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "SendTo", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("SendTo-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("SendTo", itemsMap);
		allPol.policies.put("SendTo", PCoIP_Conf);
		
		//Start Menu
		//StartMenu
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Start Menu", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("StartMenu-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("StartMenu", itemsMap);
		allPol.policies.put("StartMenu", PCoIP_Conf);
		
		//Startup Items
		//Startup
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Startup", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Startup-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Startup", itemsMap);
		allPol.policies.put("Startup", PCoIP_Conf);
		
		//Templates
		//Templates
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "Templates", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Templates-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Templates", itemsMap);
		allPol.policies.put("Templates", PCoIP_Conf);
		
		//Downloads
		//Downloads
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "{374DE290-123F-4565-9164-39C4925E467B}", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Downloads-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Downloads", itemsMap);
		allPol.policies.put("Downloads", PCoIP_Conf);
		
		//Saved Games
		//SavedGames
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "{4C5C32FF-BB9D-43B0-B5B4-2D72E54EAAA4}", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("SavedGames-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("SavedGames", itemsMap);
		allPol.policies.put("SavedGames", PCoIP_Conf);
		
		//Contacts
		//Contacts
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "{56784854-C6CB-462B-8169-88E350ACB882}", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Contacts-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Contacts", itemsMap);
		allPol.policies.put("Contacts", PCoIP_Conf);
		
		//Searches
		//Searches
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "{7D1D3A04-DEBB-4115-95CF-2F29DA2920DA}", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Searches-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Searches", itemsMap);
		allPol.policies.put("Searches", PCoIP_Conf);
		
		//Links
		//Links
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Redirected Folders", "{BFB9D5E0-C6A9-404C-B2B2-AE6DB6AF4968}", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("Links-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("Links", itemsMap);
		allPol.policies.put("Links", PCoIP_Conf);
		
		
		//********************************************************
		// [Desktop UI 文件夹]
		
		//Hide local offline file icon
		//HideOfflineIcon
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\OperationFlags", "HideOfflineIcon (0x00000001)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "1");
		itemsMap.put("HideOfflineIcon-Addition-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("HideOfflineIcon", itemsMap);
		allPol.policies.put("HideOfflineIcon", PCoIP_Conf);
		
		//Show progress when downloading large files
		//FileCopyMinSize
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "FileCopyMinSize", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "50");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Helper Flags", "HideFileCopyProgress (0x00000008)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION_0, "8");
		itemsMap.put("FileCopyMinSize-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("FileCopyMinSize-Addition-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("FileCopyMinSize", itemsMap);
		allPol.policies.put("FileCopyMinSize", PCoIP_Conf);
		
		//Show critical errors to users via tray icon alerts
		//EnableTrayIconErrorAlerts
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Helper Flags", "EnableTrayIconErrorAlerts (0x00000001)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "1");
		itemsMap.put("EnableTrayIconErrorAlerts-Addition-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("EnableTrayIconErrorAlerts", itemsMap);
		allPol.policies.put("EnableTrayIconErrorAlerts", PCoIP_Conf);
		
		//********************************************************
		// [Logging 文件夹]
		
		//Logging destination
		//LogToDebugPort
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DebugDestination", "LogToDebugPort (0x00000001)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "1");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DebugDestination", "LogToFile (0x00000002)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "2");
		itemsMap.put("LogToDebugPort-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("LogToDebugPort-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("LogToDebugPort", itemsMap);
		allPol.policies.put("LogToDebugPort", PCoIP_Conf);
		
		
		//Debug flags
		//DebugFlags
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DebugFlags", "DebugError (0x00000001)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "1");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DebugFlags", "DebugInformation (0x00000200)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "512");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\DebugFlags", "DebugPorts (0x00000004)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "4");
		
		itemsMap.put("DebugFlags-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("DebugFlags-Sub-2", PCoIP_Conf_item2);
		itemsMap.put("DebugFlags-Sub-3", PCoIP_Conf_item3);
		PCoIP_Conf = new PolicyConfig("DebugFlags", itemsMap);
		allPol.policies.put("DebugFlags", PCoIP_Conf);
		
		
		//Logging filename
		//LoggingFilename
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "Log Path", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("LoggingFilename-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("LoggingFilename", itemsMap);
		allPol.policies.put("LoggingFilename", PCoIP_Conf);
		
		
		//Log History Depth
		//LogHistoryDepth
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "LogHistoryDepth", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_INPUTBOX, "1");
		itemsMap.put("LogHistoryDepth-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("LogHistoryDepth", itemsMap);
		allPol.policies.put("LogHistoryDepth", PCoIP_Conf);
		
		
		//Upload log to network
		//RemoteLogPath
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service", "RemoteLogPath", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Operation Flags", "EnableUploadLogToNetwork (0x80000000)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_ADDITION, "-2147483648");
		itemsMap.put("RemoteLogPath-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("RemoteLogPath-Addition-1", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("RemoteLogPath", itemsMap);
		allPol.policies.put("RemoteLogPath", PCoIP_Conf);
		
		
		//Logging flags
		//LoggingFlags
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Log Levels", "LogDebug (0x00000004)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "4");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Log Levels", "LogError (0x00000001)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "1");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Service\\Log Levels", "LogInformation (0x00000002)", PolEntryType.REG_DWORD /*make sure be right*/, ElementType.ELE_CHECKBOX_V, "2");
		
		itemsMap.put("LoggingFlags-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("LoggingFlags-Sub-2", PCoIP_Conf_item2);
		itemsMap.put("LoggingFlags-Sub-3", PCoIP_Conf_item3);
		PCoIP_Conf = new PolicyConfig("LoggingFlags", itemsMap);
		allPol.policies.put("LoggingFlags", PCoIP_Conf);
		
		//********************************************************
		// [View Agent Direct-Connection Configuration 文件夹]
		
		//Client setting:AlwaysConnect
		//alwaysConnect
		PCoIP_Conf = new PolicyConfig("alwaysConnect", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","alwaysConnect",false,PolicyType.USB);
		allPol.policies.put("alwaysConnect", PCoIP_Conf);
		
		//Applications Enabled
		//appsEnabled
		PCoIP_Conf = new PolicyConfig("appsEnabled", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","appsEnabled",false,PolicyType.USB);
		allPol.policies.put("appsEnabled", PCoIP_Conf);
		
		//Client setting:AutoConnect
		//autoConnect
		PCoIP_Conf = new PolicyConfig("autoConnect", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","autoConnect",false,PolicyType.USB);
		allPol.policies.put("autoConnect", PCoIP_Conf);
		
		//Disclaimer Enabled
		//disclaimerEnabled
		PCoIP_Conf = new PolicyConfig("disclaimerEnabled", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","disclaimerEnabled",false,PolicyType.USB);
		allPol.policies.put("disclaimerEnabled", PCoIP_Conf);
		
		//Multimedia redirection (MMR) Enabled
		//mmrEnabled
		PCoIP_Conf = new PolicyConfig("mmrEnabled", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","mmrEnabled",false,PolicyType.USB);
		allPol.policies.put("mmrEnabled", PCoIP_Conf);
		
		//Reset Enabled
		//resetEnabled
		PCoIP_Conf = new PolicyConfig("resetEnabled", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","resetEnabled",false,PolicyType.USB);
		allPol.policies.put("resetEnabled", PCoIP_Conf);
		
		//USB AutoConnect
		//usbAutoConnect
		PCoIP_Conf = new PolicyConfig("usbAutoConnect", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","usbAutoConnect",false,PolicyType.USB);
		allPol.policies.put("usbAutoConnect", PCoIP_Conf);

		//USB Enabled
		//usbEnabled
		PCoIP_Conf = new PolicyConfig("usbEnabled", "Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI","usbEnabled",false,PolicyType.USB);
		allPol.policies.put("usbEnabled", PCoIP_Conf);
		
		//----------------------------------------------------
		//input
		
		//Client Credential Cache Tiemout
		//clientCredentialCacheTimeout
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "clientCredentialCacheTimeout", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("clientCredentialCacheTimeout-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("clientCredentialCacheTimeout", itemsMap);
		allPol.policies.put("clientCredentialCacheTimeout", PCoIP_Conf);

		//Disclaimer Text
		//disclaimerText
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "disclaimerText", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("disclaimerText-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("disclaimerText", itemsMap);
		allPol.policies.put("disclaimerText", PCoIP_Conf);
		
		//Disconnected Session Timeout
		//disconnectedSessionTimeout
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "disconnectedSessionTimeout", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("disconnectedSessionTimeout-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("disconnectedSessionTimeout", itemsMap);
		allPol.policies.put("disconnectedSessionTimeout", PCoIP_Conf);
		
		//External Blast Port
		//externalBlastPort
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "externalBlastPort", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("externalBlastPort-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("externalBlastPort", itemsMap);
		allPol.policies.put("externalBlastPort", PCoIP_Conf);
		
		//External Framework Channel Port
		//externalFrameworkChannelPort
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "externalFrameworkChannelPort", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("externalFrameworkChannelPort-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("externalFrameworkChannelPort", itemsMap);
		allPol.policies.put("externalFrameworkChannelPort", PCoIP_Conf);
		
		//External IP Address
		//externalIPAddress
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "externalIPAddress", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("externalIPAddress-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("externalIPAddress", itemsMap);
		allPol.policies.put("externalIPAddress", PCoIP_Conf);
		
		//External PCoIP Port
		//externalPCoIPPort
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "externalPCoIPPort", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("externalPCoIPPort-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("externalPCoIPPort", itemsMap);
		allPol.policies.put("externalPCoIPPort", PCoIP_Conf);
		
		//External RDP Port
		//externalRDPPort
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "externalRDPPort", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("externalRDPPort-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("externalRDPPort", itemsMap);
		allPol.policies.put("externalRDPPort", PCoIP_Conf);
		
		//HTTPS Port Number
		//httpsPortNumber
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "httpsPortNumber", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("httpsPortNumber-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("httpsPortNumber", itemsMap);
		allPol.policies.put("httpsPortNumber", PCoIP_Conf);

		//Client Setting: ScreenSize
		//screenSize
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "screenSize", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("screenSize-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("screenSize", itemsMap);
		allPol.policies.put("screenSize", PCoIP_Conf);
		
		//Session Timeout
		//sessionTimeout
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("Software\\Policies\\VMware, Inc.\\VMware VDM\\Agent\\Configuration\\XMLAPI", "sessionTimeout", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "");
		itemsMap.put("sessionTimeout-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("sessionTimeout", itemsMap);
		allPol.policies.put("sessionTimeout", PCoIP_Conf);

		
		//********************************************************
		// [VMware Blast 文件夹]
		
		//Screen Blanking
		//BlankScreenEnabled
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "BlankScreenEnabled", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_CHECKBOX, "0");
		itemsMap.put("BlankScreenEnabled-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("BlankScreenEnabled", itemsMap);
		allPol.policies.put("BlankScreenEnabled", PCoIP_Conf);
		
		//Session Garbage Collection
		//SessionGarbageCollection
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "SessionGCInterval_ms", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "1000");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "SessionGCThreshold_s", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "10");
		itemsMap.put("SessionGarbageCollection-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("SessionGarbageCollection-Sub-2", PCoIP_Conf_item2);
		PCoIP_Conf = new PolicyConfig("SessionGarbageCollection", itemsMap);
		allPol.policies.put("SessionGarbageCollection", PCoIP_Conf);
		
		//Image Quality
		//ImageQuality
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "JpegQualityLow", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "25");
		PCoIP_Conf_item2 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "JpegChromaSamplingLow", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "421111");
		PCoIP_Conf_item3 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "JpegQualityMid", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "35");
		PCoIP_Conf_item4 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "JpegQualityHigh", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "90");
		PCoIP_Conf_item5 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "JpegChromaSamplingHigh", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "421111");
		
		itemsMap.put("ImageQuality-Sub-1", PCoIP_Conf_item1);
		itemsMap.put("ImageQuality-Sub-2", PCoIP_Conf_item2);
		itemsMap.put("ImageQuality-Sub-3", PCoIP_Conf_item3);
		itemsMap.put("ImageQuality-Sub-4", PCoIP_Conf_item4);
		itemsMap.put("ImageQuality-Sub-5", PCoIP_Conf_item5);
		PCoIP_Conf = new PolicyConfig("ImageQuality", itemsMap);
		allPol.policies.put("ImageQuality", PCoIP_Conf);
		
		//HTTP Service
		//HTTPService
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "PortSecure", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_INPUTBOX, "22443");
		itemsMap.put("HTTPService-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("HTTPService", itemsMap);
		allPol.policies.put("HTTPService", PCoIP_Conf);
		
		
		//Audio playback
		//AudioEnabled
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "AudioEnabled", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_CHECKBOX, "0");
		itemsMap.put("AudioEnabled-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("AudioEnabled", itemsMap);
		allPol.policies.put("AudioEnabled", PCoIP_Conf);
		
		
		//Configure clipboard redirection
		//ClipboardState
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware Blast\\Config", "ClipboardState", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_SELECTBOX, "1");
		itemsMap.put("ClipboardState-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("ClipboardState", itemsMap);
		allPol.policies.put("ClipboardState", PCoIP_Conf);
		
		//********************************************************usb grid 测试
		// for test   
		// [grid2]
		
		// Filders to background download
		// background_folders
		
		itemsMap = new HashMap<String,ItemConfig>();
		PCoIP_Conf_item1 = new ItemConfig("SOFTWARE\\Policies\\VMware, Inc.\\VMware VDM\\Persona Management\\Driver\\BackgroundFolders", "**delvals.", PolEntryType.REG_SZ /*make sure be right*/, ElementType.ELE_GRID2, "");
		itemsMap.put("background_folders-Sub-1", PCoIP_Conf_item1);
		PCoIP_Conf = new PolicyConfig("background_folders", itemsMap);
		allPol.policies.put("background_folders", PCoIP_Conf);
		
		//********************************************************
		String configPath = System.getProperty("user.dir") + "\\src\\main\\webapp\\resources\\polFiles\\PolicyConfig.json";
		String ConfigJson = JsonUtil.javaToJson(allPol);
		System.out.println(ConfigJson);
		
		
		//********************************************************
		//Test
		ProfileConfig  polConfig = null;
		try {
			JsonUtil.save(configPath, allPol);
			polConfig = JsonUtil.load(configPath, ProfileConfig.class);
		} catch (IOException e) {
			System.out.println("load error !!!");
			e.printStackTrace();
		}
		System.out.println("[load success !!!] " + polConfig.toString());
		
	}
}
