package com.vmware.eucenablement.horizontoolset.av.api;

import com.vmware.eucenablement.horizontoolset.av.api.impl.VolumeImpl;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.AppStack;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Application;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Computer;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Entity;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.ExcuteResult;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Message;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.OnlineEntity;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Type;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.User;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Writable;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class helpe us to do some simple operation after login to the server.
 * Generally, more focus on user, computer and app stacks.
 *
 * @author XiaoNing
 * @version 1.0.0
 *
 */
public class VolumeAPI implements Closeable {

	private static final Logger LOG = Logger.getLogger(VolumeAPI.class);
	private VolumeImpl _volumeImpl;

	private List<User> _users = null;
	private List<Computer> _computers = null;
	private List<AppStack> _appStacks = null;
	private List<Writable> _writables = null;

	public VolumeAPI() {
	}

	public VolumeAPI(String server, String domain, String name, String password) throws Exception {
		connect(server, domain, name, password);
	}

	/**
	 * Connect to cloud volume manager
	 *
	 * @param server
	 *            address(eg:127.0.0.1) of the manager host
	 * @param domain
	 *            not in use
	 * @param name
	 *            user name to login to the server
	 * @param password
	 *            password to login to the server
	 * @throws Exception
	 *             if not success throws exception
	 */
	public void connect(String server, String domain, String name, String password) throws Exception {
		if (null == server || "" == server || null == name || "" == name || null == password || "" == password)
			throw new IllegalArgumentException();

		_volumeImpl = new VolumeImpl(server);
		Map<String, String> params = new HashMap<>();
		params.put("domain", domain);
		params.put("user[account_name]", name);
		params.put("user[password]", password);

		if (!_volumeImpl.login(params))
			throw new Exception("can not connect to the server");
		LOG.info(new Date().toString() + ": " + name + " login server: " + server);
	}

	/**
	 * Convert user name or computer name to user id or computer id
	 *
	 * @param dnsNames
	 *            computer names or user names include its
	 *            domain.(eg:win7x64.eucsolution)
	 * @param type
	 *            enum{USER,COMPUTER}
	 * @return the list of ids
	 */
	private List<Long> _convertNames2Ids(List<String> dnsNames, Type type) {
		List<Long> ids = new ArrayList<Long>();
		if (type == Type.COMPUTER) {
			for (String name : dnsNames) {
				for (Computer computer : _listComputersImpl()) {
					if (name.equalsIgnoreCase(computer.getDomainName())) {
						ids.add(computer.getId());
						continue;
					}
				}
			}
		} else if (type == Type.USER) {
			for (String name : dnsNames) {
				for (User user : _listUsersImpl()) {
					if (name.equalsIgnoreCase(user.getDomainName())) {
						ids.add(user.getId());
						continue;
					}
				}
			}
		}

		return ids;
	}

	/**
	 * Fetch all computer installed volume agent
	 *
	 * @return the list of computers
	 */
	public List<Computer> listComputers() {
		return new ArrayList<Computer>(_listComputersImpl());
	}

	/**
	 * Fetch all user controlled by volume manage
	 *
	 * @return the list of computers
	 */
	public List<User> listUsers() {
		return new ArrayList<User>(_listUsersImpl());
	}

	/**
	 * Get all available app stacks from volume manager
	 *
	 * @return all app stacks
	 */
	public List<AppStack> listAppStacks() {
		return new ArrayList<AppStack>(_listAppStacksImpl());
	}


	/**
	 * Get all writable volumes from volume manager
	 * @return all writable volumes
	 */
	public List<Writable> listWritables() {
	    return new ArrayList<Writable>(_listWritablesImpl());
	}

	/**
	 * Fetch all computer installed volume agent
	 *
	 * @return the list of computers
	 */
	private List<Computer> _listComputersImpl() {
		if (_computers == null) {
			_computers = _volumeImpl.listComputers();
		}
		return _computers;
	}

	/**
	 * Get information of the specified computer name.dns
	 *
	 * @param dnsName
	 *            computer name include its domain.(eg:win7x64.eucsolution)
	 * @return the computer entity
	 */
	public Computer getComputer(String dnsName) {
		for (Computer computer : _listComputersImpl()) {
			if (dnsName.contains(".")) {
				if (computer.getDomainName().equalsIgnoreCase(dnsName))
					return computer;
			} else {
				computer.getRealName().equalsIgnoreCase(dnsName);
				return computer;
			}
		}
		return null;
	}

