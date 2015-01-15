package com.vmware.eucenablement.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * 
 * A storage to store key-value pair. The storage is shared, with late
 * consistency.
 * 
 * Shared: the storage will be shared across multiple View Connection servers.
 * Late consistency: Changes to the storage is only visible to other readers
 * after certain time period.
 * 
 * The shared storage stores only small values, e.g. less than 100k.
 *
 * @author Xiao ning
 *
 */
public class SharedStorageAccess {

	private static Logger log = Logger.getLogger(SharedStorageAccess.class);

	private static final String fileName = "SharedStorage";

	public synchronized static boolean write2File(String key, String value) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file, true))) {
			if (!isExists(key, value)) {
				bufferWriter.newLine();
				bufferWriter.append(key + "=" + value);
				bufferWriter.flush();
				return true;
			} else {
				bufferWriter.flush();
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void write2File(ArrayList<String> lines) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		try (BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file, true))) {
			for (String line : lines) {
				bufferWriter.newLine();
				bufferWriter.append(line);
			}
			bufferWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static ArrayList<String> readFile(String key) {
		ArrayList<String> values = null;
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		try (BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
			values = new ArrayList<>();
			String line;
			while ((line = bufferReader.readLine()) != null) {
				if (line.contains("=")) {
					if (key.equals(line.substring(0, line.indexOf("=")))) {
						values.add(line.substring(line.indexOf("=") + 1));
						continue;
					}
				} else {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	public synchronized static boolean deleteLine(String key, String value) {
		ArrayList<String> temp = null;
		File file = new File(fileName);
		System.out.println("delete" + file.getAbsolutePath());
		if (!file.exists()) {
			return false;
		}
		try (BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
			temp = new ArrayList<>();
			String line;
			while ((line = bufferReader.readLine()) != null) {
				if (line.contains("=")) {
					if (key.equals(line.substring(0, line.indexOf("="))) && value.equals(line.substring(line.indexOf("=") + 1))) {
						continue;
					} else {
						temp.add(line);
					}
				}
			}
			clearFile();
			write2File(temp);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean isExists(String key, String value) {
		File file = new File(fileName);
		System.out.println("isExists" + file.getAbsolutePath());
		if (!file.exists()) {
			return false;
		}
		try (BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = bufferReader.readLine()) != null) {
				if (line.contains("=")) {
					if (key.equals(line.substring(0, line.indexOf("="))) && value.equals(line.substring(line.indexOf("=") + 1))) {
						return true;
					} else {
						continue;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private static void clearFile() {
		File file = new File(fileName);
		if (!file.exists()) {
			return;
		} else {
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
				bufferedWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
