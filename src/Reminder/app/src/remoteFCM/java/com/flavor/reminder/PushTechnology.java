package com.flavor.reminder;

import android.content.Context;
import android.util.Log;

import com.ba.reminder.interfaces.IPushTechnology;
import com.ba.reminder.logging.FlavorLogger;
import com.ba.reminder.remote.HTTPPushTechnology;
import com.flavor.reminder.remote.CustomFirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.server.model.RemindService;

import java.util.List;

/**
 * FCM Push solution.
 */
public class PushTechnology extends HTTPPushTechnology implements IPushTechnology {

    /**
     * File name to local storage.
     */
    public static final String SHARED_PREF_FILENAME = "remoteFCM";
    /**
     * Port of the private HTTP forwarding server.
     */
    public static final int PORT = 81;
    /**
     * Logger.
     */
    private final FlavorLogger flavorLogger;

    /**
     * Constructor.
     *
     * @param context: Context of application
     */
    public PushTechnology(Context context) {
        super(context, SHARED_PREF_FILENAME);
        CustomFirebaseMessagingService.setClientId(clientId);
        // Send FCM token to private HTTP forwarding server
        sendRegistrationTokenToServer();
        flavorLogger = new FlavorLogger();
    }

    @Override
    public void requestForRemindService(RemindService remindService) {
        // Logging for registration of a remind service
        flavorLogger.infoForNotification(remindService.getRemindType().toString());
        flavorLogger.infoForNotificationShouldArriveAt(remindService.scheduleTimeToTriggerReminding());

        remindService.setRemind(true);
        sendRemindService(remindService);
    }

    @Override
    public void cancelRemindService(RemindService remindService) {
        remindService.setRemind(false);
        sendRemindService(remindService);
    }

    @Override
    public List<RemindService> getRegisteredRemindServices() {
        return super.getRegisteredRemindServices();
    }

    @Override
    public void close() {
    }

    private void sendRegistrationTokenToServer() {
        // Request for FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Token", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get textual representation of FCM registration token
                    String token = task.getResult();
                    Log.d("Token", token);

                    // Send current FCM token to server
                    CustomFirebaseMessagingService.sendRegistrationToServer(token);
                });
    }
}
