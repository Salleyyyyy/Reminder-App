package com.server.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.alarmmanagement.PMSNClientAlarmManager;
import com.sun.net.httpserver.HttpServer;

/**
 * This class implements the server instance for pushing requests to PMNS.
 */
public class PMSNForwardingServer extends AbstractPrivateHTTPServer {

	private static final int PMSN_FORWARDING_PORT = 82;

	/**
	 * Single PMNS instance
	 */
	private static PMSNForwardingServer PMSN_FS_ServerInstance = new PMSNForwardingServer();
	/**
	 * Hash Map to store all ClientAlarmManagers by client ID
	 */
	private HashMap<UUID, PMSNClientAlarmManager> clientAlarmManagers = new HashMap<>();
	/**
	 * Private mobile notification server for sending push requests
	 */
	private HttpServer PMSN_FS;

	private PMSNForwardingServer() {
	}

	public static PMSNForwardingServer getServerInstance() {
		return PMSN_FS_ServerInstance;
	}

	@Override
	public void newClient(UUID clientId) {
		clientAlarmManagers.put(clientId, new PMSNClientAlarmManager(clientId));
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
		// PMSN Forwarding Server port: 82
		if (PMSN_FS != null) {
			System.out.println("PMSN Forwarding Server is already running!");
		}

		try {
			PMSN_FS = HttpServer.create(new InetSocketAddress(PMSN_FORWARDING_PORT), 0);
		} catch (IOException e) {
			System.out.println("Problem when starting PMSN_FS!");
			System.out.println("ERROR: " + e);
			PMSN_FS = null;
			return;
		}
		// Support all standard functions for handling remind services
		super.supportStandardFunctions(PMSN_FS, clientAlarmManagers);
		super.setExecutor(PMSN_FS);

		PMSN_FS.start();
	}

	public void close() {
		System.out.println("PMSN Forwarding Server gets closed");
		super.close(PMSN_FS, clientAlarmManagers);
	}
}
