package com.ba.reminder.remote;

import com.flavor.reminder.PushTechnology;

/**
 * This class contains all constants for urls and connection relevant info.
 */
public class ServerUtils {
    /**
     * Connection timeout after a fixed number of seconds.
     */
    public static final int CONNECTION_TIMEOUT = 7000;
    /**
     * Constant for no or infinite connection timeout.
     */
    public static final int INFINITE_CONNECTION_TIMEOUT = 0;
    /** Localhost address for emulator. */
    public static final String IP = "10.0.2.2";
    /**
     * URL to connect.
     */
    public static final String URL = "http://" + IP + ":" + PushTechnology.PORT + "/";
    /**
     * URL for registration of a remind service.
     */
    public static final String POST_REMIND_SERVICE_URL = URL + "remindService";
    /**
     * URL for sending the FCM token.
     */
    public static final String POST_TOKEN_URL = URL + "token";
    /**
     * URL for getting the list of all registered remind services.
     */
    public static final String GET_REMIND_OBJECT_LIST_URL = URL + "getRegisteredRemindServices";
    /**
     * URL for HTTP Long Polling and actively waiting for push notifications.
     */
    public static final String WAIT_FOR_REMIND_URL = URL + "waitForRemind";
    /**
     * Regular prefix for posting the client ID.
     */
    public static final String POST_CLIENT_ID_REGEX = "ClientId: ";
    /**
     * Regular prefix for posting the FCM token.
     */
    public static final String POST_CLIENT_TOKEN_REGEX = "Token: ";
    /**
     * Key for storing the client ID at the client.
     */
    public static final String clientId_key = "clientId";
}
