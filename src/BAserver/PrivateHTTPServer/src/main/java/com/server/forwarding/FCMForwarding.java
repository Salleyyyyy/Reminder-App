package com.server.forwarding;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.AndroidConfig.Priority;

/**
 * This class is responsible for forwarding a push request to the FCM Back-End
 * from Google.
 */
public class FCMForwarding implements IForwarding {

	/**
	 * Title of the notification.
	 */
	private static final String NOTIFICATION_CONTENT_TITLE = "Reminder App Notification";
	/**
	 * FCM Messages contain a notification text that can be accessed with this key.
	 */
	private static final String NOTIFICATION_KEY = "NOTIFICATION";
	/**
	 * Extra that is appended for FCM Messages.
	 */
	private static final String APPEND_FCM_NOTIFICATION_MESSAGE = " (send via FCM)";

	/**
	 * Registration Token from FCM.
	 */
	private String registrationToken;

	/**
	 * FirebaseMessaging instance for sending push requests to FCM
	 */
	private FirebaseMessaging firebaseMessaging;

	/**
	 * Constructor.
	 * 
	 * @param firebaseApp: Firebase App instance with all configurations
	 */
	public FCMForwarding(FirebaseApp firebaseApp) {
		this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
	}

	/**
	 * Sets the FCM Token for this client or refreshes it
	 * 
	 * @param registrationToken: FCM Token
	 */
	public void setToken(String registrationToken) {
		this.registrationToken = registrationToken;
	}

	/**
	 * Forwards a message (notification) to the specific client with the FCM token.
	 * 
	 * @param notificationInfo: Notification info for pushing a notification
	 */
	@Override
	public void forwardMessage(NotificationInfo notificationInfo) {
		if (registrationToken == null) {
			printError("Registration token missing!");
			return;
		}
		// Send a message to the device corresponding with the registration token
		forwardMessageToFCM(notificationInfo.getNotificationMessage(), notificationInfo.hasHighPriority());
	}

	/**
	 * Forwards a message (notification) to the FCM Back-End from Google with the
	 * priority given. High priority messages will reach the client even in doze
	 * mode. Normal prioritized messages are affected from Doze Mode.
	 * 
	 * @param notificationMessage: Notification message
	 * @param highPriority:        True for high priority messages otherwise false
	 *                             for normal priority messages
	 */
	private void forwardMessageToFCM(String notificationMessage, boolean highPriority) {
		Message message;
		if (highPriority) {
			message = createFCMMessageWithHighPriority(notificationMessage + APPEND_FCM_NOTIFICATION_MESSAGE);
		} else {
			message = createFCMMessageWithNormalPriority(notificationMessage + APPEND_FCM_NOTIFICATION_MESSAGE);
		}

		try {
			firebaseMessaging.send(message);
			printInfo("Sent message to FCM: " + notificationMessage);
		} catch (FirebaseMessagingException e) {
			printException(e);
			return;
		}
	}

	/**
	 * Creates a FCM message with normal priority with this notification message and
	 * client's FCM token.
	 * 
	 * @param notificationMessage: Notification text
	 * @return Normal prioritized FCM Message with notification text for client with
	 *         FCM Token
	 */
	private Message createFCMMessageWithNormalPriority(String notificationMessage) {
		return Message.builder().putData(NOTIFICATION_KEY, notificationMessage).setToken(registrationToken)
				.setAndroidConfig(createAndroidConfigWithNormalMessagePriority()).build();
		/**
		 * return
		 * Message.builder().setNotification(createFCMNotification(notificationMessage)).setToken(registrationToken)
		 * .setAndroidConfig(createAndroidConfigWithNormalMessagePriority()).build();
		 **/
	}

	/**
	 * Creates a FCM message with high priority with this notification message and
	 * client's FCM token.
	 * 
	 * @param notificationMessage: Notification text
	 * @return High prioritized FCM Message with notification text for client with
	 *         FCM Token
	 */
	private Message createFCMMessageWithHighPriority(String notificationMessage) {
		return Message.builder().putData(NOTIFICATION_KEY, notificationMessage).setToken(registrationToken)
				.setAndroidConfig(createAndroidConfigWithHighMessagePriority()).build();
		/**
		 * return
		 * Message.builder().setNotification(createFCMNotification(notificationMessage)).setToken(registrationToken)
		 * .setAndroidConfig(createAndroidConfigWithHighMessagePriority()).build();
		 **/
	}

	/**
	 * Created Android configurations for high priority messages.
	 * 
	 * @return Android configurations for a high priority messaging.
	 */
	private AndroidConfig createAndroidConfigWithHighMessagePriority() {
		return createAndroidConfigWithPriority(Priority.HIGH);
	}

	/**
	 * Created Android configurations for normal priority messages.
	 * 
	 * @return Android configurations for a normal priority messaging.
	 */
	private AndroidConfig createAndroidConfigWithNormalMessagePriority() {
		return createAndroidConfigWithPriority(Priority.NORMAL);
	}

	/**
	 * Created Android configurations for specified priority (Normal or High).
	 * 
	 * @param priority: Priority level (Normal or High)
	 * @return Android configurations for a messaging with specified priority.
	 */
	private AndroidConfig createAndroidConfigWithPriority(Priority priority) {
		return AndroidConfig.builder().setPriority(priority).build();
	}

	/**
	 * Not used.
	 */
	private Notification createFCMNotification(String notificationMessage) {
		return Notification.builder().setBody(notificationMessage).setTitle(NOTIFICATION_CONTENT_TITLE).build();
	}

	/**
	 * Print exception to console.
	 * 
	 * @param e: Exception
	 */
	private void printException(Exception e) {
		System.out.println("EXCEPTION: " + e.getMessage());
	}

	/**
	 * Print info to console.
	 * 
	 * @param infoMessage: Info message
	 */
	private void printInfo(String infoMessage) {
		System.out.println("INFO: " + infoMessage);
	}

	/**
	 * Print an error message to console.
	 * 
	 * @param errorMessage: Message which contains an error
	 */
	private void printError(String errorMessage) {
		System.out.println("ERROR: " + errorMessage);
	}
}
