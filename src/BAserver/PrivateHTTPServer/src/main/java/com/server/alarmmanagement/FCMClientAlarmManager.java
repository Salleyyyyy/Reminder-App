package com.server.alarmmanagement;

import java.util.UUID;

import com.google.firebase.FirebaseApp;
import com.server.forwarding.FCMForwarding;

/**
 * This class inherits all functionalities of a general client alarmmanager and
 * is specialized for FCM Push or forwarding a message (notification) to the FCM
 * Back-End.
 */
public class FCMClientAlarmManager extends ClientAlarmManager {

	/**
	 * FCMForwarding instance for forwarding a push request
	 */
	private FCMForwarding fcmForwarding;

	/**
	 * This thread is actively polling for notifications to push them to the FCM
	 * Back-End from Google
	 */
	private Thread waitForForwardingMessageToFCM = new Thread(() -> {
		while (true) {
			while (!anyNotificationAvailable())
				;
			fcmForwarding.forwardMessage(getNotificationInfo());
		}
	});

	/**
	 * Constructor.
	 * 
	 * @param clientId:    Client ID of client
	 * @param firebaseApp: Firebase App for Firebase configuration
	 */
	public FCMClientAlarmManager(UUID clientId, FirebaseApp firebaseApp) {
		super(clientId);
		fcmForwarding = new FCMForwarding(firebaseApp);
		waitForForwardingMessageToFCM.start();
	}

	/**
	 * Sets the FCM Token for this client or refreshes it
	 * 
	 * @param registrationToken: FCM Token
	 */
	public void setToken(String registrationToken) {
		fcmForwarding.setToken(registrationToken);
	}
}
