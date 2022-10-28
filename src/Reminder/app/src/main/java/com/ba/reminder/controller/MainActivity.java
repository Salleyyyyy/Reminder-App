package com.ba.reminder.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ba.reminder.R;
import com.flavor.reminder.PushTechnology;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Entry point of the app. This activity starts when the app is launched.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Push Technology for all views and interaction in this app
     */
    static PushTechnology pushTechnology;

    /**
     * Called when the activity is created.
     * View is displayed to user and all procedures are done.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);

        initClickListener();
        initPushTechnology();
    }

    /**
     * Displays the settings view by starting the settings activity.
     *
     * @param view: View of the button clicked
     */
    public void showSettings(android.view.View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    /**
     * Initializes all click listeners.
     */
    private void initClickListener() {
        // Click listener for menu icon
        MaterialToolbar toolbar = findViewById(R.id.main_topAppBar);
        toolbar.setNavigationOnClickListener(this::showSettings);
    }

    /**
     * Initializes the Push Technology.
     */
    private void initPushTechnology() {
        pushTechnology = new PushTechnology(MainActivity.this);
    }

    /**
     * Called when the view is destroyed.
     * Push Technology will be closed.
     */
    @Override
    protected void onDestroy() {
        pushTechnology.close();
        super.onDestroy();
    }
}