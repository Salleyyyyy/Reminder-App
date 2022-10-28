package com.flavor.reminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.server.model.BloodPressure;
import com.server.model.DocAppointment;
import com.server.model.Medicine;
import com.server.model.RemindService;
import com.server.model.RemindType;
import com.server.model.Water;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class connects to the local database and executes some SQL queries.
 */
public class RemindDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database name
     */
    private static final String DATABASE_NAME = "remindDatabase.db";
    /**
     * Database version
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * USER ID can be ignored
     */
    private static final int USER_ID = 0;

    /**
     * Constructor.
     *
     * @param context: Context of application.
     */
    public RemindDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the remind service table
        db.execSQL(RemindService.RemindEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Inserts a remind service object to database.
     *
     * @param remindService remind service object that should be written to database
     */
    public void writeRemindServiceInDatabase(RemindService remindService) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Deletion of unique object before insertion to avoid duplicates
        if (remindService.isUnique()) {
            deleteRemindServiceInDatabase(remindService);
        }
        // If a user tries to insert a row that already exists and should not be inserted due to unique constraint violation
        db.insertWithOnConflict(RemindService.RemindEntry.TABLE_NAME, null, translateRemindServiceToValueSet(remindService), SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a remind service in database.
     *
     * @param remindService Remind service to be deleted in the database
     */
    public void deleteRemindServiceInDatabase(RemindService remindService) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        String moment = remindService.getFormattedDateText();
        String inputType = remindService.getRemindType().toString();
        // In case of BloodPressure and Water simple deletion is enough as only one object is in the database
        String selection = RemindService.RemindEntry.COLUMN_INPUTTYPE + " = " + "'" + inputType + "'";

        if (!remindService.isUnique()) {
            // If remind service is not unique, the date attribute is important
            selection += " AND " + RemindService.RemindEntry.COLUMN_REMINDMOMENT + " = " + "'" + moment + "'";
        }
        db.delete(RemindService.RemindEntry.TABLE_NAME, selection, null);
    }

    /**
     * Returns the list of all remind services stored in the darabase.
     *
     * @return List of remindServices that are registered in the database.
     */
    public List<RemindService> getListOfRemindServices() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                RemindService.RemindEntry.COLUMN_INPUTTYPE,
                RemindService.RemindEntry.COLUMN_REMINDMOMENT,
                RemindService.RemindEntry.COLUMN_REMIND
        };

        // Execution of the query, iteration through the results with a cursor
        Cursor cursor = db.query(
                RemindService.RemindEntry.TABLE_NAME,
                projection,             // The array of columns to return
                "",              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List<RemindService> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            String remindType = cursor.getString(
                    cursor.getColumnIndex(RemindService.RemindEntry.COLUMN_INPUTTYPE));
            String dateString = cursor.getString(
                    cursor.getColumnIndex(RemindService.RemindEntry.COLUMN_REMINDMOMENT));
            int remindInteger = cursor.getInt(
                    cursor.getColumnIndex(RemindService.RemindEntry.COLUMN_REMIND));
            // Boolean values are stored as integers in sqlite
            boolean remind = remindInteger == 1;

            if (remindType.equals(RemindType.DOCAPPOINTMENT.toString())) {
                results.add(new DocAppointment(dateString, remind));
            } else if (remindType.equals(RemindType.MEDICINE.toString())) {
                results.add(new Medicine(dateString, remind));
            } else if (remindType.equals(RemindType.BLOODPRESSURE.toString())) {
                results.add(new BloodPressure(dateString, remind));
            } else if (remindType.equals(RemindType.WATER.toString())) {
                results.add(new Water(dateString, remind));
            }
        }
        cursor.close();
        return results;
    }

    /**
     * This function refreshes the database so that all remind services (Doc Appointments) that are not up to date get deleted.
     */
    public void refresh() {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // All entries are deleted whose notification was already pushed to client and the remind service is not regular
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        now.add(Calendar.MINUTE, DocAppointment.MIN_BEFORE_REMINDING);

        String deleteAllNotifiedDocAppointments = RemindService.RemindEntry.COLUMN_INPUTTYPE + " = " + "'" + RemindType.DOCAPPOINTMENT + "'" +
                " AND " + RemindService.RemindEntry.COLUMN_REMINDMOMENT + " <= " + "'" + RemindService.formatDateTime(now.getTime(), DocAppointment.DATE_FORMAT) + "'";
        db.delete(RemindService.RemindEntry.TABLE_NAME, deleteAllNotifiedDocAppointments, null);
    }

    /**
     * Creates a set of values from a remind service stored to database.
     *
     * @param remindService: Remind service for insertion in database
     * @return Content values of the remind service to insert
     */
    private ContentValues translateRemindServiceToValueSet(RemindService remindService) {
        ContentValues values = new ContentValues();
        values.put(RemindService.RemindEntry.COLUMN_USERNAME, USER_ID);
        values.put(RemindService.RemindEntry.COLUMN_INPUTTYPE, remindService.getRemindType().toString());
        values.put(RemindService.RemindEntry.COLUMN_REMINDMOMENT, remindService.getFormattedDateText());
        values.put(RemindService.RemindEntry.COLUMN_REMIND, remindService.getRemind());
        return values;
    }
}
