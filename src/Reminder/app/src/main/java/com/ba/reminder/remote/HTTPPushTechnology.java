package com.ba.reminder.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.server.model.RemindService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class has all functionalities for all push technologies like Client ID handling, registration, cancel or request of remind services.
 */
public class HTTPPushTechnology {

    /**
     * Client ID
     */
    protected UUID clientId;
    /**
     * Local storage for persisting the client ID
     */
    private SharedPreferences localStorage;

    /**
     * Constructor.
     *
     * @param context: Context of application
     * @param localStorageFileName: Name of the file which stores the client ID
     */
    public HTTPPushTechnology(Context context, String localStorageFileName) {
        initLocalStorage(context, localStorageFileName);
        initClientId();
    }

    /**
     * Sends a remind service for registration or canceling.
     * Whether it will be a registration or a cancel depends on the remind flag of the remind service.
     *
     * @param remindService: Remind service
     */
    protected void sendRemindService(RemindService remindService) {
        Thread TSendRemindService = new TSendRemindService(remindService, clientId);
        TSendRemindService.start();
    }

    /**
     * Requests for all server-side registered (active) remind service of the user.
     *
     * @return List of all remind server (currently active) of the user
     */
    protected List<RemindService> getRegisteredRemindServices() {
        // Result list to capture all remind services received from the server
        AtomicReference<ArrayList<RemindService>> remindServiceList = new AtomicReference<>(new ArrayList<>());
        Thread TGetRegisteredRemindServices = new TGetRegisteredRemindServices(clientId, remindServiceList);
        TGetRegisteredRemindServices.start();
        try {
            // Wait until registered remind services are received
            TGetRegisteredRemindServices.join();
        } catch (InterruptedException e) {
            Log.e("Exception", e.toString());
        }
        return remindServiceList.get();
    }

    /**
     * Initializes the client ID.
     * Connects to server to receive a client ID if not already saved locally.
     */
    private void initClientId() {
        // Check whether a client ID is already stored
        if (isAnyClientIdAvailable()) {
            initClientIdFromLocalStorage();
        } else {
            initClientIdFormServer();
        }
    }

    /**
     * Initizializes the local storage
     *
     * @param context: Context of application
     * @param localStorageFileName: Path to the local storage
     */
    private void initLocalStorage(Context context, String localStorageFileName) {
        localStorage = context.getSharedPreferences(localStorageFileName, Context.MODE_PRIVATE);
    }

    /**
     * Checks whether a client ID has been already saved after receiving from server.
     *
     * @return true if there is a Client ID, otherwise false
     */
    private boolean isAnyClientIdAvailable() {
        String noValue = "";
        return !localStorage.getString(ServerUtils.clientId_key, noValue).equals(noValue);
    }

    /**
     * Initializes the client id after retrieving from local storage.
     */
    private void initClientIdFromLocalStorage() {
        String noValue = "";
        String storedClientId = localStorage.getString(ServerUtils.clientId_key, noValue);
        if (storedClientId.equals(noValue)) return;
        clientId = UUID.fromString(storedClientId);
    }

    /**
     * Initializes the client ID received from the server while connecting.
     */
    private void initClientIdFormServer() {
        connectToServer();
        // If Client ID is received successfully, it is stored locally.
        if (isAnyClientIdAvailable()) {
            initClientIdFromLocalStorage();
        } else {
            Log.e("ClientId:", "ClientId not received from server!");
        }
    }

    /**
     * Starts a connection to the server in order to get a client ID.
     */
    private void connectToServer() {
        Thread TConnectToServer = new TConnect(localStorage);
        TConnectToServer.start();
        try {
            TConnectToServer.join();
        } catch (InterruptedException e) {
            Log.e("Connection:", "Connecting to server has been interrupted!");
        }
    }
}
