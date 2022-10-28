package com.flavor.reminder;

import android.content.Context;

import com.ba.reminder.interfaces.IPushTechnology;
import com.ba.reminder.logging.FlavorLogger;
import com.flavor.reminder.localnotifications.LocalNotificationScheduler;
import com.server.model.RemindService;

import java.util.List;

import com.flavor.reminder.database.RemindDatabaseHelper;

/**
 * Local Push solution.
 */
public class PushTechnology implements IPushTechnology {

    /**
     * Not used in the local solution.
     */
    public static final int PORT = -1;
    /**
     * Database class to store and delete all entries of remind services.
     */
    private RemindDatabaseHelper remindDatabaseHelper;
    /**
     * Local Notification scheduler to schedule the alarms to push a notification at the right time.
     */
    private final LocalNotificationScheduler localNotificationScheduler;
    /**
     * Logger
     */
    private final FlavorLogger flavorLogger;

    /**
     * Flag to wait for thread before database is ready to use.
     */
    private volatile boolean databaseReadyToUse = false;
    /**
     * Lock object for sequential executions.
     */
    private final Object sequentialExecution = new Object();

    /**
     * Constructor.
     *
     * @param context: Context of application
     */
    public PushTechnology(Context context) {
        flavorLogger = new FlavorLogger();
        // Database operations and initializing of the notification scheduler are parallel
        Thread createDatabaseHelper = new Thread(() -> {
            remindDatabaseHelper = new RemindDatabaseHelper(context);
            databaseReadyToUse = true;
        });
        createDatabaseHelper.start();
        localNotificationScheduler = new LocalNotificationScheduler(context);
    }

    @Override
    public void requestForRemindService(RemindService remindService) {
        // Wait until local database can be used
        while(!databaseReadyToUse);
        // Logging as remind service is registered/canceled
        flavorLogger.infoForNotification(remindService.getRemindType().toString());
        flavorLogger.infoForNotificationShouldArriveAt(remindService.scheduleTimeToTriggerReminding());

        // Avoid requesting and canceling at the same time
        synchronized (sequentialExecution) {
            // Database operations and notification scheduler operations are parallel
            Thread scheduleLocalNotification = new Thread(() -> localNotificationScheduler.notifyOrCancelNotification(remindService));
            Thread writeEntryInDatabase = new Thread(() -> remindDatabaseHelper.writeRemindServiceInDatabase(remindService));
            scheduleLocalNotification.start();
            writeEntryInDatabase.start();
        }
    }

    @Override
    public void cancelRemindService(RemindService remindService) {
        // Wait until local database can be used
        while(!databaseReadyToUse);
        // Avoid requesting and canceling at the same time
        synchronized (sequentialExecution) {
            // Database operations and notification scheduler operations are parallel
            Thread cancelLocalNotification = new Thread(() -> localNotificationScheduler.notifyOrCancelNotification(remindService));
            Thread deleteEntryInDatabase = new Thread(() -> remindDatabaseHelper.deleteRemindServiceInDatabase(remindService));
            cancelLocalNotification.start();
            deleteEntryInDatabase.start();
        }
    }

    @Override
    public List<RemindService> getRegisteredRemindServices() {
        // Wait until local database can be used
        while(!databaseReadyToUse);
        return remindDatabaseHelper.getListOfRemindServices();
    }

    @Override
    public void close() {
        remindDatabaseHelper.close();
    }
}
