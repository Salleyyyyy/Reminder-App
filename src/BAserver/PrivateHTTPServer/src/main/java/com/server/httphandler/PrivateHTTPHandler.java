package com.server.httphandler;

import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httpserver.AbstractPrivateHTTPServer;
import com.server.httpserver.ServerFunctions;

/**
 * This class provides all functions for handlers of a private (custom) HTTP
 * server.
 */
public class PrivateHTTPHandler extends HTTPHandler {

	/**
	 * Abstract private (custom) HTTP server.
	 */
	protected AbstractPrivateHTTPServer aPrivateHTTPServer;

	/**
	 * Constructor.
	 * 
	 * @param aPrivateHTTPServer: Abstract private (custom) HTTP server
	 */
	public PrivateHTTPHandler(AbstractPrivateHTTPServer aPrivateHTTPServer) {
		this.aPrivateHTTPServer = aPrivateHTTPServer;
	}

	/**
	 * Creates a new client ID.
	 * 
	 * @return Unique Client ID (UUID)
	 */
	protected UUID createNewClientId() {
		return UUID.randomUUID();
	}

	/**
	 * Stores a new client with the Client ID given.
	 * 
	 * @param clientId: Client ID of the client
	 */
	protected void addNewClient(UUID clientId) {
		aPrivateHTTPServer.newClient(clientId);
	}

	/**
	 * Returns the Client AlarmManager for the client with the Client ID given.
	 * 
	 * @param clientId: Client ID of the client
	 * @return Client AlarmManager for the client with Client ID
	 */
	protected ClientAlarmManager getClientAlarmManager(UUID clientId) {
		return aPrivateHTTPServer.getClientAlarmManager(clientId);
	}

	/**
	 * Checks if the Client ID as text is valid and checks if any client with this
	 * client ID is registered at the server.
	 * 
	 * @param expectedClientIdAsText: Client ID as text
	 * @return Client ID if client ID is valid and registered at the server.
	 */
	protected UUID isClientIdValid(String expectedClientIdAsText) {
		UUID clientId = getClientID(expectedClientIdAsText);
		if (clientId == null) {
			printError("Invalid client Id");
			sendErrorResponse(BAD_REQUEST);
			return null;
		}
		if (!isUserRegistered(clientId)) {
			printError("Client Id not registered");
			sendErrorResponse(FORBIDDEN);
			return null;
		}
		return clientId;
	}

	/**
	 * Checks if a user with the Client ID given is registered at the server.
	 * 
	 * @param clientId: Client ID of a possibly registered client
	 * @return true if a client with this Client ID is registered at the server,
	 *         otherwise false
	 */
	protected boolean isUserRegistered(UUID clientId) {
		return aPrivateHTTPServer.isUserRegistered(clientId);
	}

	protected void logInfo(String infoMessage) {
		aPrivateHTTPServer.printInfo("Received " + infoMessage);
	}

	/**
	 * Gets the Client ID in the text.
	 * 
	 * @param expectedClientIdAsText: Client ID as string
	 * @return Client ID (UUID) or null if no UUID can be constructed from the text
	 *         given
	 */
	private UUID getClientID(String expectedClientIdAsText) {
		return ServerFunctions.getClientID(expectedClientIdAsText);
	}
}
