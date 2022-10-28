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
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

/**
 * This thread holds an open connection to PMNS (private mobile notification server) to receive push notifications.
 */
public class THoldConnection extends Thread {

    /**
     * Regular prefix for notifications from PMNS
     */
    public static final String POST_NOTIFICATION = "NOTIFICATION: ";
    /**
     * Regular prefix post a client ID to PMNS.
     */
    public static final String POST_CLIENT_ID = "ClientId: ";
    /**
     * Regular prefix to post the role to PMNS.
     */
    public static final String POST_ROLE = "Role: ";
    /**
     * Receiver role.
     */
    public static final String RECEIVER = "Receiver";
    /**
     * Port of PMNS.
     */
    private static final int PMSN_PORT = 83;
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
     * Socket to PMNS.
     */
    private Socket connection;

    /**
     * Constructor.
     *
     * @param context: Context of application
     * @param clientId: Client ID
     */
    public THoldConnection(Context context, UUID clientId) {
        this.clientId = clientId;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            // Connect to PMNS
            connection = new Socket(ServerUtils.IP, PMSN_PORT);
            Log.d("DEBUG", "Connected to server.");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            // Client ID is unique so that PMNS can distinguish between the clients.
            bw.write(POST_CLIENT_ID + clientId.toString());
            bw.newLine();
            // Role of receiver as the client receives push notifications
            bw.write(POST_ROLE + RECEIVER);
            bw.newLine();
            bw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // Connection is kept open
            String expectedNotificationMessage;
            while (true) {
                // Thread is blocked
                expectedNotificationMessage = br.readLine();
                if (expectedNotificationMessage == null) {
                    Log.d("PMSN", "End of stream");
                    return;
                }
                String notification = getNotificationMessage(expectedNotificationMessage);
                if (notification != null) {
                    // Log when message arrived to the client
                    flavorLogger.infoForNotificationArrivedAtClient(notification);

                    // Show notification at the notification tray
                    NotificationReceiver.triggerNotification(context, notification);
                }
            }
            // If a connection error occurs, try reconnecting
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
            Log.e("Exception", e.toString());
        }
    }

    /**
     * Gets the notification text in the message sent by PMNS with a regular expresion.
     *
     * @param expectedNotificationMessage: Message which contains a notification text
     * @return Notification text
     */
    public static String getNotificationMessage(String expectedNotificationMessage) {
        if (!expectedNotificationMessage.matches(POST_NOTIFICATION + ".*")) return null;
        // Message can also be empty string
        String[] clientRolePart = expectedNotificationMessage.split(POST_NOTIFICATION);
        return clientRolePart[1];
    }

    @Override
    public void interrupt() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                Log.e("Exception", e.toString());
            }
        }
        Log.d(getClass().getSimpleName(), "Closed connection to PSMN");
        super.interrupt();
    }

    /**
     * Reconnecting to PMNS.
     */
    private void reconnect() {
        run();
    }
}
