package com.server.httphandler;

import java.io.IOException;
import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httpserver.AbstractPrivateHTTPServer;
import com.server.model.RemindService;
import com.server.model.parse.RemindServiceJsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class handles a HTTP exchange for handling a registration or cancel of a
 * remind service.
 */
public class RemindServiceHandler extends PrivateHTTPHandler implements HttpHandler {

	/**
	 * RemindServiceJSONParser to deserialize remind services from JSON
	 */
	private RemindServiceJsonParser remindObjectJsonParser = new RemindServiceJsonParser();

	/**
	 * Constructs this HTTP handler for this HTTP server.
	 * 
	 * @param aPrivateHTTPServer: Abstract private (custom) HTTP server
	 */
	public RemindServiceHandler(AbstractPrivateHTTPServer aPrivateHTTPServer) {
		super(aPrivateHTTPServer);
	}

	@Override
	public void handle(HttpExchange exchange) {
		setExchange(exchange);
		// Post HTTP method because the client ID is posted
		if (isHTTPRequestMethod(HTTP_REQUEST_METHOD_POST)) {
			// Request body should contain ClientId as a string
			String clientIdAsText;
			try {
				clientIdAsText = readLine();
			} catch (IOException e) {
				handleExceptionAndCloseExchange(e);
				return;
			}

			// Get client Id from request body and validate
			UUID clientId = isClientIdValid(clientIdAsText);
			if (clientId == null) {
				printError("Client Id not valid");
				closeHTTPExchange();
				return;
			}

			String remindObjectAsJson;
			try {
				// Remind service as JSON in the HTTP request body
				remindObjectAsJson = readLine();
			} catch (IOException e) {
				handleExceptionAndCloseExchange(e);
				return;
			}

			RemindService remindService;
			try {
				// Remind service is deserialized from the JSON
				remindService = remindObjectJsonParser.readRemindServiceFromJson(remindObjectAsJson);
			} catch (Exception jsonSyntaxException) {
				printError("Received JSON not valid!");
				closeHTTPExchange();
				return;
			}

			// Logging info that remind service arrived
			logInfo(remindObjectAsJson);

			ClientAlarmManager clientAlarmManager = getClientAlarmManager(clientId);
			// Register or cancel remind service received
			clientAlarmManager.registerOrCancelRemindService(remindService);

			try {
				sendResponse("Remind Service successfully registered!");
			} catch (IOException e) {
				printException(e);
			}
		} else {
			sendErrorResponse(HTTP_METHOD_NOT_ALLOWED);
		}
		closeHTTPExchange();
	}
}
