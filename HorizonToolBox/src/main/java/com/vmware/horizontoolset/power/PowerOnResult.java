package com.vmware.horizontoolset.power;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PowerOnResult {
	private static List<PowerOnJobResult> _alljobsresults = new CopyOnWriteArrayList<PowerOnJobResult>();
	public static void addResult(PowerOnJobResult result){
		_alljobsresults.add(result);
	}
	
	public static List<PowerOnJobResult> getAllResults(){
		
		return _alljobsresults;
	}
	
}
