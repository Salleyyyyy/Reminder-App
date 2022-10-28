package com.server.storage;

import java.util.ArrayList;
import java.util.List;

import com.server.model.RemindService;

/**
 * This class stores all remind services that are available for a user. Those
 * remind services are added or removed from this storage.
 */
public class NotificationStorage {

	private List<RemindService> notificationList = new ArrayList<>();

	/**
	 * Empty constructor.
	 */
	public NotificationStorage() {
	}

	/**
	 * Checks whether there is a notification available in the storage.
	 * 
	 * @return true is there is any notification that must be sent, otherwise false
	 */
	public synchronized boolean anyNotificationAvalaible() {
		return !notificationList.isEmpty();
	}

	/**
	 * Adds a new remind service to this storage.
	 * 
	 * @param remindService: Remind service to get the notification text for later
	 *                       push
	 */
	public synchronized void addNewNotificationMessage(RemindService remindService) {
		notificationList.add(remindService);
	}

	/**
	 * Removes a remind service (notification) from this storage.
	 * 
	 * @return Remind service that is removed
	 */
	public synchronized RemindService removeNotificationForSending() {
		return notificationList.remove(0);
	}

	/**
	 * Checks whether the storage contains this remind service
	 * 
	 * @param remindService: Remind service possibly in the storage
	 * @return true if this remind service is contained in the storage, otherwise
	 *         false
	 */
	public synchronized boolean contains(RemindService remindService) {
		return notificationList.contains(remindService);
	}
}
