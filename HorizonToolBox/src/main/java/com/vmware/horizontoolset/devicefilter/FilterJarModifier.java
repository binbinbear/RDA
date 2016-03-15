package com.vmware.horizontoolset.devicefilter;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import com.vmware.horizontoolset.Application;
import com.vmware.horizontoolset.util.FileUtil;

public class FilterJarModifier implements IFilterJarModifier{
	private static Logger log = Logger.getLogger(FilterJarModifier.class);

	@Override
	public boolean isFilterJarFound(){
		File targetJar = getJarDestFile();
		if (targetJar.exists()){
			log.info("Toolbox Filter Jar Found in broker");
			return true;
		}
		log.info("Toolbox Filter Jar Missed in broker");
		return false;
	}
	@Override
	public void putFilterJar(){
		try {
			FileCopyUtils.copy(getSrcFilterJar(), getJarDestFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Can't copy jar from src to dest:" +e.getMessage(), e);
		}
	}
	@Override
	public void removeFilterJar(){
		getJarDestFile().delete();
	}

	private static final String filtername = "toolboxfilter-0.0.1-SNAPSHOT.jar";
	private File getJarDestFile() {
		String serverPath = Application.getViewServerPath();

		return new File(serverPath + "\\broker\\webapps\\ROOT\\WEB-INF\\lib\\" + filtername);
	}


	public static File getSrcFilterJar(){
		String jarpath = FileUtil.getWebAppRoot() + "/resources/" + filtername;
		File jar = new File(jarpath);
		return jar;
	}


	public static void main(String args[]) {
		System.setProperty("horizontoolset.root", "C:\\Program Files\\VMware\\HorizonToolbox\\HorizonToolbox2.0.1\\webapps\\toolbox");

		FilterJarModifier modifier = new FilterJarModifier();
		System.out.println(modifier.isFilterJarFound());
		if (modifier.isFilterJarFound()){
			modifier.removeFilterJar();
		}else{
			modifier.putFilterJar();
		}


	}
}
