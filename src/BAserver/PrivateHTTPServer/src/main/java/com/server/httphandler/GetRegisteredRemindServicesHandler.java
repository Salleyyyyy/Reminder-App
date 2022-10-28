package com.server.httphandler;

import java.io.IOException;
import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httpserver.AbstractPrivateHTTPServer;
import com.server.model.parse.RemindServiceJsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class handles the HTTP exchange for receiving the list of all remind
 * service that the user registered for.
 */
public class GetRegisteredRemindServicesHandler extends PrivateHTTPHandler implements HttpHandler {

	private RemindServiceJsonParser remindObjectJsonParser = new RemindServiceJsonParser();

	/**
	 * Constructs this HTTP handler for this HTTP server.
	 * 
	 * @param aPrivateHTTPServer: Abstract private (custom) HTTP server
	 */
	public GetRegisteredRemindServicesHandler(AbstractPrivateHTTPServer aPrivateHTTPServer) {
		super(aPrivateHTTPServer);
	}

	@Override
	public void handle(HttpExchange exchange) {
		setExchange(exchange);
		// Post HTTP method because the Client ID is posted to the server
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
				printError("ClientId not valid");
				sendErrorResponse(BAD_REQUEST);
				closeHTTPExchange();
				return;
			}

			// Get the list of all remind services in JSON registered from the user with
			// this client ID
			ClientAlarmManager clientAlarmManager = getClientAlarmManager(clientId);
			String remindObjectList_json = remindObjectJsonParser
					.parseToJson(clientAlarmManager.getRegisteredRemindServices());
			try {
				sendResponse(remindObjectList_json);
			} catch (IOException e) {
				handleExceptionAndCloseExchange(e);
				return;
			}
		} else {
			sendErrorResponse(HTTP_METHOD_NOT_ALLOWED);
		}
		closeHTTPExchange();
	}
}
