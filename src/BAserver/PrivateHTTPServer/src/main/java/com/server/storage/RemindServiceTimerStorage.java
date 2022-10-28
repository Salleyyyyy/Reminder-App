package com.server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.server.model.RemindService;

/**
 * This storage saves all remind services with their corresponding timer for
 * retrieving.
 */
public class RemindServiceTimerStorage {

	/**
	 * HashMap of alle remind services and their corresponding timers
	 */
	private HashMap<RemindService, Timer> registeredRemindServices = new HashMap<>();

	/**
	 * Empty constructor.
	 */
	public RemindServiceTimerStorage() {
	}

	/**
	 * Get all the registered remind services.
	 * 
	 * @return List of all registered remind services
	 */
	public List<RemindService> getRegisteredRemindServices() {
		return new ArrayList<>(registeredRemindServices.keySet());
	}

	/**
	 * Checks whether this remind service is saved in the storage.
	 * 
	 * @param remindService: Remind service saved possibly in the storage
	 * @return true if remind service is saved, otherwise false
	 */
	public boolean existsRemindService(RemindService remindService) {
		return retrieveRemindService(remindService) != null;
	}

	/**
	 * Gets the corresponding timer for this remind service.
	 * 
	 * @param remindService: Remind service for his corresponding timer
	 * @return Timer of the remind service given
	 */
	public Timer getTimerForRemindService(RemindService remindService) {
		return registeredRemindServices.get(retrieveRemindService(remindService));
	}

	/**
	 * Removes a remind service from this storage.
	 * 
	 * @param remindService: Remind service that is removed from this storage.
	 */
	public void removeRemindService(RemindService remindService) {
		registeredRemindServices.remove(retrieveRemindService(remindService));
	}

	/**
	 * Saves a remind service with his corresponding (already scheduled) timer.
	 * 
	 * @param remindService:  Remind service
	 * @param scheduledTimer: Already scheduled timer
	 */
	public void registerForRemindService(RemindService remindService, Timer scheduledTimer) {
		registeredRemindServices.put(remindService, scheduledTimer);
	}

	/**
	 * Retrieves the remind service for the remind service given. This method
	 * implements a work around due to equality check issues with hashing.
	 * 
	 * @param expectedRemindObject: Expected remind service in the storage
	 * @return Remind service in the storage or null if not saved
	 */
	private RemindService retrieveRemindService(RemindService expectedRemindObject) {
		if (expectedRemindObject == null)
			return null;
		for (RemindService key : registeredRemindServices.keySet()) {
			if (key.equals(expectedRemindObject))
				return key;
		}
		return null;
	}

}
