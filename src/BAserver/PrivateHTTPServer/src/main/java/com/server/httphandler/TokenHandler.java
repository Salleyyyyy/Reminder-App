package com.server.httphandler;

import java.io.IOException;
import java.util.UUID;

import com.server.httpserver.FCMForwardingServer;
import com.server.httpserver.ServerFunctions;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class handles a HTTP exchange when a user pushes his FCM token to the
 * server. (Only for FCM push)
 */
public class TokenHandler extends PrivateHTTPHandler implements HttpHandler {

	/**
	 * Constructs this HTTP handler for this HTTP server.
	 * 
	 * @param fcmForwardingServer: Abstract private (custom) HTTP FCM Forwarding
	 *                             server
	 */
	public TokenHandler(FCMForwardingServer fcmForwardingServer) {
		super(fcmForwardingServer);
	}

	@Override
	public void handle(HttpExchange exchange) {
		setExchange(exchange);
		// Post HTTP method because the client ID is posted
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

			String clientTokenAsText;
			try {
				// FCM token in the HTTP request body as text
				clientTokenAsText = readLine();
			} catch (IOException e) {
				handleExceptionAndCloseExchange(e);
				return;
			}

			// Get the token in the text
			String token = ServerFunctions.getClientToken(clientTokenAsText);
			if (token == null) {
				printError("Token not valid!");
				sendErrorResponse(BAD_REQUEST);
				closeHTTPExchange();
				return;
			}

			// Sets (at the first time) or refreshes the FCM token of the client with
			// the client ID of the user
			((FCMForwardingServer) aPrivateHTTPServer).setTokenToFCMForwardingClient(clientId, token);
		} else {
			sendErrorResponse(HTTP_METHOD_NOT_ALLOWED);
		}
		closeHTTPExchange();
	}
}
