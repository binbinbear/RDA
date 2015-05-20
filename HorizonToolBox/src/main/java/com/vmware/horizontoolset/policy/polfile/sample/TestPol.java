package com.vmware.horizontoolset.policy.polfile.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vmware.horizontoolset.policy.polfile.PolFile;
import com.vmware.horizontoolset.policy.polfile.PolFileException;



public class TestPol {

	public static void main(String[] args) {

		try {	
			/*
			PolFile pf = new PolFile();
			pf.setString("k1", "v1", "Lingling island");
			pf.setDWORD("k2", "v2", 12345);
			pf.save("/1.pol");
			
			pf = new PolFile();
			pf.load("/1.pol");
			pf._dump();
			//*/
		
			//dumpPolFile("sample_base_all.pol");
			//dumpPolFile("registry.pol");
			//dumpPolFile("registry2.pol");
			//dumpPolFile("C:/testPol/registry_all.pol");
			//dumpPolFile("registry_little_common.pol");
			PolFile pf = new PolFile();
			pf.load("c:/policy_samples/test/fromAd.pol");
//			pf.deleteValue("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.server_clipboard_state");
//			pf.setString("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "**del.pcoip.server_clipboard_state", "");
//			
//			pf._dump();
//			pf.setDWORD("Software\\Policies\\VMware, Inc.\\VMware VDM\\Log", "MaxDaysKept", 5);
//			pf.setString("Software\\Policies\\Teradici\\PCoIP\\pcoip_admin_defaults", "pcoip.cert_store_name", "vmware");
			System.out.println("[===========================]");
			pf._dump();
			
			
		//	pf.save("c:/testPol/registry_to_disabled.pol");
			
			/*
			byte[] bytes = BitUtil.getUnicodeBytes("\0\0");
			BitUtil.dump(bytes);
			bytes = BitUtil.getUnicodeBytes("我的征途是星辰大海 abc");
			BitUtil.dump(bytes);
			*/
		} catch (PolFileException e) {
			e.printStackTrace();
		}
	}

	private static void dumpPolFile(String name) {
		PolFile pf = loadPolFile(name);
		if (pf == null)
			
			return;
		
		System.out.println("===========================");
		System.out.println("PolFile: " + name);
		System.out.println();
		pf._dump();
	}
	
	private static PolFile loadPolFile(String name) {
		//name = "/" + TestPol.class.getName().replace('.', '/') + '/' + name;
		byte[] bytes = loadRes(name);
		if (bytes == null) {
			System.out.println("Pol file not found in resource: " + name);
			return null;
		}
		
		PolFile pf = new PolFile();
		pf.load(bytes);
		return pf;
	}
	
	private static byte[] loadRes(String name) {
		
		try (InputStream in = TestPol.class.getResourceAsStream(name);) {
			
			if (in == null){
				System.out.println("return null");
				return null;
			}
				
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1048576];
			
			while (true) {
				int n = in.read(buf);
				if (n < 0)
					break;
				
				out.write(buf, 0, n);
			}
			
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
