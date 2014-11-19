package com.vmware.horizontoolset.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.vmware.horizontoolset.usage.Connection;
import com.vmware.vdi.admin.be.events.AdminEvent;



public class ExportFileUtil {
	private static Logger log = Logger.getLogger(ExportFileUtil.class);
	
	private static final String[] connectionHeaders={"User Name","Connection Time","Disconnection Time","Usage Time(seconds)","machineName", "poolName", "farmName"};
	
	private static final String[] connectionBeanAttrs={"username","connectionTime","disconnectionTime","usageTime","machineName", "poolName", "farmName"};
	private static final String DateFormatString = "dd-MMM-yy HH:mm:ss";
	private static final CellProcessor[] processors = new CellProcessor[] { 
            new Optional(), // username (must be unique)
            new FmtDate(DateFormatString), // connection time
            new FmtDate(DateFormatString), // dis connect time
            new Optional(), // usageTime
            new Optional(), // machine
            new Optional(), // pool
            new Optional() // farm
            
    };
	
	

	public static void exportConnections(List<Connection> data, Writer writer) throws IOException   
    {  
		
		if (data==null || data.size()==0){
			data = new ArrayList<Connection>();
			AdminEvent aeve = new  AdminEvent();
			aeve.setEventId(1);
			aeve.setMessage("login");
			aeve.setTime(new Date());
			aeve.setUsername("hello");
			EventImpl event = new EventImpl(aeve);
			ConnectionImpl conn = new ConnectionImpl(event, event);
			data.add(conn);
		}
		CsvBeanWriter csvwriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
    	log.info("Start exprot csv");
    	csvwriter.writeHeader(connectionHeaders);
    	for (Connection connection: data){
    		csvwriter.write(connection,connectionBeanAttrs,processors );
    	}
    	
    	csvwriter.flush();
    	csvwriter.close();
    } 

}
