package com.ba.reminder.remote;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This thread connects to the server in order to get and to save the client ID received.
 */
public class TConnect extends Thread {

    /**
     * SharedPreferences of the client.
     */
    private final SharedPreferences sharedPref;

    /**
     * Constructor with a SharedPreferences object for saving the client ID locally.
     *
     * @param sharedPref: SharedPreferences of the client
     */
    public TConnect(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(ServerUtils.URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(ServerUtils.CONNECTION_TIMEOUT);
            // Successful case
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                // When the client connects to the server,
                // he gets a unique client ID
                String clientIdFromServer = br.readLine();
                // Save the client ID received for authentication towards the server
                writeReceivedClientIdIntoLocalStorage(clientIdFromServer);
            } else {
                Log.d("SERVER ERROR", urlConnection.getResponseCode() + "");
            }
        } catch (IOException e) {
            Log.e("Connection", e.toString());
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    /**
     * Stores the client ID to a local storage at the client.
     *
     * @param clientIdFromServer: Client ID as text
     */
    private void writeReceivedClientIdIntoLocalStorage(String clientIdFromServer) {
        SharedPreferences.Editor editor = sharedPref.edit();
        // Saving with a common key for retrieving later
        editor.putString(ServerUtils.clientId_key, clientIdFromServer);
        // Synchronous action
        if (!editor.commit()) {
            Log.e("Local Storage", "Unsuccessful writing to local storage!");
        }
    }
}
