package com.vmware.eucenablement.horizontoolset.av.api.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.AppStack;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Application;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Computer;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Entity;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.ExcuteResult;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Message;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.OnlineEntity;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.User;
import com.vmware.eucenablement.horizontoolset.av.api.pojo.Writable;
import com.vmware.eucenablement.horizontoolset.av.api.util.HTTPMethod;
import com.vmware.eucenablement.horizontoolset.av.api.util.VolumeHelper;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this VolumeImpl play a agent role to help us communicate with cloud volume,
 *
 * specificly help us get the information from the volume manage
 *
 * @author Xiaoning
 *
 */
public class VolumeImpl implements Closeable {

	private static Logger LOG = Logger.getLogger(VolumeImpl.class);
        //private static Logger LOG = null;
	private VolumeHelper volumeHelper;

	/**
	 *
	 * @param server
	 *            the address of server want to connection
	 * @throws Exception
	 *             if server fromate is illegal
	 */
	public VolumeImpl(String server) throws Exception {
		LOG.debug(new Date().toString() + ": create VolumeImppl");
		this.volumeHelper = new VolumeHelper(server);
	}

	/**
	 * login to the specified server and then keep connection session
	 *
	 * @param params
	 *            the login parameters<key,value> format
	 * @return true if success, false if failure
	 */
	public boolean login(Map<String, String> params) {
		try {
			volumeHelper.requestProcess(params, "login", HTTPMethod.POST);
			if (!("".equals(volumeHelper.getCsrf_TOKEN()) || null == volumeHelper.getCsrf_TOKEN() || "".equals(volumeHelper.getSession_ID()) || null == volumeHelper
					.getSession_ID())) {
				return true;
			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": login failed:" + e);
		}
		return false;
	}

	/**
         * get all writable volumes from volume manager
         *
         * @return all writable volumes
         */
	public List<Writable> listWritables() {
	    List<Writable> writableList = null;
	    try {
	        String result = volumeHelper.requestProcess(null, "cv_api/writables", HTTPMethod.GET);

	        if (result.contains("datastores") && result.contains("writable_volumes")) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(result).getAsJsonObject();
                    JsonObject datastores = jsonObject.getAsJsonObject("datastores");
                    JsonArray jsonWritables = datastores.getAsJsonArray("writable_volumes");

                    writableList = gson.fromJson(jsonWritables, new TypeToken<List<Writable>>() {
                    }.getType());

            } else {
                    LOG.warn(new Date().toString() + ": can not get app stacks, try latter");
            }
	    } catch (Exception e) {
                LOG.error(new Date().toString() + ": can not get writables, try latter:" + e);
            }

