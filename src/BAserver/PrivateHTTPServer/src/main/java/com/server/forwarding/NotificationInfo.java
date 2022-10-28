package com.server.forwarding;

/**
 * This class contains all necessary info for sending a notification.
 */
public class NotificationInfo {

	/**
	 * Notification text.
	 */
	private String notificationMessage;
	/**
	 * High priority or not
	 */
	private boolean highPriority;

	/**
	 * Constructor.
	 * 
	 * @param notificationMessage: Notification text
	 * @param highPriority:        True if notification has a high priority,
	 *                             otherwise false
	 */
	public NotificationInfo(String notificationMessage, boolean highPriority) {
		this.notificationMessage = notificationMessage;
		this.highPriority = highPriority;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public boolean hasHighPriority() {
		return highPriority;
	}
}