	/**
	 * Fetch all user controlled by volume manage
	 *
	 * @return the list of computers
	 */
	private List<User> _listUsersImpl() {

		if (_users == null) {
			_users = _volumeImpl.listUsers();
		}
		return _users;
	}

	/**
	 * Get information of the specified user name.dns
	 *
	 * @param dnsName
	 *            user name include its domain.(eg:jimmy.eucsolution)
	 * @return the user entity
	 */
	public User getUser(String dnsName) {
		for (User user : _listUsersImpl()) {
			if (dnsName.contains(".")) {
				if (user.getDomainName().equalsIgnoreCase(dnsName))
					return user;
			} else {
				if (user.getRealName().equalsIgnoreCase(dnsName)) {
					return user;
				}
			}

		}
		return null;
	}

	/**
	 * Get all available app stacks from volume manager
	 *
	 * @return all app stacks
	 */
	private List<AppStack> _listAppStacksImpl() {
		if (_appStacks == null) {
			List<AppStack> appStacks = _volumeImpl.listAppStacks();
			if (appStacks != null && !appStacks.isEmpty()) {
				_appStacks = new ArrayList<>();
				for (AppStack appStack : appStacks) {
					_appStacks.add(_volumeImpl.getAppStack(appStack.id));
				}
			}
		}
		return _appStacks;
	}

	/**
         * Get all writable volumes from volume manager
         *
         * @return all writable volumes
         */
        private List<Writable> _listWritablesImpl() {
                List<Writable> writables = _volumeImpl.listWritables();
                return writables;
                /*if (_writables == null) {
                        List<Writable> writables = _volumeImpl.listWritables();


                         if (writables != null && !writables.isEmpty()) {

                            writables = new ArrayList<>();
                                for (Writable writable : writables) {
                                    _writables.add(_volumeImpl.getW(writable.id));
                                }
                        }
                }
                return _writables;*/
        }

        public String getWritable4Json(long id) {
            return _volumeImpl.getWritable4Json(id);
        }

        public Writable getWritable(long id) {
           return _volumeImpl.getWritable(id);
        }


	/**
	 *
	 * refresh app stacks
	 *
	 * @return
	 */
	public List<AppStack> refreshAppStacks() {
		List<AppStack> appStacks = _volumeImpl.listAppStacks();
		if (appStacks != null && !appStacks.isEmpty()) {
			_appStacks = new ArrayList<>();
			for (AppStack appStack : appStacks) {
				_appStacks.add(_volumeImpl.getAppStack(appStack.id));
			}
		}
		return _appStacks;
	}

	/**
	 * Get the information of specified app stack by its name
	 *
	 * @param fileLocation
	 *            of app stack identified by storage+path+name
	 * @return the entity of app stack
	 */
	public AppStack getAppStack(String fileLocation) {
		for (AppStack appstack : _listAppStacksImpl()) {
			if (appstack.file_location.equals(fileLocation.trim()))
				return appstack;
		}
		return null;
	}

	/**
	 * Get the available app stacks for computer or user
	 *
	 * @param dnsName
	 *            of user or computer
	 * @param type
	 *            enum{USER,COMPUTER}
	 * @return app stacks which can be attached to the user or computer
	 */
	public List<AppStack> availableAppStacks(String dnsName, Type type) {
		if (type == Type.USER) {
			if (null != getUser(dnsName))
				return _volumeImpl.listAvailableAppStacks4User(getUser(dnsName).getId());
			return null;

		} else {
			if (null != getComputer(dnsName))
				return _volumeImpl.listAvailableAppStacks4Computer(getComputer(dnsName).getId());
			return null;
		}

	}

	/**
	 * Get app stacks which have been attached to the specified computer or user
	 *
	 * @param dnsName
	 *            of user or computer
	 * @param type
	 *            enum{USER,COMPUTER}
	 * @return list, app stacks which have been attached to the user or computer
	 */
	public List<AppStack> assignmentsAppStacks(String dnsName, Type type) {
		if (type == Type.USER) {
			if (null != getUser(dnsName))
				return _volumeImpl.listAssignments4User(getUser(dnsName).getId());
			return null;
		} else {
			if (null != getComputer(dnsName))
				return _volumeImpl.listAssignments4Computer(getComputer(dnsName).getId());

			return null;
		}

	}

	/**
	 * Get the applications a n app stack include
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name)
	 * @return list, applications information
	 */
	public List<Application> listApplication4AppStack(String fileLocation) {
		return _volumeImpl.listApplication4Stack(getAppStack(fileLocation).id);
	}

