package com.vmware.horizontoolset.policy.gpo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CsvFuncReadall implements ReadCsvFunc{
	private CSVReader csvr;
	
	public CsvFuncReadall(){
	}
	
	@Override
	public List<Map<String, String>> operate(String fileName) {
		try {
			csvr = new CSVReader(new FileReader(fileName));
			List<Map<String, String>> psRes = null;
			psRes = csvr.readAll();
			return psRes;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
