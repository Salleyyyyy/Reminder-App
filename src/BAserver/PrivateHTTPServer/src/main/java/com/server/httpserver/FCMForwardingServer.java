package com.server.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.server.alarmmanagement.FCMClientAlarmManager;
import com.server.httphandler.TokenHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This class implements the server instance that sends or forwards push
 * requests to the FCM Backend of Google.
 */
public class FCMForwardingServer extends AbstractPrivateHTTPServer {

	/**
	 * Path to JSON file which is needed for server side authentication for FCM
	 */
	private static final String FILE_PATH = "serviceAccountKey.json";
	private static final int FCM_FORWARDING_PORT = 81;

	/**
	 * Single server instance
	 */
	private static FCMForwardingServer FCM_FS_ServerInstance = new FCMForwardingServer();
	/**
	 * Hash Map to store all ClientAlarmManagers by client ID
	 */
	private HashMap<UUID, FCMClientAlarmManager> clientAlarmManagers = new HashMap<>();
	/**
	 * FCM forwarding server
	 */
	private HttpServer FCM_FS;
	/**
	 * Firebase App for authentication towards FCM
	 */
	private FirebaseApp firebaseApp;

	private FCMForwardingServer() {
	}

	public static FCMForwardingServer getServerInstance() {
		return FCM_FS_ServerInstance;
	}

	@Override
	public boolean isUserRegistered(UUID clientId) {
		return clientAlarmManagers.containsKey(clientId);
	}

	@Override
	public void newClient(UUID clientId) {
		clientAlarmManagers.put(clientId, new FCMClientAlarmManager(clientId, firebaseApp));
	}

	@Override
	public FCMClientAlarmManager getClientAlarmManager(UUID clientId) {
		return clientAlarmManagers.get(clientId);
	}

	/**
	 * Sets the token to be saved with the client ID registered at the private
	 * server to specifically push a notification to a certain client
	 * 
	 * @param clientId:        Client ID of receiver
	 * @param registeredToken: Token of client for FCM
	 */
	public void setTokenToFCMForwardingClient(UUID clientId, String registeredToken) {
		getClientAlarmManager(clientId).setToken(registeredToken);
	}

	@Override
	public void start() {
		// FCM Forwarding Server port: 81
		if (FCM_FS != null) {
			System.out.println("FCM Forwarding Server is already running!");
		}

		// Authorize towards FCM Services
		firebaseApp = authorizePrivateServerToFCMServices();
		if (firebaseApp == null) {
			System.out.println("ERROR: Firebase Initialization failed!");
			return;
		}

		try {
			FCM_FS = HttpServer.create(new InetSocketAddress(FCM_FORWARDING_PORT), 0);
		} catch (IOException e) {
			System.out.println("Problem when starting FCM_FS");
			System.out.println("ERROR: " + e);
			FCM_FS = null;
			return;
		}
		// Support all standard functions for handling remind services
		super.supportStandardFunctions(FCM_FS, clientAlarmManagers);

		// Custom Token Handler to receive tokens of a client
		TokenHandler tokenHandler = new TokenHandler(this);
		FCM_FS.createContext("/token", tokenHandler);

		super.setExecutor(FCM_FS);

		FCM_FS.start();
	}

	public void close() {
		System.out.println("FCM Forwarding Server gets closed");
		super.close(FCM_FS, clientAlarmManagers);
		firebaseApp.delete();
		firebaseApp = null;
	}

	/**
	 * Authorizes this server towards the FCM Backend of Google
	 * 
	 * @return Firebase that contains all authentication options
	 */
	@SuppressWarnings("deprecation")
	private FirebaseApp authorizePrivateServerToFCMServices() {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream(FILE_PATH);
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return null;
		}

		FirebaseOptions options;
		try {
			// Sets all credentials in the file to authenticate towards FCM
			options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
		return FirebaseApp.initializeApp(options);
	}
}
