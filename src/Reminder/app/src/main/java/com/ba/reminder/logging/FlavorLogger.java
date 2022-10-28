package com.ba.reminder.logging;

import android.util.Log;

import com.ba.reminder.BuildConfig;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This logger class logs all phases of a notification (from creation to displaying).
 * The log can be viewed via logcat and ADB.
 */
public class FlavorLogger {

    /**
     * log info: flavor name
     */
    private static final String flavorMode = BuildConfig.FLAVOR;
    /**
     * Date format for all dates
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Empty constructor.
     */
    public FlavorLogger() {
    }

    /**
     * Logs registration for a push notification.
     *
     * @param notificationType: Type of notification, e.g. Doc Appointment
     */
    public void infoForNotification(String notificationType) {
        Log.i(flavorMode, "[Notification] Registered for notification of type: " + notificationType);
    }

    /**
     * Logs the date and time when the notification should arrive at the client.
     *
     * @param scheduledTime: Date and time for expected arrival
     */
    public void infoForNotificationShouldArriveAt(Date scheduledTime) {
        Log.i(flavorMode, "[Should Arrive] Notification should arrive at: " + formatDate(scheduledTime));
    }

    /**
     * Logs the message/request to Back-End.
     *
     * @param messageToServer: Request to Back-End.
     */
    public void infoForNotificationSent(String messageToServer) {
        Log.i(flavorMode, "[Sent To Server] Message was sent to server: " + messageToServer);
    }

    /**
     * Logs the notification message when the notification arrives at the client.
     *
     * @param notificationMessage: Notification text
     */
    public void infoForNotificationArrivedAtClient(String notificationMessage) {
        Log.i(flavorMode, "[Arrived To Client] Message arrived at client: " + notificationMessage);
    }

    /**
     * Logs the notification message when the notification was displayed in the notification tray.
     *
     * @param notificationMessage: Notification text
     */
    public void infoForNotificationDisplayed(String notificationMessage) {
        Log.i(flavorMode, "[Displayed] Notification was displayed: " + notificationMessage);
    }

    /**
     * Formats the date with a fixed date format.
     *
     * @param date: Date object
     * @return Formatted date text
     */
    private String formatDate(Date date) {
        Format formatter = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
        return formatter.format(date);
    }
}