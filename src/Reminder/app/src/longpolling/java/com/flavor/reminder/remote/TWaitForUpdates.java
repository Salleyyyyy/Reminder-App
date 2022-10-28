package com.flavor.reminder.remote;

import android.content.Context;
import android.util.Log;

import com.ba.reminder.logging.FlavorLogger;
import com.ba.reminder.notification.NotificationReceiver;
import com.ba.reminder.remote.ServerUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.UUID;

/**
 * This threads connects to the server with the URL for HTTP Long Polling.
 * The connection is kept open until a push notification arrives at the client.
 */
public class TWaitForUpdates extends Thread {

    /**
     * Logger.
     */
    private final FlavorLogger flavorLogger = new FlavorLogger();
    /**
     * Client ID.
     */
    private final UUID clientId;
    /**
     * Context of application.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context: Context of application.
     * @param clientId: Client ID for authentication
     */
    public TWaitForUpdates(Context context, UUID clientId) {
        this.context = context;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        URL url;
        try {
            url = new URL(ServerUtils.WAIT_FOR_REMIND_URL);
        } catch (MalformedURLException e) {
            Log.e("ERROR ", e.toString());
            return;
        }

        while (true) {
            // Non persistent connection
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                // Infinite Waiting
                urlConnection.setConnectTimeout(ServerUtils.INFINITE_CONNECTION_TIMEOUT);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                // HTTP Post headers
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "text/plain");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Connection", "close");

                // POST Body contains client id for authentication
                BufferedWriter br_out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                br_out.write(ServerUtils.POST_CLIENT_ID_REGEX + clientId.toString());
                br_out.newLine();
                br_out.flush();
                br_out.close();

                // Blocks until notification has been received
                if (urlConnection.getResponseCode() == 200) {
                    BufferedReader br_in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String notificationMessage;
                    while ((notificationMessage = br_in.readLine()) != null) {
                        // Log as message arrived at client
                        flavorLogger.infoForNotificationArrivedAtClient(notificationMessage);

                        // Display message at the notification tray
                        NotificationReceiver.triggerNotification(context, notificationMessage);
                    }
                    br_in.close();
                } else {
                    Log.e("SERVER ERROR", urlConnection.getResponseCode() + "");
                    Log.e("HTTP Long Polling", "Stop Long Polling");
                    return;
                }
                // If a connection problem occurs, try reconnecting
            } catch (ConnectException e) {
                Log.e("Exception", e.toString());
                Log.d("DEBUG", "Reconnecting failed");
                Log.d("DEBUG", "Start Reconnecting");
                reconnect();
            } catch (SocketException e) {
                Log.e("Exception", e.toString());
                Log.d("DEBUG", "Start Reconnecting");
                reconnect();
            } catch (IOException e) {
                Log.e("ERROR", e.toString());
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
        }
    }

    /**
     * Reconnects to the server.
     */
    private void reconnect() {
        run();
    }
}
