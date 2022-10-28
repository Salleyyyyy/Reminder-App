package com.ba.reminder.remote;

import android.util.Log;

import com.ba.reminder.logging.FlavorLogger;
import com.server.model.RemindService;
import com.server.model.parse.RemindServiceJsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * This thread sends a registration or canceling of a remind service to the server.
 */
public class TSendRemindService extends Thread {

    /**
     * JSON Parser for remind service objects
     */
    private final RemindServiceJsonParser remindServiceJsonParser = new RemindServiceJsonParser();
    /**
     * Remind service for registration or canceling at the back-end
     */
    private final RemindService remindService;
    /**
     * Client ID
     */
    private final UUID clientId;
    /**
     * Logger
     */
    private final FlavorLogger flavorLogger;

    /**
     * Constructor.
     *
     * @param remindService: Remind service for registration or canceling
     * @param clientId: Client ID
     */
    public TSendRemindService(RemindService remindService, UUID clientId) {
        this.remindService = remindService;
        this.clientId = clientId;
        flavorLogger = new FlavorLogger();
    }

    @Override
    public void run() {
        String remindService_json = remindServiceJsonParser.parseToJson(remindService);
        Log.d("RemindService to send", remindService_json);

        HttpURLConnection urlConnection = null;
        try {
            // Set up connection
            URL url = new URL(ServerUtils.POST_REMIND_SERVICE_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);

            // POST Header info
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Connection", "close");

            // POST Body contains remindService as json
            BufferedWriter br_out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            br_out.write(ServerUtils.POST_CLIENT_ID_REGEX + clientId.toString());
            br_out.newLine();

            br_out.write(remindService_json);
            br_out.newLine();
            br_out.flush();
            br_out.close();

            if (urlConnection.getResponseCode() != 200) {
                Log.e("SERVER ERROR", urlConnection.getResponseCode() + "");
            }
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        // Log for remind service registration sent to server
        flavorLogger.infoForNotificationSent(remindService_json);
    }
}
