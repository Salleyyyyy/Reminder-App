package com.server.httphandler;

import java.io.IOException;
import java.util.UUID;

import com.server.alarmmanagement.ClientAlarmManager;
import com.server.httpserver.AbstractPrivateHTTPServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class handles a HTTP exchange in which the client waits for a
 * notification. (Only for HTTP Long Polling Push)
 */
public class WaitForRemindHandler extends PrivateHTTPHandler implements HttpHandler {

	/**
	 * Constructs this HTTP handler for this HTTP server.
	 * 
	 * @param aPrivateHTTPServer: Abstract private (custom) HTTP server
	 */
	public WaitForRemindHandler(AbstractPrivateHTTPServer aPrivateHTTPServer) {
		super(aPrivateHTTPServer);
	}

	@Override
	public void handle(HttpExchange exchange) {
		setExchange(exchange);
		// POST Client ID
		if (isHTTPRequestMethod(HTTP_REQUEST_METHOD_POST)) {
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
				sendErrorResponse(BAD_REQUEST);
				closeHTTPExchange();
				return;
			}

			// Wait until a notification is available
			// Until a notification (= update) is available this connection will be kept
			// open
			ClientAlarmManager clientAlarmManager = getClientAlarmManager(clientId);
			while (!clientAlarmManager.anyNotificationAvailable())
				;

			// Be care of: Response has only one notification message per connection with
			// this implementation
			// No message priorities are considered !
			String notificationMessage = clientAlarmManager.getNotificationInfo().getNotificationMessage();
			try {
				sendResponse(notificationMessage);
			} catch (IOException e) {
				printException(e);
			}
		} else {
			sendErrorResponse(HTTP_METHOD_NOT_ALLOWED);
		}
		closeHTTPExchange();
	}
}
