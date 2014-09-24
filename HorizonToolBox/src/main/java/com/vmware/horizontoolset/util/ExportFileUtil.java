package com.vmware.horizontoolset.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.vmware.horizontoolset.usage.Connection;
import com.vmware.horizontoolset.usage.ExportType;

public class ExportFileUtil {
	private static Logger log = Logger.getLogger(ExportFileUtil.class);
	
	private static HSSFCellStyle HeaderStyle;
	private static String[] connectionHeaders={"User","Usage time","Start time","End Time","Machine"};
	
    @SuppressWarnings("unchecked")
	public static void exportExcel(ExportType type,  
           Object data, OutputStream out) throws IOException  
    {  
    	log.info("Start exprot eccel");
        HSSFWorkbook workbook = new HSSFWorkbook();  
        ExportFileUtil.setExcelStyle(workbook);
                
        if(type == ExportType.Connection){
            HSSFSheet sheet = workbook.createSheet(type.toString()); 
            sheet.setDefaultColumnWidth(15);  
        	ExportFileUtil.exportConnectionExcel(sheet, (List<Connection>) data);
        }      
        workbook.write(out);
    } 
    private static void exportConnectionExcel(HSSFSheet sheet,List<Connection> list){
    	log.info("Start export connection ");
    	 ExportFileUtil.exportHeaders(sheet, connectionHeaders);
    	 HSSFRow row = null;  
    	 if(list == null) return;
    	 log.info("Export connections size:"+list.size());
         for(int j=0;j<list.size();j++)
         {
        	 Connection connection = list.get(j);
              row = sheet.createRow(j+1);  
              row.createCell(0).setCellValue(connection.getUserName());
              row.createCell(1).setCellValue(connection.getUsageTime());
              row.createCell(2).setCellValue(connection.getConnectionTime());
              row.createCell(3).setCellValue(connection.getDisconnectionTime());
              row.createCell(4).setCellValue(connection.getMachineName());
           }      
    }
   
    private static void setExcelStyle(HSSFWorkbook workbook){
    
        HSSFCellStyle style = workbook.createCellStyle();  
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);  
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
 
        HSSFFont font = workbook.createFont();  
        font.setColor(HSSFColor.VIOLET.index);  
        font.setFontHeightInPoints((short) 12);  
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
        style.setFont(font);  
        HeaderStyle = style;
      }
    private static void exportHeaders(HSSFSheet sheet, String[] headers){
    	 HSSFRow row = sheet.createRow(0);  
    	  for (int i = 0; i < headers.length; i++)  
          {  
              HSSFCell cell = row.createCell(i); 
              cell.setCellStyle(HeaderStyle);
              HSSFRichTextString text = new HSSFRichTextString(headers[i]);  
              cell.setCellValue(text);  
          }  
    }
}
