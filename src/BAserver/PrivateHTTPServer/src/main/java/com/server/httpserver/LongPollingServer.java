package com.server.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httphandler.WaitForRemindHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This class implements the server instance for pushing notifications with HTTP
 * Long Polling.
 */
public class LongPollingServer extends AbstractPrivateHTTPServer {

	private static final int LONG_POLLING_PORT = 80;

	/**
	 * Single Long Polling server instance
	 */
	private static LongPollingServer LPS_ServerInstance = new LongPollingServer();
	/**
	 * Hash Map to store all ClientAlarmManagers by client ID
	 */
	private HashMap<UUID, ClientAlarmManager> clientAlarmManagers = new HashMap<>();
	/**
	 * HTTP long polling server
	 */
	private HttpServer LPS;

	private LongPollingServer() {
	}

	/**
	 * Gets single long polling server instance
	 * 
	 * @return Single HTTP long polling server instance
	 */
	public static LongPollingServer getServerInstance() {
		return LPS_ServerInstance;
	}

	@Override
	public void newClient(UUID clientId) {
		clientAlarmManagers.put(clientId, new ClientAlarmManager(clientId));
	}

	@Override
	public boolean isUserRegistered(UUID clientId) {
		return clientAlarmManagers.containsKey(clientId);
	}

	@Override
	public ClientAlarmManager getClientAlarmManager(UUID clientId) {
		return clientAlarmManagers.get(clientId);
	}

	@Override
	public void start() {
		// Long Polling Server port: 80
		if (LPS != null) {
			System.out.println("Long Polling Server is already running!");
		}

		try {
			LPS = HttpServer.create(new InetSocketAddress(LONG_POLLING_PORT), 0);
		} catch (IOException e) {
			System.out.println("Problem when starting LPS!");
			System.out.println("ERROR: " + e);
			LPS = null;
			return;
		}
		// Support all standard functions for handling remind services
		super.supportStandardFunctions(LPS, clientAlarmManagers);

		// Custom handlers for pushing notifications through a HTTP Long Polling request
		// lifecycle
		WaitForRemindHandler waitForRemindHandler = new WaitForRemindHandler(this);
		LPS.createContext("/waitForRemind", waitForRemindHandler);

		super.setExecutor(LPS);

		LPS.start();
	}

	public void close() {
		System.out.println("Long Polling Server gets closed");
		super.close(LPS, clientAlarmManagers);
	}
}
