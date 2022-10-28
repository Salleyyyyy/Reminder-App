package com.ba.reminder.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.ba.reminder.R;
import com.ba.reminder.logging.FlavorLogger;
import com.server.model.SerializeConst;

import java.util.Random;

/**
 * This class is the entry point for displaying the notification in the notification tray.
 */
public class NotificationReceiver extends BroadcastReceiver {

    /**
     * Common notification channel id for all notifications.
     */
    private static final String NOTIFICATION_CHANNEL_ID = "0";
    /**
     * Common notification channel name for all notifications.
     */
    private static final String NOTIFICATION_CHANNEL_NAME = "REMIND_CHANNEL";
    /**
     * Common notification title for all notifications.
     */
    private static final String NOTIFICATION_CONTENT_TITLE = "Reminder App Notification";
    /**
     * Common color code for all notifications.
     */
    private static final String COLOR_STRING = "#4CAF50";

    @Override
    public void onReceive(Context context, Intent intent) {
        FlavorLogger flavorLogger = new FlavorLogger();

        // Create NotificationManager and NotificationChannel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // High priority for fast delivery
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);

        // Retrieve notification text from intent
        String message = (String) intent.getSerializableExtra(SerializeConst.NOTIFICATION_MESSAGE);
        // Create a random number
        int randomId = createRandomId();

        // Build a notification
        Notification notification = buildNotification(context, message);

        // Display the notification
        notificationManager.notify(randomId, notification);

        // Logging for notification displayed
        flavorLogger.infoForNotificationDisplayed(message);
    }

    /**
     * Builds and designs an Android notification with the notification text given.
     *
     * @param context: Context in which the notification should be displayed
     * @param content: Notification text
     * @return Android notification
     */
    private Notification buildNotification(Context context, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(NOTIFICATION_CONTENT_TITLE);
        builder.setContentText(content);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setColorized(true);
        builder.setColor(Color.parseColor(COLOR_STRING));
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        return builder.build();
    }

    /**
     * Creates the intent for triggering the activity of displaying a notification with the notification text given.
     *
     * @param context: Context in which the notification should be displayed
     * @param message: Notification text
     * @return Intent for triggering the activity of displaying a notification
     */
    public static Intent createNotificationIntent(Context context, String message) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(SerializeConst.NOTIFICATION_MESSAGE, message);
        return notificationIntent;
    }

    /**
     * Triggers the displaying of the notification at the notification tray.
     *
     * @param context: Context in which the notification should be displayed
     * @param message: Message which contains the notification text to show
     */
    public static void triggerNotification(Context context, String message) {
        context.sendBroadcast(createNotificationIntent(context, message));
    }

    /**
     * Creates a random number.
     *
     * @return Random integer.
     */
    private int createRandomId() {
        return (new Random()).nextInt();
    }
}
