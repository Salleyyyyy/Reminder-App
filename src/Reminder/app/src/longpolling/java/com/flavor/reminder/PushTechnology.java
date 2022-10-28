package com.flavor.reminder;

import android.content.Context;
import android.util.Log;

import com.ba.reminder.interfaces.IPushTechnology;
import com.ba.reminder.logging.FlavorLogger;
import com.ba.reminder.remote.HTTPPushTechnology;
import com.flavor.reminder.remote.TWaitForUpdates;
import com.server.model.RemindService;

import java.util.List;

/**
 * HTTP Long Polling Push solution.
 */
public class PushTechnology extends HTTPPushTechnology implements IPushTechnology {

    /**
     * File name of local storage.
     */
    public static final String SHARED_PREF_FILENAME = "long_polling";
    /**
     * Port of the private HTTP push server.
     */
    public static final int PORT = 80;
    /**
     * Context of application.
     */
    private final Context context;
    /**
     * Logger.
     */
    private final FlavorLogger flavorLogger;
    /**
     * Thread that actively waits for notification with Long Polling
     */
    private Thread waitForUpdates;

    /**
     * Constructor.
     *
     * @param context: Context of application.
     */
    public PushTechnology(Context context) {
        super(context, SHARED_PREF_FILENAME);
        this.context = context;
        flavorLogger = new FlavorLogger();

        // HTTP Long Polling
        waitForNotifications();
    }

    /**
     * Waits for notifications pushed from the server.
     */
    private void waitForNotifications() {
        waitForUpdates = new TWaitForUpdates(context, clientId);
        waitForUpdates.start();
    }

    @Override
    public void requestForRemindService(RemindService remindService) {
        // Logging
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
        if (waitForUpdates != null && !waitForUpdates.isInterrupted()) {
            Log.d("PushTech", "Interrupt Waiting");
            waitForUpdates.interrupt();
        }
    }
}
