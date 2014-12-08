package com.vmware.horizontoolset.util.polfile;



public class TestPol {

	public static void main(String[] args) {
		//*
		PolFile pf = new PolFile();
		pf.setString("k1", "v1", "Lingling island");
		pf.setDWORD("k2", "v2", 12345);
		pf.save("/1.pol");
		
		
		pf = new PolFile();
		pf.load("/1.pol");
		pf._dump();
		
		
		pf = new PolFile();
		pf.load("z:/temp/sample_all.pol");
		pf._dump();
		
		//*/
	}

}
