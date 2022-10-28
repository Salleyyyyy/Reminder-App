package com.ba.reminder.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ba.reminder.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.server.model.DocAppointment;
import com.server.model.Medicine;
import com.server.model.SerializeConst;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity class  behind the time view where a user selects the time.
 */
public class TimeActivity extends AppCompatActivity {

    /**
     * Calendar instance which holds the selected time, if none selected the current time will be used
     */
    private final Calendar selectedTimeCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtimeview);

        initBackButtonInToolbar();
        normalizeSelectedTimeCalendar();
        setSelectedTime();
        // The time dialog is opened when this view is displayed to the user
        openTimeDialog(findViewById(R.id.newdocappoint));
    }

    /**
     * Opens the time dialog where the user determines hour and minutes.
     *
     * @param view: View of the button clicked
     */
    public void openTimeDialog(View view) {
        MaterialTimePicker materialTimePicker = createMaterialTimePicker();
        materialTimePicker.show(getSupportFragmentManager(), TimeActivity.class.getSimpleName());
    }

    /**
     * Opens the ui process of selecting a time point.
     *
     * @param view: View of the button clicked
     */
    public void cancelProcess(View view) {
        finish();
    }

    /**
     * Normalizes the selected time, that means all seconds ans milliseconds get discarded (00 instead).
     */
    private void normalizeSelectedTimeCalendar() {
        selectedTimeCalendar.set(Calendar.SECOND, 0);
        selectedTimeCalendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Creates a time picker where a user picks the time.
     *
     * @return
     */
    private MaterialTimePicker createMaterialTimePicker() {
        // Sets the current time (hour and minutes)
        Calendar currentTimeCalendar = Calendar.getInstance();
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        // 24 hours standard
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(currentTimeCalendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(currentTimeCalendar.get(Calendar.MINUTE))
                        .setTitleText("Uhrzeit auswählen")
                        .build();
        picker.addOnPositiveButtonClickListener(v -> {
            selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
            selectedTimeCalendar.set(Calendar.MINUTE, picker.getMinute());
            setSelectedTime();
        });
        return picker;
    }

    /**
     * Initializes the back button in the toolbar at the top of the view
     */
    private void initBackButtonInToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.newtimeview_topAppBar);
        toolbar.setNavigationOnClickListener(this::back);
    }

    /**
     * Returns back to the previous view.
     */
    private void back(View view) {
        finish();
    }

    /**
     * Displays the selected time of the user by the text field.
     */
    private void setSelectedTime() {
        TextView textView = findViewById(R.id.selectedTimeText);
        textView.setText(getSelectedTimeAsText());
    }

    /**
     * Gets the selected time of the user formatted as 'hh:mm'.
     *
     * @return Formatted time selected by the user.
     */
    private String getSelectedTimeAsText() {
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.LONG, Locale.GERMANY);
        return dateFormat.format(selectedTimeCalendar.getTime());
    }

    /**
     * Saves the time (hour and minutes) by the user after picking the right time.
     *
     * @param view: View of the button clicked
     */
    public void save(View view) {
        int hourOfDay = selectedTimeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedTimeCalendar.get(Calendar.MINUTE);

        // Check if passed object will be of type DocAppointment or Medicine
        if (getIntent().getSerializableExtra(SerializeConst.DATE_IN_MILLISECOND) != null) {
            // Get date that was selected in a previous view
            long dateInMilliseconds = (long) getIntent().getSerializableExtra(SerializeConst.DATE_IN_MILLISECOND);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dateInMilliseconds);
            MainActivity.pushTechnology.requestForRemindService(new DocAppointment(dateInMilliseconds, hourOfDay, minute));
        } else {
            // For remind services of type medicine the time info is sufficient
            MainActivity.pushTechnology.requestForRemindService(new Medicine(hourOfDay, minute));
        }
        // Go back to previous view
        finish();
    }
}
