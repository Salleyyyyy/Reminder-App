package com.server.httphandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class provides all functions to handle basically a HTTP communication
 * for receiving HTTP requests and send HTTP response in one HTTP exchange.
 */
public class HTTPHandler {

	/**
	 * HTTP response code for success
	 */
	protected static final int OK = 200;
	/**
	 * HTTP response code for a false formed request
	 */
	protected static final int BAD_REQUEST = 400;
	/**
	 * HTTP response code if the operation requested is forbidden
	 */
	protected static final int FORBIDDEN = 403;
	/**
	 * HTTP response code for a not allowed http method
	 */
	protected static final int HTTP_METHOD_NOT_ALLOWED = 405;

	protected static final String HTTP_REQUEST_METHOD_POST = "POST";

	/**
	 * HTTP Exchange to receive HTTP request and send HTTP response in one exchange
	 */
	private HttpExchange exchange;
	/**
	 * Buffered Reader to read a HTTP request
	 */
	private BufferedReader br;
	/**
	 * Buffered Writer to write a HTTP response
	 */
	private BufferedWriter bw;

	/**
	 * Sets the HTTP exchange for receiving requests and sending a response.
	 * 
	 * @param exchange: HTTP Exchange
	 */
	public void setExchange(HttpExchange exchange) {
		this.exchange = exchange;
		this.br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
		this.bw = new BufferedWriter(new OutputStreamWriter(exchange.getResponseBody()));
	}

	/**
	 * Checks if the HTTP method in the HTTP request is this.
	 * 
	 * @param httpRequestMethod: HTTP request method (e.g. GET)
	 * @return true if HTTP method in the HTTP request is this, otherwise false
	 */
	protected boolean isHTTPRequestMethod(String httpRequestMethod) {
		return getRequestMethod().equals(httpRequestMethod);
	}

	/**
	 * Reads a line in the stream of HTTP requests.
	 * 
	 * @return Read line
	 * @throws IOException
	 */
	protected String readLine() throws IOException {
		String receivedMessage = br.readLine();
		printInfo("Received " + receivedMessage);
		return receivedMessage;
	}

	/**
	 * Closes the HTTP exchange and handles all thrown exceptions if there are any.
	 * 
	 * @param e: Thrown Exception while closing the HTTP Exchange
	 */
	protected void handleExceptionAndCloseExchange(IOException e) {
		printException(e);
		closeHTTPExchange();
	}

	/**
	 * Sends a HTTP response to the client with the message given.
	 * 
	 * @param message: Message (notification) text
	 * @throws IOException
	 */
	protected void sendResponse(String message) throws IOException {
		printInfo("Sending " + message);
		sendResponseHeaders(OK, message.length());
		sendResponseBody(message);
	}

	/**
	 * Closes the HTTP Exchange.
	 */
	protected void closeHTTPExchange() {
		exchange.close();
	}

	/**
	 * Sends an error response with this HTTP errorCode.
	 * 
	 * @param errorCode: HTTP error code
	 */
	protected void sendErrorResponse(int errorCode) {
		try {
			// -1 because the error message has no length
			sendResponseHeaders(errorCode, -1);
		} catch (IOException e) {
			printException(e);
		}
	}

	protected void printError(String error) {
		System.out.println("ERROR: " + error);
	}

	public void printException(IOException exception) {
		System.out.println("EXCEPTION: " + exception);
	}

	private void printInfo(String info) {
		System.out.println("INFO: " + info);
	}

	/**
	 * Gets the request method of the HTTP request.
	 * 
	 * @return Request method name.
	 */
	private String getRequestMethod() {
		return exchange.getRequestMethod();
	}

	/**
	 * Sends the response with the HTTP response code (e.g. 200 for success) and the
	 * response length.
	 * 
	 * @param responseCode:   HTTP response Code
	 * @param responseLength: Length of the response
	 * @throws IOException
	 */
	private void sendResponseHeaders(int responseCode, int responseLength) throws IOException {
		exchange.sendResponseHeaders(responseCode, responseLength);
	}

	/**
	 * Sends the HTTP response body with the message given.
	 * 
	 * @param message: Message (text) in the HTTP response
	 * @throws IOException
	 */
	private void sendResponseBody(String message) throws IOException {
		bw.write(message);
		bw.flush();
	}
}