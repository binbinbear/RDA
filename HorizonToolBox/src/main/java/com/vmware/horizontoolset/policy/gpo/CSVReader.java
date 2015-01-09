package com.vmware.horizontoolset.policy.gpo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CSVReader {

   BufferedReader br;
   boolean hasNext = true;

   /**
    * Constructor.
    * 
    * @param reader
    *           the reader to an underlying CSV source.
    */
   public CSVReader(Reader reader) {
      this.br = new BufferedReader(reader);
   }

   /**
    * Reads the entire file into a List with each element being a String[] of
    * tokens.
    * 
    * @return a List of String[], with each String[] representing a line of the
    *         file.
    * 
    * @throws IOException
    *            if bad things happen during the read
    */
   public List<Map<String, String>> readAll() throws IOException {
      List<Map<String, String>> allMapElements =
            new ArrayList<Map<String, String>>();
      Map<String, String> mapElement = new HashMap<String, String>();
      while (hasNext) {
         String[] key = readNext();
         String[] value = readNext();
         if (key != null) {
            for (int i = 0; i < key.length; i++) {
               mapElement.put(key[i], value[i]);
            }
            allMapElements.add(mapElement);
         }
      }
      return allMapElements;

   }

   /**
    * Reads the next line from the buffer and converts to a string array.
    * 
    * @return a string array with each comma-separated element as a separate
    *         entry.
    * 
    * @throws IOException
    *            if bad things happen during the read
    */
   public String[] readNext() throws IOException {

      String nextLine = br.readLine();
      if (nextLine == null) {
         hasNext = false;
      }
      return hasNext ? parseLine(nextLine) : null;
   }

   /**
    * Parses an incoming String and returns an array of elements.
    * 
    * @param nextLine
    *           the string to parse
    * @return the comma-tokenized list of elements, or null if nextLine is null
    */
   public static String[] parseLine(String nextLine) {

      if (nextLine == null) {
         return null;
      }
      List<String> tokensOnThisLine = new ArrayList<String>();
      StringBuffer sb = new StringBuffer();
      boolean inQuotes = false;
      for (int i = 0; i < nextLine.length(); i++) {

         char c = nextLine.charAt(i);
         if (c == '"') {
            inQuotes = !inQuotes;
         } else if (c == ',' && !inQuotes) {
            tokensOnThisLine.add(sb.toString());
            sb = new StringBuffer(); // start work on next token
         } else {
            sb.append(c);
         }

      }
      tokensOnThisLine.add(sb.toString());
      return tokensOnThisLine.toArray(new String[0]);
   }

   public static void main(String[] args) throws IOException {
      File file = new File("tiliuaQWJk.csv");
      BufferedReader bufRdr;
      try {
         bufRdr = new BufferedReader(new FileReader(file));
         CSVReader csvReader = new CSVReader(bufRdr);
         try {
            List<Map<String, String>> list = csvReader.readAll();
            for (int i = 0; i < list.size(); i++) {
               list.get(i);
               Iterator<String> it = list.get(i).keySet().iterator();
               while (it.hasNext()) {
                  String name = it.next();
                  System.out.println("key= " + name + ", value = "
                        + list.get(i).get(name));
               }
            }
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         bufRdr.close();
      } catch (FileNotFoundException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

   }
}
