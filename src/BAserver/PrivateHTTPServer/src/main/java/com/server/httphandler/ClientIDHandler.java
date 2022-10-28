package com.server.httphandler;

import java.io.IOException;
import java.util.UUID;

import com.sun.net.httpserver.HttpHandler;
import com.server.httpserver.AbstractPrivateHTTPServer;
import com.sun.net.httpserver.HttpExchange;

/**
 * This class handles the HTTP exchange for a new client that has connected to
 * the HTTP server.
 */
public class ClientIDHandler extends PrivateHTTPHandler implements HttpHandler {

	/**
	 * Constructs this HTTP handler for this HTTP server.
	 * 
	 * @param aPrivateHTTPServer: Abstract private (custom) HTTP server
	 */
	public ClientIDHandler(AbstractPrivateHTTPServer aPrivateHTTPServer) {
		super(aPrivateHTTPServer);
	}

	@Override
	public void handle(HttpExchange exchange) {
		setExchange(exchange);
		UUID newClientId = createNewClientId();
		try {
			// Send the Client ID created to the client
			// For further requests this must be used in the HTTP requests
			sendResponse(newClientId.toString());
		} catch (IOException e) {
			handleExceptionAndCloseExchange(e);
			return;
		}
		// New client is registered with this client ID
		addNewClient(newClientId);
		closeHTTPExchange();
	}
}
