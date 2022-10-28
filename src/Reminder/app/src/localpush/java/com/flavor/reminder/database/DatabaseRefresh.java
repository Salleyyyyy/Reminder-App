package com.flavor.reminder.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class is a Broadcast receiver for refreshing the database and deleting
 * obsolete entries.
 */
public class DatabaseRefresh extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RemindDatabaseHelper remindDatabaseHelper = new RemindDatabaseHelper(context);
        remindDatabaseHelper.refresh();
    }
}
