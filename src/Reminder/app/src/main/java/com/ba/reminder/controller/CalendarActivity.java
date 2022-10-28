package com.ba.reminder.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ba.reminder.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.server.model.SerializeConst;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This activity is related to the calendar view where the user sets the date of the remind service (in this case: Doc appointnment)
 */
public class CalendarActivity extends AppCompatActivity {

    /**
     * Calendar instance for selected date
     **/
    private final Calendar selectedDateCalendar = Calendar.getInstance();

    /**
     * Click listener of the date dialog
     */
    private final DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
        selectedDateCalendar.set(year, month, dayOfMonth);
        setSelectedDate();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdocappointview);

        initClickListeners();
        setSelectedDate();
        openDateDialog(findViewById(R.id.newdocappoint));
    }

    /**
     * Starts the activity for the time view where the user determines the time.
     *
     * @param view: View of the button clicked
     */
    public void selectTime(android.view.View view) {
        startActivity((new Intent(CalendarActivity.this, TimeActivity.class).putExtra(SerializeConst.DATE_IN_MILLISECOND, selectedDateCalendar.getTimeInMillis())));
        finish();
    }

    /**
     * Opens the date dialog where the user determines the date (day, month, year)
     *
     * @param view: View of the button clicked
     */
    public void openDateDialog(View view) {
        // Set current Locale to Germany for DatePicker with german language
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);

        DatePickerDialog datePickerDialog = createDatePickerDialog();
        datePickerDialog.show();

        // Redo
        Locale.setDefault(locale);
    }

    /**
     * Creates a date dialog where the current date is shown.
     *
     * @return Date dialog where the user can input a date.
     */
    private DatePickerDialog createDatePickerDialog() {
        Calendar dateNow = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                onDateSetListener,
                dateNow.get(Calendar.YEAR),
                dateNow.get(Calendar.MONTH),
                dateNow.get(Calendar.DAY_OF_MONTH)) {
        };
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(dateNow.getTimeInMillis());
        return datePickerDialog;
    }

    /**
     * Initializes all click listeners
     */
    private void initClickListeners() {
        // Click listener for back icon
        MaterialToolbar toolbar = findViewById(R.id.newdocappointview_topAppBar);
        toolbar.setNavigationOnClickListener(this::back);
    }

    /**
     * Finishes the activity and returns to the previous dialog.
     *
     * @param view: View of the button clicked
     */
    private void back(View view) {
        finish();
    }

    /**
     * Displays the selected date of the user in the text field in the view.
     */
    private void setSelectedDate() {
        TextView textView = findViewById(R.id.selectedDateText);
        textView.setText(getSelectedDateAsText());
    }

    /**
     * Formats the selected date to a date text representation.
     *
     * @return Formatted date text
     */
    private String getSelectedDateAsText() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
        return dateFormat.format(selectedDateCalendar.getTime());
    }
}