	/**
	 * Get the entities(user or computer) that the specified app stack has been
	 * attached
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name)
	 * @return entities installed the app stack
	 */
	public List<Entity> listAssignments4AppStack(String fileLocation) {
		return _volumeImpl.listAssignments4Stack(getAppStack(fileLocation).id);
	}

	/**
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name)
	 * @return
	 */
	public List<Entity> listAttachments4AppStack(String fileLocation) {
		return _volumeImpl.listAttachments4Stack(getAppStack(fileLocation).id);
	}

	/**
	 * Get the online entites currently
	 *
	 * @return list, online entities
	 */
	public List<OnlineEntity> listOnlineEntities() {
		return _volumeImpl.listOnlineEntities();
	}

	/**
	 * Attach specified app stack to computers
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name) which want to
	 *            attach
	 * @param dnsNames
	 *            list of computer name.domain
	 * @param real
	 *            true if attach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message assignStack2Computer(String fileLocation, List<String> dnsNames, boolean real) {

		return _volumeImpl.assignAppStack2Computers(getAppStack(fileLocation).id, _convertNames2Ids(dnsNames, Type.COMPUTER), real);
	}

	/**
	 * Detach specified app stack to computers
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name) which want to
	 *            attach
	 * @param dnsNames
	 *            list of computer name.domain
	 * @param real
	 *            true if detach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message unassignStack2Computer(String fileLocation, List<String> dnsNames, boolean real) {
		return _volumeImpl.unassignAppStack4Computers(getAppStack(fileLocation).id, _convertNames2Ids(dnsNames, Type.COMPUTER), real);
	}

	/**
	 * Attach specified app stack to users
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name) which want to
	 *            attach
	 * @param dnsNames
	 *            list of user name.domain
	 * @param real
	 *            true if attach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message assignStack2User(String fileLocation, List<String> dnsNames, boolean real) {

		return _volumeImpl.assignAppStack2Users(getAppStack(fileLocation).id, _convertNames2Ids(dnsNames, Type.USER), real);
	}


	/**
         * Disable a writable volume
         *
         * @param ID for writable volume
         *
         * @return if success, ExcuteResult.resultFlag is 0. if failure,
         *         Massage.ExcuteResult is other value
         */
        public ExcuteResult disableWritableVolume(Long writeVolume_id) {
            return _volumeImpl.disableWritableVolume(writeVolume_id);
        }

        /**
         * Enable a writable volume
         *
         * @param ID for writable volume
         *
         * @return if success, ExcuteResult.resultFlag is 0. if failure,
         *         Massage.ExcuteResult is other value
         */
        public ExcuteResult enableWritableVolume(Long writeVolume_id) {
            return _volumeImpl.enableWritableVolume(writeVolume_id);
        }

        /**
         * Expand a writable volume
         *
         * @param ID for writable volume
         *
         * @return if success, ExcuteResult.resultFlag is RES_SUCCESS. if failure,
         *         this field is other values.
         */
        public ExcuteResult expandWritableVolume(Long writeVolume_id, Long volumeSize) {
           return _volumeImpl.expandWritableVolume(writeVolume_id, volumeSize);
        }

        /**
         * Delete a writable volume
         *
         * @param ID for writable volume
         *
         * @return if success, ExcuteResult.resultFlag is 0. if failure,
         *         Massage.ExcuteResult is other value
         */
        public ExcuteResult deleteWritableVolume(Long writeVolume_id) {
            return _volumeImpl.deleteWritableVolume(writeVolume_id);
        }

	/**
	 * Detach specified app stack from users
	 *
	 * @param fileLocation
	 *            of App Stack(identified by Storage+path+name) which want to
	 *            attach
	 * @param dnsNames
	 *            list of user name.domain
	 * @param real
	 *            true if detach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message unassignStack2User(String fileLocation, List<String> dnsNames, boolean real) {
		return _volumeImpl.unassignAppStack4Users(getAppStack(fileLocation).id, _convertNames2Ids(dnsNames, Type.USER), real);
	}

	/**
	 * Create AppStacks by importing VMDK files from the datastore
	 *
	 * @param dataCenter
	 * @param dataStore
	 * @param path
	 * @param delay
	 * @return
	 */
	public Message importAppStack(String dataCenter, String dataStore, String path, boolean delay) {
		String msg = _volumeImpl.importAppStack(dataCenter, dataStore, path, delay);
		return new Message(Arrays.asList(msg), null);
	}

	/**
	 * Close the connection, release resources
	 */
	@Override
	public void close() throws IOException {
		_volumeImpl.close();
	}
}
