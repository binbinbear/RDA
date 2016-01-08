package com.vmware.horizon.auditing.db;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vdi.adamwrapper.adam.AdamEventsDatabaseManager;
import com.vmware.vdi.adamwrapper.exceptions.ADAMConnectionFailedException;
import com.vmware.vdi.adamwrapper.exceptions.ADAMServerException;
import com.vmware.vdi.adamwrapper.ldap.VDIContext;
import com.vmware.vdi.adamwrapper.ldap.VDIContextFactory;
import com.vmware.vdi.adamwrapper.objects.EventsDatabase;
import com.vmware.vdi.admin.be.events.AdminEventDatabase;

public class EventDBUtil implements AutoCloseable, ToolBoxDB {
	private static Logger log = Logger.getLogger(EventDBUtil.class);
	private VDIContext vdiContext;

	public EventDBUtil(VDIContext vdiContext) throws ADAMConnectionFailedException{
		this.vdiContext = vdiContext;
	}

	private EventDBUtil() throws ADAMConnectionFailedException {
		this( VDIContextFactory.defaultVDIContext());
	}

	public static EventDBUtil createDefault() {
		try {
			return new EventDBUtil();
		} catch (Exception e) {
			log.error("Fail creating EventDBUtil.", e);
		}
		return null;
	}


	@Override
	public void close() {

		if (vdiContext != null) {
			vdiContext.close();
			vdiContext = null;
		}
	}


	@Override
	public boolean isToolboxTableAvaiable() {
		List<EventsDatabase> dbconfigs;
		try {
			dbconfigs = AdamEventsDatabaseManager.getInstance().getAll(this.vdiContext);
		} catch (ADAMServerException e) {
			// TODO Auto-generated catch block
			log.error("Cant' get db configuration:"+e.getMessage(), e);
			return false;
		}
		if (dbconfigs.isEmpty()) {
			log.debug("Database configuration not found");
			return false;
		}
		EventsDatabase dbconfig = dbconfigs.get(0);


		AdminEventDatabase database;
		try {
			database = new AdminEventDatabase(dbconfig);

			if (database.connect(dbconfig)) {
				if (database.isToolboxTableExists()) {
						log.info("Toolbox table is already there, return true");
						return true;
				}
			}

		} catch (Exception e) {

		}


		return false;
	}

	@Override
	public void removeKey(String... keys) throws StorageException {


	}

	@Override
	public List<StorageItem> getItems(String... keys) throws StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addItems(List<StorageItem> items) throws StorageException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addItem(StorageItem item) throws StorageException {
		// TODO Auto-generated method stub

	}



}
