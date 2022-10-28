package com.flavor.reminder.localnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ba.reminder.notification.NotificationReceiver;
import com.server.model.RemindService;

import com.flavor.reminder.database.DatabaseRefresh;

/**
 * This class is the entry point for all local push notifications.
 * Local push notifications are implemented with alarms that trigger at the right moment to push a notification to the client.
 *
 * Be care of:
 * As of API 19 all repeating alarms are inexact.
 * For further info: https://developer.android.com/reference/android/app/AlarmManager#setInexactRepeating(int,%20long,%20long,%20android.app.PendingIntent)
 */
public class LocalNotificationScheduler {
    /**
     * Alarm Manager to schedule alarms
     */
    private final AlarmManager alarmManager;
    /**
     * Context of application
     */
    private final Context context;

    /**
     * Constructor that initializes the alarm manager.
     *
     * @param context: Context of apllication
     */
    public LocalNotificationScheduler(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Registers or cancels a remind service which depends on the remind flag of the remind service.
     *
     * @param remindService: Remind service
     */
    public void notifyOrCancelNotification(RemindService remindService) {
        scheduleAlarmForLocalNotification(remindService);
    }

    /**
     * Schedules the alarm for a remind service to push a notification at the scheduled time.
     *
     * @param remindService: Remind service
     */
    public void scheduleAlarmForLocalNotification(RemindService remindService) {
        // ID for cancel operation
        int remindServiceId = remindService.createUniqueID();
        // Broadcast for displaying a notification
        PendingIntent broadcast = createBroadcastForLocalNotification(remindService.getNotificationText(), remindServiceId);
        long scheduledTimeInMilliseconds = remindService.scheduleTimeToTriggerReminding().getTime();
        // Registration
        if (remindService.getRemind()) {
            if (!remindService.isRegular()) {
                // RTC WAKE UP for waking up the device in idle
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTimeInMilliseconds, broadcast);
                // Delete entry after notification as this type of remind service is not regular
                PendingIntent entryDeletion = createPendingIntentForDatabaseRefresh(remindServiceId);
                // Inconsistency is tolerable
                alarmManager.set(AlarmManager.RTC, scheduledTimeInMilliseconds, entryDeletion);
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, scheduledTimeInMilliseconds,
                        remindService.getPeriodOfReminding(), broadcast);
            }
            //Cancel
        } else {
            // Both are canceled as both have the same ID
            alarmManager.cancel(broadcast);
        }
    }

    /**
     * Creates a broadcast that is intents to push a notification built with the notification text.
     *
     * @param notificationMessage: Notification text
     * @param broadcast_ID: Broad cast ID for canceling
     * @return Pending intent for local pushing
     */
    private PendingIntent createBroadcastForLocalNotification(String notificationMessage, int broadcast_ID) {
        Intent notificationIntent = NotificationReceiver.createNotificationIntent(context, notificationMessage);
        return PendingIntent.getBroadcast(context, broadcast_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates a broadcast that is intents to refresh the database to delete entries that are not up to date.
     *
     * @param broadcast_ID: Broad cast ID for canceling
     * @return Pending intent for refreshing the database
     */
    private PendingIntent createPendingIntentForDatabaseRefresh(int broadcast_ID) {
        Intent notificationIntent = new Intent(context, DatabaseRefresh.class);
        return PendingIntent.getBroadcast(context, broadcast_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
