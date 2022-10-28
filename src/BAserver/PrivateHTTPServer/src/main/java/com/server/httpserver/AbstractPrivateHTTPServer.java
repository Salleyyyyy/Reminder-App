package com.server.httpserver;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httphandler.ClientIDHandler;
import com.server.httphandler.GetRegisteredRemindServicesHandler;
import com.server.httphandler.RemindServiceHandler;
import com.server.logging.ServerLogger;

/**
 * This abstract class provides abstract methods for all private HTTP Push
 * Server.
 */
public abstract class AbstractPrivateHTTPServer {

	/**
	 * Not used and can be ignored.
	 */
	private ServerLogger serverLogger;

	/**
	 * Max number of connections accepted of a private http server
	 */
	private static final int MAX_NUM_OF_CONNECTIONS = 100;

	/**
	 * Starts the HTTP server instance.
	 */
	public abstract void start();

	/**
	 * Registers a new client with a UUID.
	 * 
	 * @param clientId: Unique Client ID
	 */
	public abstract void newClient(UUID clientId);

	/**
	 * Checks if user with this client ID is already registered in the server.
	 * 
	 * @param clientId: Unique Client ID
	 * @return true if client with this client ID is already registered, otherwise
	 *         false
	 */
	public abstract boolean isUserRegistered(UUID clientId);

	public abstract ClientAlarmManager getClientAlarmManager(UUID clientId);

	public void printInfo(String infoMessage) {
		serverLogger.printInfo(infoMessage);
	}

	/**
	 * Initializes all http handlers for standard functions (Client ID handling and
	 * remind service handling) of all private http server
	 * 
	 * @param httpServer:          HTTPServer object for adding HTTP Handlers which
	 * @param clientAlarmManagers: Hash Map of all Client Alarm Manager
	 */
	protected void supportStandardFunctions(HttpServer httpServer,
			HashMap<UUID, ? extends ClientAlarmManager> clientAlarmManagers) {
		// Logger can be ignored
		serverLogger = new ServerLogger(getClass().getSimpleName());

		ClientIDHandler clientIDHandler = new ClientIDHandler(this);
		RemindServiceHandler remindServiceHandler = new RemindServiceHandler(this);
		GetRegisteredRemindServicesHandler getRegisteredRemindServicesHandler = new GetRegisteredRemindServicesHandler(
				this);

		// All standard functions are reachable with those URLs
		httpServer.createContext("/", clientIDHandler);
		httpServer.createContext("/remindService", remindServiceHandler);
		httpServer.createContext("/getRegisteredRemindServices", getRegisteredRemindServicesHandler);
	}

	protected void setExecutor(HttpServer httpServer) {
		// Executor handles parallel all requests with a fix number of accepted
		// connections
		ExecutorService executorService = Executors.newFixedThreadPool(MAX_NUM_OF_CONNECTIONS);
		httpServer.setExecutor(executorService);
	}

	protected void close(HttpServer httpServer, HashMap<UUID, ? extends ClientAlarmManager> clientAlarmManagers) {
		httpServer.stop(0);
		clientAlarmManagers.clear();
		httpServer = null;
	}
}