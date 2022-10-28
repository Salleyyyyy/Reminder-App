package com.server.forwarding;

/**
 * Interface for forwarding a message
 */
public interface IForwarding {
	/**
	 * Forwards a (notification) message with the info given
	 * 
	 * @param notificationInfo: Notification info
	 */
	public void forwardMessage(NotificationInfo notificationInfo);
}
