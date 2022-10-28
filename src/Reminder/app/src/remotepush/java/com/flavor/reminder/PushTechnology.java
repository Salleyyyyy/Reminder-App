package com.flavor.reminder;

import android.content.Context;

import com.ba.reminder.interfaces.IPushTechnology;
import com.ba.reminder.logging.FlavorLogger;
import com.ba.reminder.remote.HTTPPushTechnology;
import com.flavor.reminder.remote.THoldConnection;
import com.server.model.RemindService;

import java.util.List;

/**
 * Push solution with a private (own) mobile notification server (MNS).
 */
public class PushTechnology extends HTTPPushTechnology implements IPushTechnology {

    /**
     * File name to local storage.
     */
    public static final String SHARED_PREF_FILENAME = "remote_push";
    /**
     * Port for private HTTP Server, responsible for forwarding
     */
    public static final int PORT = 82;
    /**
     * Logger
     */
    private final FlavorLogger flavorLogger;
    /**
     * Threads that holds an open connection to MNS.
     */
    private final THoldConnection TListenToNotification;

    /**
     * Constructor.
     *
     * @param context: Context of application
     */
    public PushTechnology (Context context){
        super(context, SHARED_PREF_FILENAME);
        TListenToNotification = new THoldConnection(context, clientId);
        TListenToNotification.start();
        flavorLogger = new FlavorLogger();
    }

    @Override
    public void requestForRemindService(RemindService remindService) {
        // Logging as remind service gets registered
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
        if(TListenToNotification != null){
            TListenToNotification.interrupt();
        }
    }
}
