package com.ba.reminder.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ba.reminder.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.server.model.BloodPressure;
import com.server.model.RemindService;
import com.server.model.RemindType;
import com.server.model.Water;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This activity corresponds to the setting view.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Toolbar at tge top of the screen
     */
    private MaterialToolbar toolbar;

    /**
     * List view which displays all remind service entries
     */
    private ListView listView;

    /**
     * Toggle button for reminding (not) of bloodpressure
     */
    private SwitchMaterial switchMaterial_bloodPressure;

    /**
     * Toggle button for reminding (not) of water
     */
    private SwitchMaterial switchMaterial_water;

    /**
     * HashMap of all remindService strings and remindServices to retrieve selected remindService
     */
    private HashMap<String, RemindService> hashMap;

    /**
     * Array Adapter which contains the remind services to display in the listView
     */
    private ArrayAdapter<RemindService> arrayAdapter;

    /**
     * List of all remind services displayed in the list
     */
    private List<RemindService> entryList;

    /**
     * List of all remind services for the toggle buttons
     */
    private List<RemindService> switchesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingsview);

        // Find components in the UI
        findUIComponents();
        // Init Click Listeners
        initClickListeners();
        // Update settings view to show new settings
        updateSettingsView();
    }

    /**
     * Called when the view is recreated e.g. after closing and opening the app again.
     */
    @Override
    protected void onRestart() {
        // Update the setting view with updated entries and settings
        updateSettingsView();
        super.onRestart();
    }

    /**
     * Starts the activity for making a doc appointment entry.
     *
     * @param view: View of the button clicked
     */
    public void addNewDocAppointment(android.view.View view) {
        startActivity(new Intent(SettingsActivity.this, CalendarActivity.class));
    }

    /**
     * Starts the activity for making a medicine entry
     *
     * @param view: View of the button clicked
     */
    public void addNewMedAppointment(android.view.View view) {
        startActivity(new Intent(SettingsActivity.this, TimeActivity.class));
    }

    /**
     * Deletes the selected entry in the settings entry view.
     * The corresponding remind service of this entry will be canceled.
     *
     * @param view: View of the button clicked
     */
    public void deleteResult(View view) {
        // Get selected remind service by text
        TextView textView = (TextView) ((ConstraintLayout) view.getParent()).getChildAt(0);
        RemindService remindServiceToCancel = getSelectedRemindService(textView.getText().toString());

        // Delete/Cancel remindService
        remindServiceToCancel.setRemind(false);
        MainActivity.pushTechnology.cancelRemindService(remindServiceToCancel);

        // Switch back to main screen
        back();
    }

    /**
     * Returns back to main screen.
     *
     * @param view: View of the button clicked
     */
    public void back(View view) {
        finish();
    }

    /**
     * Returns back to main screen.
     */
    private void back() {
        finish();
    }

    /**
     * Finds all UI components in the view and initializes them.
     */
    private void findUIComponents() {
        switchMaterial_bloodPressure = findViewById(R.id.bloodpressure_toggle);
        switchMaterial_water = findViewById(R.id.water_toggle);
        toolbar = findViewById(R.id.settingsview_topAppBar);
        listView = findViewById(R.id.resultView);
    }

    /**
     * Gets the selected remind service by clicking entry in the entry list.
     *
     * @param text: Text of the selected entry
     * @return Remind service seleceted by the user
     */
    private RemindService getSelectedRemindService(String text) {
        return hashMap.get(text);
    }

    /**
     * Updates all the remind services in the lists.
     */
    private synchronized void updateRemindServiceList() {
        // All remind services are received by Back-End (server if remote push technology)
        List<RemindService> allRemindServiceList = MainActivity.pushTechnology.getRegisteredRemindServices();
        // Init with empty lists
        entryList = new ArrayList<>();
        switchesList = new ArrayList<>();
        // Filter list of remind objects for displaying
        for (RemindService remindService : allRemindServiceList) {
            if (remindService.getRemindType().equals(RemindType.DOCAPPOINTMENT) || remindService.getRemindType().equals(RemindType.MEDICINE)) {
                entryList.add(remindService);
            } else if (remindService.getRemindType().equals(RemindType.WATER) || remindService.getRemindType().equals(RemindType.BLOODPRESSURE)) {
                switchesList.add(remindService);
            }
        }
    }

    /**
     * Updates the entries according to the lists of remind services.
     */
    private void updateEntries() {
        // Fill in the hash map of remind services to retrieve when selecting an entry in the list
        fillHashMapOfRemindServices(entryList);
        arrayAdapter = new ArrayAdapter<>(SettingsActivity.this, R.layout.result, R.id.resultTextView, entryList);
    }

    /**
     * Fill in the hash map with the list of all remind services given.
     *
     * @param list: List of all remind services
     */
    private void fillHashMapOfRemindServices(List<RemindService> list) {
        hashMap = new HashMap<>();
        for (RemindService remindService : list)
            hashMap.put(remindService.toString(), remindService);
    }

    /**
     * Updates the toggle states of the toggle buttons to the corresponding remind services.
     */
    private void updateCheckStateOfSwitchMaterials() {
        // Depending on implementation
        // If there is not a remind service sent by Back-End, that means the
        // remind flag is per default false
        boolean bloodPressureChecked = false;
        boolean waterChecked = false;

        for (RemindService remindService : switchesList) {
            if (remindService.getRemindType().equals(RemindType.BLOODPRESSURE)) {
                bloodPressureChecked = remindService.getRemind();
            } else if (remindService.getRemindType().equals(RemindType.WATER)) {
                waterChecked = remindService.getRemind();
            }
        }

        switchMaterial_bloodPressure.setChecked(bloodPressureChecked);
        switchMaterial_water.setChecked(waterChecked);
    }

    /**
     * Requests for reminding of water.
     *
     * @param remind: true if user wants to get reminded of water, otherwise false
     */
    private void requestForWaterRemind(boolean remind) {
        Water water = new Water();
        water.setRemind(remind);
        requestForRemind(water);
    }

    /**
     * Requests for reminding of blood pressure.
     *
     * @param remind: true if user wants to get reminded of blood pressure, otherwise false
     */
    private void requestForBloodPressureRemind(boolean remind) {
        BloodPressure bloodPressure = new BloodPressure();
        bloodPressure.setRemind(remind);
        requestForRemind(bloodPressure);
    }

    /**
     * Requests for this remind service to the Back-End.
     *
     * @param remindService: Remind service that is registered for.
     */
    private void requestForRemind(RemindService remindService) {
        if (remindService.getRemind()) {
            MainActivity.pushTechnology.requestForRemindService(remindService);
        } else {
            MainActivity.pushTechnology.cancelRemindService(remindService);
        }

        // Update settings view
        updateSettingsView();
    }

    /**
     * Shows all entries of remind services.
     */
    private void showEntries() {
        listView.setAdapter(arrayAdapter);
    }

    /**
     * Initializes all click listeners in the view.
     */
    private void initClickListeners() {
        // Click listener for menu icon
        initNavigationButtonClickListener();
        // Click listeners for switchers
        initSwitchClickListeners();
    }

    /**
     * Initializes the click listener of the navigation button.
     */
    private void initNavigationButtonClickListener() {
        toolbar.setNavigationOnClickListener(this::back);
    }

    /**
     * Initializes all click listeners of the toggle buttons.
     */
    private void initSwitchClickListeners() {
        // If any of the toggle buttons are clicked, the user returns to the main view.
        switchMaterial_bloodPressure.setOnClickListener(v -> {
            requestForBloodPressureRemind(switchMaterial_bloodPressure.isChecked());
            back();
        });
        switchMaterial_water.setOnClickListener(v -> {
            requestForWaterRemind(switchMaterial_water.isChecked());
            back();
        });
    }

    /**
     * Updates (Refreshes) the whole settings view (remind services list, entry list, toggle buttons).
     */
    private void updateSettingsView() {
        // Update remindService list with data from backend
        updateRemindServiceList();
        // Update Entry List
        updateEntries();
        // Show updated entries
        showEntries();
        // Fill check state of switch
        updateCheckStateOfSwitchMaterials();
    }
}
