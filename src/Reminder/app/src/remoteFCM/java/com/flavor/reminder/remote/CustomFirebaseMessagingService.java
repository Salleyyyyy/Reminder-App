package com.flavor.reminder.remote;

import android.util.Log;

import com.ba.reminder.notification.NotificationReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.UUID;

/**
 * Entry point when a message (notification) is sent by the FCM backend from Google.
 * This service will be automatically started.
 * For further info: https://stackoverflow.com/questions/43128290/android-notification-with-fcm-who-started-the-firebasemessagingservice
 */
public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Key for getting the notification text in case of a message with no notification type.
     */
    private static final String NOTIFICATION_KEY = "NOTIFICATION";
    /**
     * Tag for logging
     */
    private static final String TAG = CustomFirebaseMessagingService.class.getSimpleName();
    /**
     * Client ID.
     */
    private static UUID clientId;

    /**
     * Sets the client ID.
     *
     * @param clientId: Client ID
     */
    public static void setClientId(UUID clientId) {
        CustomFirebaseMessagingService.clientId = clientId;
    }

    /**
     * Calles when a FCM token is sent by FCM.
     *
     * @param token: FCM token
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // In case of a refreshing, this token has to be sent to the private HTTP forwarding server.
        sendRegistrationToServer(token);
    }

    /**
     * Sends the FCM token to the private HTTP forwarding server.
     *
     * @param token: FCM token needed for client specific pushing
     */
    public static void sendRegistrationToServer(String token) {
        // If a client ID is not given, the private server can't attribute this token to any client
        if (clientId != null) {
            Thread t = new TToken(token, clientId);
            t.start();
        }
    }

    /**
     * Called when a message or notification is received by FCM.
     * The main difference lays in the way it is handled at the client.
     * In case of a notification type,
     * this method is not called and the notification is handled automatically when the app is in the foreground.
     * In case of a message type, this method is always called even if the app is in the foreground.
     * In regards of the push notification, this difference is not important. In this implementation we mainly use
     * messages in order to log the arrival of the message in this method which is impossible when the notification is handled automatically
     * by the system.
     *
     * @param remoteMessage Message object received from FCM
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message;
        Map<String, String> data = remoteMessage.getData();

        // 1. Possibility: RemoteMessage can contain a notification (notification type)
        // Only called when app is in the background
        RemoteMessage.Notification notification;
        if ((notification = remoteMessage.getNotification()) != null && (message = notification.getBody()) != null) {
            NotificationReceiver.triggerNotification(CustomFirebaseMessagingService.this, message);
        }
        // 2. Possibility: RemoteMessage can contain data for notification (message type)
        else if (data.containsKey(NOTIFICATION_KEY)) {
            NotificationReceiver.triggerNotification(CustomFirebaseMessagingService.this, data.get(NOTIFICATION_KEY));
        }
        else {
            Log.e(TAG, "RemoteMessage has no info for a notification");
        }
    }
}
