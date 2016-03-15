package com.vmware.vdi.broker.devicefilter;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

import com.vmware.vdi.broker.toolboxfilter.util.JsonUtil;
public class BlockedLogger {

	private static BlockedLogger _instance ;

	public static BlockedLogger getInstance(){
		if (_instance ==null){
			synchronized (BlockedLogger.class){
				if (_instance ==null){
					_instance = new BlockedLogger();
				}
			}
		}
		return _instance;
	}

	private static final String logfilename = "ToolboxBlocked.log";
	private  Logger thislog;
	private static Logger systemlog = Logger.getLogger(BlockedAccess.class);
	private BlockedLogger(){
		systemlog.info("Toolbox Block logger is starting");
		try {

			SimpleLayout layout = new SimpleLayout();
			FileAppender appender = new RollingFileAppender(layout, logfilename, true);

			thislog = Logger.getLogger(BlockedLogger.class);

			thislog.addAppender(appender);
			thislog.setLevel(Level.INFO);

			systemlog.info("Toolbox logger has been started!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			systemlog.error("can't start logger:"+e.getMessage(),e);

		}
	}


	public void logBlockedAccess (BlockedAccess blocked){
		thislog.info(JsonUtil.javaToJson(blocked));

	}

	public static void main(String args[]){
		BlockedLogger.getInstance().logBlockedAccess(new BlockedAccess (new HashMap<String, String>(), "testpool"));
	}
}
