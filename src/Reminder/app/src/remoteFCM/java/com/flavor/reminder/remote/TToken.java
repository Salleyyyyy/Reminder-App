package com.flavor.reminder.remote;

import android.util.Log;

import com.ba.reminder.remote.ServerUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * This thread connects to the private HTTP forwarding server under the corresponding url to send the FCM token.
 */
public class TToken extends Thread {

    /**
     * FCM token.
     */
    private final String token;
    /**
     * Client ID.
     */
    private final UUID clientId;

    /**
     * Constructor.
     *
     * @param token: FCM token
     * @param clientId: Client ID
     */
    public TToken(String token, UUID clientId) {
        this.token = token;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            // Set up connection
            URL url = new URL(ServerUtils.POST_TOKEN_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(false);

            // POST Header info
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Connection", "close");

            // POST Body contains client ID and FCM token separated with a ner line
            BufferedWriter br_out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            br_out.write(ServerUtils.POST_CLIENT_ID_REGEX + clientId.toString());
            br_out.newLine();
            br_out.write(ServerUtils.POST_CLIENT_TOKEN_REGEX + token);
            br_out.newLine();
            br_out.flush();
            br_out.close();
            // Wait for server response
            // No response body
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
    }
}
