package com.vmware.eucenablement.service;

import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmware.eucenablement.util.SessionUtil;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.FileManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualDiskManager;

/**
 * Service to handle vSphere related actions
 * 
 * @author sam
 *
 */
public class VCenterService {
	private static Logger log = Logger.getLogger(VCenterService.class);
	public ServiceInstance si;

	private VCenterService(String server, String name, String password) {
		try {
			si = new ServiceInstance(new URL("https://" + server + "/sdk"), name, password, true);
		} catch (Exception e) {
			log.error("can not connect to vcenter");
			si = null;
		}

	}

	// get vCenter service instance
	public synchronized static VCenterService instance(HttpSession session) {
		VCenterService api = SessionUtil.getVCenterService(session);
		if (null == api) {
			log.warn("Vcenter session timeout");
		}
		return api;
	}

	// login to vCenter, and store vCenter service in session
	public synchronized static void login(HttpSession session, String server, String name, String password) {
		VCenterService api = new VCenterService(server, name, password);
		if (api.si != null) {
			SessionUtil.setSessionObj(session, api);
		}

	}

	/**
	 * Copy source AppStack files including vmdk and metadata files to target
	 * location
	 * 
	 * @param srcDatastore
	 * @param srcFilePath
	 * @param srcFileName
	 * @param dstDatastore
	 * @param dstFilePath
	 * @param dstFileName
	 * @return true copy succeed; false copy fail
	 */
	public boolean copyFile(String srcDatastore, String srcFilePath, String srcFileName, String dstDatastore, String dstFilePath, String dstFileName) {
		boolean result = false;

		try {

			Folder rootFolder = si.getRootFolder();
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			if (mes == null || mes.length == 0) {
				System.out.println(" no datacenters");
				return result;
			}

			Datacenter[] datacenterArray = Arrays.copyOf(mes, mes.length, Datacenter[].class);

			Datacenter srcDatacenter = getDatacenterbyDatastore(datacenterArray, srcDatastore);
			Datacenter dstDatacenter = getDatacenterbyDatastore(datacenterArray, dstDatastore);

			// Work around for vmdk file here : rename ->copy -> rename

			FileManager fileMgr = si.getFileManager();
			if (fileMgr == null) {
				System.out.println("FileManager not available.");
				return result;
			}

			// create parent directories if needed
			String dstDir = "[" + dstDatastore + "]" + dstFilePath;
			fileMgr.makeDirectory(dstDir, dstDatacenter, true);

			if (!srcFilePath.endsWith("/")) {
				srcFilePath = srcFilePath + "/";
			}
			if (!dstFilePath.endsWith("/")) {
				dstFilePath = dstFilePath + "/";
			}

			String srcPath = "[" + srcDatastore + "]" + srcFilePath + srcFileName;
			String dstPath = "[" + dstDatastore + "]" + dstFilePath + dstFileName;

			// rename source vmdk by adding a file extension '.tmp'
			if (srcFileName.endsWith("vmdk")) {
				Task mTask = fileMgr.moveDatastoreFile_Task(srcPath, srcDatacenter, srcPath + ".tmp", srcDatacenter, true);
				if (mTask.waitForTask() == Task.SUCCESS) {
					System.out.println("rename vmdk to tmp successfully!");
				} else {
					System.out.println("rename vmdk to tmp files failed!");
					return result;
				}

				// copy the renamed file
				Task cTask = fileMgr.copyDatastoreFile_Task(srcPath + ".tmp", srcDatacenter, dstPath, dstDatacenter, true);

				if (cTask.waitForTask() == Task.SUCCESS) {
					System.out.println("tmp vmdk File copied successfully!");
				} else {
					System.out.println("tmp vmdk File copy failed!");
					return result;
				}
				Thread.sleep(5 * 1000);

				// copy the new generated flat file
				String flatFileName = srcFileName.substring(0, srcFileName.lastIndexOf(".vmdk")) + "-flat.vmdk";
				String srcFlatPath = "[" + srcDatastore + "]" + srcFilePath + flatFileName;
				String dstFlatPath = "[" + dstDatastore + "]" + dstFilePath + flatFileName;

				cTask = fileMgr.copyDatastoreFile_Task(srcFlatPath, srcDatacenter, dstFlatPath, dstDatacenter, true);

				if (cTask.waitForTask() == Task.SUCCESS) {
					System.out.println("flat vmdk File copied successfully!");
				} else {
					System.out.println("flat vmdk File copy failed!");
					return result;
				}
				Thread.sleep(5 * 1000);
			}
			// copy the metadata file
			Task cTask = fileMgr.copyDatastoreFile_Task(srcPath + ".metadata", srcDatacenter, dstPath + ".metadata", dstDatacenter, true);
			if (cTask.waitForTask() == Task.SUCCESS) {
				System.out.println(".metadata File copied successfully!");
			} else {
				System.out.println(".metadata File copy failed!");
				return result;
			}
			Thread.sleep(5 * 1000);

			// Clear up , rename source tmp vmdk to original source file name
			Task mTask = fileMgr.moveDatastoreFile_Task(srcPath + ".tmp", srcDatacenter, srcPath, srcDatacenter, true);
			if (mTask.waitForTask() == Task.SUCCESS) {
				System.out.println("rename tmp to vmdk file successfully!");
			} else {
				System.out.println("rename tmp to vmdk files failed!");
				return result;
			}

			Thread.sleep(5 * 1000);
			result = true;
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	private synchronized static Datacenter getDatacenterbyDatastore(Datacenter[] datacenterArray, String datastoreName) {

		for (Datacenter datacenter : datacenterArray) {
			// Assumption : datastore can have duplicated name in one vCenter
			Datastore[] datastories = datacenter.getDatastores();
			for (Datastore datastore : datastories) {
				if (datastore.getName().equalsIgnoreCase(datastoreName.trim())) {
					return datacenter;
				}
			}
		}
		return null;
	}

	/**
	 * Get the Datastore parent Datacenter name
	 * 
	 * @param datastore
	 * @return
	 */
	public String getDatacenter(String datastore) {
		String result = null;
		try {
			Folder rootFolder = si.getRootFolder();
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			if (mes == null || mes.length == 0) {
				log.error(" no datacenters");
				return result;
			}

			Datacenter[] datacenterArray = Arrays.copyOf(mes, mes.length, Datacenter[].class);
			Datacenter datacenter = getDatacenterbyDatastore(datacenterArray, datastore);
			result = datacenter.getName();

		} catch (Exception e) {
			return result;
		}
		return result;

	}

	/**
	 * Copy source AppStack files including vmdk and meta data files to target
	 * location
	 * 
	 * @param srcVDK
	 *            source AppStack
	 * @param dstDatastore
	 *            destination of Data store
	 * @param dstFilePath
	 *            destination of file path
	 * @param dstFileName
	 *            destination of file name
	 * @return is success?
	 * 
	 * @author Xiaoning
	 */
	public boolean copyVirtualDisk(String srcVDK, String dstDatastore, String dstFilePath, String dstFileName) {
		boolean result = false;
		try {

			Folder rootFolder = si.getRootFolder();
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
			if (mes == null || mes.length == 0) {
				log.error(" no datacenters");
				return result;
			}

			Datacenter[] datacenterArray = Arrays.copyOf(mes, mes.length, Datacenter[].class);

			Datacenter srcDatacenter = getDatacenterbyDatastore(datacenterArray, srcVDK.substring(1, srcVDK.indexOf(']')));
			Datacenter dstDatacenter = getDatacenterbyDatastore(datacenterArray, dstDatastore);

			VirtualDiskManager vdm = si.getVirtualDiskManager();
			FileManager fileMgr = si.getFileManager();

			if (fileMgr == null || vdm == null) {
				log.error("FileManager not available.");
				return result;
			}
			// create parent directories if needed
			String dstDir = "[" + dstDatastore + "]" + dstFilePath;
			fileMgr.makeDirectory(dstDir, dstDatacenter, true);
			if (!dstDir.endsWith("/")) {
				dstDir = dstDir + "/";
			}
			String dstPath = dstDir + dstFileName;
			// copy virtual disk
			Task cTask = vdm.copyVirtualDisk_Task(srcVDK, srcDatacenter, dstPath, dstDatacenter, null, true);

			if (cTask.waitForTask() == Task.SUCCESS) {
				log.info("File vdk copy successfully!");
			} else {
				log.warn("File vdk copy failed!");
				return result;
			}
			Thread.sleep(5 * 1000);
			// copy the meta data file
			String srcMT = srcVDK + ".metadata";
			dstPath = dstPath + ".metadata";
			cTask = fileMgr.copyDatastoreFile_Task(srcMT, srcDatacenter, dstPath, dstDatacenter, true);
			if (cTask.waitForTask() == Task.SUCCESS) {
				log.info("metadata copy successfully!");
			} else {
				log.warn("metadata copy failed!");
				return result;
			}
			Thread.sleep(5 * 1000);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
}