	    return writableList;
	}

	public String getWritable4Json(long id) {
	    String result = "";
	    try {
                result = volumeHelper.requestProcess(null, "cv_api/writables/" + id, HTTPMethod.GET);
	    } catch(Exception e) {
	        LOG.error(new Date().toString() + ": can not get writable volume, try latter:" + e);
	    }

	    return result;
	}

	public Writable getWritable(long id) {
	    Writable writableDetail = null;
	    try {
                String result = volumeHelper.requestProcess(null, "cv_api/writables/" + id, HTTPMethod.GET);

                if (result.contains("writable")) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(result).getAsJsonObject();
                    JsonObject jsonWritable = jsonObject.getAsJsonObject("writable");
                    writableDetail = gson.fromJson(jsonWritable, Writable.class);
                }

            } catch(Exception e) {
                LOG.error(new Date().toString() + ": can not get writable volume, try latter:" + e);
            }

	    return writableDetail;
	}

	/**
	 * get all available app stacks from volume manager
	 *
	 * @return all app stacks
	 */
	public List<AppStack> listAppStacks() {
		List<AppStack> appStackList = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/appstacks", HTTPMethod.GET);
			if (result.contains("currentappstacks") && result.contains("appstacks")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonObject currentappstacks = jsonObject.getAsJsonObject("currentappstacks");
				JsonArray appstacks = currentappstacks.getAsJsonArray("appstacks");

				appStackList = gson.fromJson(appstacks, new TypeToken<List<AppStack>>() {
				}.getType());

			} else {
				LOG.warn(new Date().toString() + ": can not get app stacks, try latter");
			}

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get app stacks, try latter:" + e);
		}
		return appStackList;
	}

	public AppStack getAppStack(long id) {
		AppStack appStackDetail = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/appstacks/" + id, HTTPMethod.GET);
			if (result.contains("appstack")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonObject appstack = jsonObject.getAsJsonObject("appstack");
				appStackDetail = gson.fromJson(appstack, AppStack.class);
			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get app stacks, try latter:" + e);
		}
		return appStackDetail;
	}

	/**
	 * fetch all computer installed volume agent
	 *
	 * @return the list of computers
	 */
	public List<Computer> listComputers() {
		List<Computer> computers = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/computers", HTTPMethod.GET);
			Gson gson = new Gson();
			computers = gson.fromJson(result, new TypeToken<List<Computer>>() {
			}.getType());
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get computers: " + e);
		}
		return computers;
	}

	/**
	 * get the available app stacks for computer
	 *
	 * @param id
	 *            of computer
	 * @return app stacks which can be attached to the computer
	 */
	public List<AppStack> listAvailableAppStacks4Computer(long id) {
		List<AppStack> tmp = null;
		List<AppStack> appStackList = new ArrayList<>();
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/computers/" + id + "/assignable_appstacks", HTTPMethod.GET);
			if (result.contains("appstacks")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonArray appstacks = jsonObject.getAsJsonArray("appstacks");
				tmp = gson.fromJson(appstacks, new TypeToken<List<AppStack>>() {
				}.getType());
				for (int i = 0; i < tmp.size(); i++) {
					if (tmp.get(i).status.equals("assigned") && tmp.get(i).assigned) {
						continue;
					} else {
						appStackList.add(tmp.get(i));
					}
				}
			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get available appstacks for computer " + id + ": " + e);
		}
		return appStackList;
	}

	/**
	 * get app stacks which have been attached to the specified computer
	 *
	 * @param id
	 *            of computer
	 * @return list, app stacks which have been attached to the computer
	 */
	public List<AppStack> listAssignments4Computer(long id) {
		List<AppStack> tmp = null;
		List<AppStack> appStackList = new ArrayList<>();
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/computers/" + id + "/assignable_appstacks", HTTPMethod.GET);
			if (result.contains("appstacks")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonArray appstacks = jsonObject.getAsJsonArray("appstacks");
				tmp = gson.fromJson(appstacks, new TypeToken<List<AppStack>>() {
				}.getType());
				for (int i = 0; i < tmp.size(); i++)
					if (tmp.get(i).status.equals("enabled") && !tmp.get(i).assigned)
						continue;
					else
						appStackList.add(tmp.get(i));

			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get assignments for computer " + id + ": " + e);
		}
		return appStackList;
	}

	/**
	 * fetch all users controlled by volume manager
	 *
	 * @return the list of users
	 */
	public List<User> listUsers() {
		List<User> users = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/users", HTTPMethod.GET);
			Gson gson = new Gson();
			users = gson.fromJson(result, new TypeToken<List<User>>() {
			}.getType());
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get users :" + e);
		}
		return users;
	}

	/**
	 * get the available app stacks for user
	 *
	 * @param id
	 *            of user
	 * @return app stacks which can be attached to the user
	 */
	public List<AppStack> listAvailableAppStacks4User(long id) {
		List<AppStack> tmp = null;
		List<AppStack> appStackList = new ArrayList<>();
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/users/" + id + "/assignable_appstacks", HTTPMethod.GET);
			if (result.contains("appstacks")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonArray appstacks = jsonObject.getAsJsonArray("appstacks");
				tmp = gson.fromJson(appstacks, new TypeToken<List<AppStack>>() {
				}.getType());
				for (int i = 0; i < tmp.size(); i++)
					if (tmp.get(i).status.equals("assigned") && tmp.get(i).assigned)
						continue;
					else
						appStackList.add(tmp.get(i));

			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get available appstacks for user " + id + ": " + e);
		}
		return appStackList;
	}

	/**
	 * get app stacks which have been attached to the specified user
	 *
	 * @param id
	 *            of user
	 * @return list, app stacks which have been attached to the user
	 */
	public List<AppStack> listAssignments4User(long id) {
		List<AppStack> tmp = null;
		List<AppStack> appStackList = new ArrayList<>();
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/users/" + id + "/assignable_appstacks", HTTPMethod.GET);
			if (result.contains("appstacks")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonArray appstacks = jsonObject.getAsJsonArray("appstacks");
				tmp = gson.fromJson(appstacks, new TypeToken<List<AppStack>>() {
				}.getType());
				for (int i = 0; i < tmp.size(); i++)
					if (tmp.get(i).status.equals("enabled") && !tmp.get(i).assigned)
						continue;
					else
						appStackList.add(tmp.get(i));

			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get assignments for user " + id + ": " + e);
		}
		return appStackList;
	}

	/**
	 * get the applications an app stack include
	 *
	 * @param id
	 *            of app stack
	 * @return list, applications information
	 */
	public List<Application> listApplication4Stack(long id) {
		List<Application> applicationList = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/appstacks/" + id + "/applications?id=" + id, HTTPMethod.GET);
			if (result.contains("applications")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonArray appstacks = jsonObject.getAsJsonArray("applications");
				applicationList = gson.fromJson(appstacks, new TypeToken<List<Application>>() {
				}.getType());
			}
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get applications for app stack " + id + ": " + e);
		}
		return applicationList;
	}

	/**
	 * get the entities(user or computer) that the specified app stack has been
	 * attached
	 *
	 * @param id
	 *            of app stack
	 * @return entities installed the app stack
	 */
	public List<Entity> listAssignments4Stack(long id) {
		List<Entity> entityList = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/appstacks/" + id + "/assignments?id=" + id, HTTPMethod.GET);

			Gson gson = new Gson();
			entityList = gson.fromJson(result, new TypeToken<List<Entity>>() {
			}.getType());
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get assignments for app stack " + id + ": " + e);
		}
		return entityList;
	}

	/**
	 * @param
	 * @return
	 */
	public List<Entity> listAttachments4Stack(long id) {
		List<Entity> entityList = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/appstacks/" + id + "/attachments?id=" + id, HTTPMethod.GET);

			Gson gson = new Gson();
			entityList = gson.fromJson(result, new TypeToken<List<Entity>>() {
			}.getType());

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get attachments for app stack " + id + ": " + e);
		}
		return entityList;
	}

	/**
	 * @param
	 * @return
	 */
	public String listMachines() {
		return null;
	}

	/**
	 * get the online entites currently
	 *
	 * @return list, online entities
	 */
	public List<OnlineEntity> listOnlineEntities() {
		List<OnlineEntity> onlineEntityList = null;
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/online_entities", HTTPMethod.GET);
			if (result.contains("online") && result.contains("records")) {
				Gson gson = new Gson();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(result).getAsJsonObject();
				JsonObject onlines = jsonObject.getAsJsonObject("online");
				JsonArray records = onlines.getAsJsonArray("records");
				onlineEntityList = gson.fromJson(records, new TypeToken<List<OnlineEntity>>() {
				}.getType());
			}

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get online entity: " + e);
		}
		return onlineEntityList;
	}

	/**
	 * @param
	 * @return
	 */
	public boolean assign(Map<String, String> map_assignParameters) {
		map_assignParameters.put("action_type", "assign");
		return false;
	}

	/**
	 * attach specified app stack to computers
	 *
	 * @param app_stack_id
	 *            id of app stack which want to attach
	 * @param computer_ids
	 *            list of computer id
	 * @param real
	 *            true if attach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message assignAppStack2Computers(Long app_stack_id, List<Long> computer_ids, boolean real) {
		Message msg = null;
		Map<String, String> map_assignParameters = new HashMap<>();
		map_assignParameters.put("action_type", "Assign");
		map_assignParameters.put("mount_prefix", "");

		map_assignParameters.put("ids[]", app_stack_id + "");
		for (int i = 0; i < computer_ids.size(); i++) {
			map_assignParameters.put("assignments[" + i + "][entity_type]", "Computer");
			map_assignParameters.put("assignments[" + i + "][id]", computer_ids.get(i) + "");
		}
		map_assignParameters.put("rtime", real + "");
		try {
			String result = volumeHelper.requestProcess(map_assignParameters, "cv_api/assignments", HTTPMethod.POST);
			Gson gson = new Gson();
			if (result.contains("successes")) {
				msg = gson.fromJson(result, Message.class);
			} else if (result.contains("warning")) {
				msg = gson.fromJson(result, Message.class);
			} else {
				LOG.warn(new Date().toString() + ": can not assign app stack to computers");
			}

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not assign app stack to computers: " + e);
		}
		return msg;
	}

	/**
	 * attach specified app stack to users
	 *
	 * @param app_stack_id
	 *            id of app stack which want to attach
	 * @param user_ids
	 *            list of user id
	 * @param real
	 *            true if attach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message assignAppStack2Users(Long app_stack_id, List<Long> user_ids, boolean real) {
		Message msg = null;
		Map<String, String> map_assignParameters = new HashMap<>();
		map_assignParameters.put("action_type", "Assign");
		map_assignParameters.put("mount_prefix", "");

		map_assignParameters.put("ids[]", app_stack_id + "");
		for (int i = 0; i < user_ids.size(); i++) {
			map_assignParameters.put("assignments[" + i + "][entity_type]", "User");
			map_assignParameters.put("assignments[" + i + "][id]", user_ids.get(i) + "");
		}
		map_assignParameters.put("rtime", real + "");
		try {
			String result = volumeHelper.requestProcess(map_assignParameters, "cv_api/assignments", HTTPMethod.POST);
			Gson gson = new Gson();
			if (result.contains("successes"))
				msg = gson.fromJson(result, Message.class);
			else if (result.contains("warning"))

				msg = gson.fromJson(result, Message.class);
			else
				LOG.warn(new Date().toString() + ": can not assign app stack to users");

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not assign app stack to users: " + e);
		}
		return msg;
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
	    ExcuteResult resultExcuted = new ExcuteResult();
	    Map<String, String> map_disableParameters = new HashMap<>();
	    map_disableParameters.put("volumes[]", writeVolume_id.toString());

	    try {
                String result = volumeHelper.requestProcess(map_disableParameters, "cv_api/writables/disable", HTTPMethod.POST);
                if (result.contains("was disabled")) {
                    resultExcuted.resultFlag = ExcuteResult.RES_SUCCESS;
                    resultExcuted.message = result;
                }
                else {
                        LOG.warn(new Date().toString() + ": can not disable this volume - id: " + writeVolume_id + ". " + result);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = result;

                }
	    } catch (Exception e) {
                        LOG.error(new Date().toString() + ": can not disable this volume: " + e);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = "Can not disable this volume: " + e;
                }
                return resultExcuted;
	}

	/**
         * enable a writable volume
         *
         * @param ID for writable volume
         *
         * @return if success, ExcuteResult.resultFlag is 0. if failure,
         *         Massage.ExcuteResult is other value
         */
        public ExcuteResult enableWritableVolume(Long writeVolume_id) {
            ExcuteResult resultExcuted = new ExcuteResult();
            Map<String, String> map_enableParameters = new HashMap<>();
            map_enableParameters.put("volumes[]", writeVolume_id.toString());

            try {
                String result = volumeHelper.requestProcess(map_enableParameters, "cv_api/writables/enable", HTTPMethod.POST);
                if (result.contains("was enabled")) {
                    resultExcuted.resultFlag = ExcuteResult.RES_SUCCESS;
                    resultExcuted.message = result;
                }
                else {
                        LOG.warn(new Date().toString() + ": can not enable this volume - id: " + writeVolume_id + ". " + result);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = result;

                }
            } catch (Exception e) {
                        LOG.error(new Date().toString() + ": can not enable this volume: " + e);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = "Can not enable this volume: " + e;
                }
                return resultExcuted;
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

            ExcuteResult resultExcuted = new ExcuteResult();
            String msgDescription = "";

            Map<String, String> map_expandParameters = new HashMap<String, String>();
            map_expandParameters.put("size_mb", volumeSize.toString());
            map_expandParameters.put("volumes[]", writeVolume_id.toString());

            try {
                    String result = volumeHelper.requestProcess(map_expandParameters, "cv_api/writables/grow", HTTPMethod.POST);
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(result).getAsJsonObject();
                    JsonArray jsonSuccesses = jsonObject.getAsJsonArray("successes");
                    JsonArray jsonWarnings = jsonObject.getAsJsonArray("warnings");
                    JsonArray jsonErrors = jsonObject.getAsJsonArray("errors");


                    if((jsonSuccesses != null) && (jsonSuccesses.size() > 0)) {
                        resultExcuted.resultFlag = ExcuteResult.RES_SUCCESS;
                        msgDescription += jsonSuccesses.getAsString();
                    } else if ((jsonWarnings != null) && (jsonWarnings.size() > 0)) {
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        msgDescription += jsonWarnings.getAsString();

                    } else if ((jsonErrors != null) && (jsonErrors.size() > 0)) {
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        msgDescription += jsonErrors.getAsString();
                    } else {
                        LOG.warn(new Date().toString() + ": can not expand writable volumes");
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        msgDescription += "Can not expand writable volumes.";
                    }

            } catch (Exception e) {
                    LOG.error(new Date().toString() + ": can not unassign app stack to users: " + e);
                    resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                    msgDescription = "Can not expand writable volumes. Exceptions: " + e;
            }

            resultExcuted.message = msgDescription;

            return resultExcuted;
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
            ExcuteResult resultExcuted = new ExcuteResult();
            Map<String, String> map_deleteParameters = new HashMap<>();
            map_deleteParameters.put("volumes[]", writeVolume_id.toString());

            try {
                String result = volumeHelper.requestProcess(map_deleteParameters, "cv_api/volumes/delete", HTTPMethod.POST);
                if (result.contains("success")) {
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(result).getAsJsonObject();

                    resultExcuted.resultFlag = ExcuteResult.RES_SUCCESS;
                    resultExcuted.message = jsonObject.get("success").getAsString();
                }
                else {
                        LOG.warn(new Date().toString() + ": failed to delete this volume - id: " + writeVolume_id + ". " + result);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = "Failed to delete this volume(id: " + writeVolume_id + "). " + "Reason: "+ result;

                }
            } catch (Exception e) {
                        LOG.error(new Date().toString() + ": failed to delete this volume(" + writeVolume_id +"): " + e);
                        resultExcuted.resultFlag = ExcuteResult.RES_GENERAL_FAILURE;
                        resultExcuted.message = "Failed to delete this volume(id: " + writeVolume_id +"). Reason: " + e;
                }
                return resultExcuted;
        }



	/**
	 * @param
	 * @return
	 */
	public boolean unassign(Map<String, String> map_assignParameters) {
		return false;
	}

	/**
	 * detach specified app stack from users
	 *
	 * @param app_stack_id
	 *            id of app stack which want to detach
	 * @param user_ids
	 *            list of user id
	 * @param real
	 *            true if detach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message unassignAppStack4Users(Long app_stack_id, List<Long> user_ids, boolean real) {
		Message msg = null;
		Map<String, String> map_assignParameters = new HashMap<String, String>();
		map_assignParameters.put("action_type", "Unassign");
		map_assignParameters.put("mount_prefix", "");

		map_assignParameters.put("ids[]", app_stack_id + "");
		for (int i = 0; i < user_ids.size(); i++) {
			map_assignParameters.put("assignments[" + i + "][entity_type]", "User");
			map_assignParameters.put("assignments[" + i + "][id]", user_ids.get(i) + "");
		}
		map_assignParameters.put("rtime", real + "");
		try {
			String result = volumeHelper.requestProcess(map_assignParameters, "cv_api/assignments", HTTPMethod.POST);
			Gson gson = new Gson();
			if (result.contains("successes"))
				msg = gson.fromJson(result, Message.class);
			else if (result.contains("warning"))
				msg = gson.fromJson(result, Message.class);
			else
				LOG.warn(new Date().toString() + ": can not unassign app stack to users");

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not unassign app stack to users: " + e);
		}
		return msg;
	}

	/**
	 * detach specified app stack to computers
	 *
	 * @param app_stack_id
	 *            id of app stack which want to detach
	 * @param computer_ids
	 *            list of computer id
	 * @param real
	 *            true if detach immediately, false if next login or after
	 *            reboot
	 * @return if success, Massage.successes is not null. if failure,
	 *         Massage.warn is not null
	 */
	public Message unassignAppStack4Computers(Long app_stack_id, List<Long> computer_ids, boolean real) {
		Message msg = null;
		Map<String, String> map_assignParameters = new HashMap<String, String>();
		map_assignParameters.put("action_type", "Unassign");
		map_assignParameters.put("mount_prefix", "");

		map_assignParameters.put("ids[]", app_stack_id + "");
		for (int i = 0; i < computer_ids.size(); i++) {
			map_assignParameters.put("assignments[" + i + "][entity_type]", "Computer");
			map_assignParameters.put("assignments[" + i + "][id]", computer_ids.get(i) + "");
		}
		map_assignParameters.put("rtime", real + "");
		try {
			String result = volumeHelper.requestProcess(map_assignParameters, "cv_api/assignments", HTTPMethod.POST);
			Gson gson = new Gson();
			if (result.contains("successes"))
				msg = gson.fromJson(result, Message.class);
			else if (result.contains("warning"))
				msg = gson.fromJson(result, Message.class);
			else
				LOG.warn(new Date().toString() + ": can not unassign app stack to computers");

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not unassign app stack to computers: " + e);
		}
		return msg;
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
	public String importAppStack(String dataCenter, String dataStore, String path, boolean delay) {
		Map<String, String> map_Parameters = new HashMap<String, String>();
		map_Parameters.put("datacenter", dataCenter);
		map_Parameters.put("datastore", dataStore);

		map_Parameters.put("path", path);
		map_Parameters.put("delay", delay + "");
		String result = null;
		try {
			result = volumeHelper.requestProcess(map_Parameters, "cv_api/volumes/import", HTTPMethod.POST);
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not import app stack: " + e);
		}
		return result;
	}

	/**
	 * @return String of app volume version
	 */
	public String getVersion() {
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/version", HTTPMethod.GET);
			if (result.contains("version"))
				return result;
			else
				LOG.warn(new Date().toString() + ": can not get volume version");

		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get volume version" + ": " + e);
		}
		return null;
	}

	/**
	 * @return String of app volume licenses
	 *
	 */
	public String getLicense() {
		try {
			String result = volumeHelper.requestProcess(null, "cv_api/license_usage", HTTPMethod.GET);
			if (result.contains("licenses"))
				return result;
			else
				LOG.warn(new Date().toString() + ": can not get volume licenses");
		} catch (Exception e) {
			LOG.error(new Date().toString() + ": can not get volume licenses" + ": " + e);
		}

		return null;
	}

	/**
	 * close the connection
	 */
	public void close() {
		try {
			volumeHelper.requestProcess(null, "logout", HTTPMethod.GET);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				volumeHelper.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
