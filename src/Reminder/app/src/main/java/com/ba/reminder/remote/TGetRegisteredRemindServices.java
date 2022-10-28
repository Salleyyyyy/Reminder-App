package com.ba.reminder.remote;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.server.model.RemindService;
import com.server.model.parse.RemindServiceJsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This thread gets a list of all registered remind services at the server.
 */
public class TGetRegisteredRemindServices extends Thread {

    /**
     * Client ID
     */
    private final UUID clientId;
    /**
     * Result list for remind services received from the server
     */
    private final AtomicReference<ArrayList<RemindService>> remindServices;
    /**
     * JSON Parser for remind service objects
     */
    private final RemindServiceJsonParser remindServiceJsonParser = new RemindServiceJsonParser();

    /**
     * Constructor.
     *
     * @param clientId: Client ID
     * @param remindServices: Result list for remind services received from the server
     */
    public TGetRegisteredRemindServices(UUID clientId, AtomicReference<ArrayList<RemindService>> remindServices) {
        this.clientId = clientId;
        this.remindServices = remindServices;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            // Set up connection
            URL url = new URL(ServerUtils.GET_REMIND_OBJECT_LIST_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            // HTTP Post method
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Connection", "close");

            // Post the client ID
            BufferedWriter br_out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            br_out.write(ServerUtils.POST_CLIENT_ID_REGEX + clientId.toString());
            br_out.newLine();
            br_out.close();

            // Success
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader br_in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                ArrayList<RemindService> listOfRemindServices = new ArrayList<>();
                String readRemindService;
                // List of remind services separated with a new line
                while ((readRemindService = br_in.readLine()) != null) {
                    Log.d("Received RemindService:", readRemindService);
                    RemindService remindService;
                    try {
                        // Remind service deserialized from JSON
                        remindService = remindServiceJsonParser.readRemindServiceFromJson(readRemindService);
                    } catch (JsonSyntaxException jsonSyntaxException) {
                        Log.e("Json Parse Error", jsonSyntaxException.getMessage());
                        continue;
                    }
                    listOfRemindServices.add(remindService);
                }
                br_in.close();
                remindServices.set(listOfRemindServices);
            } else {
                Log.e("SERVER ERROR", urlConnection.getResponseCode() + "");
            }
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
