package com.pmsn.server;

import java.io.IOException;
import java.net.ServerSocket;

import com.pmsn.constants.PMSNNetworkInfo;

/**
 * This class contains all methods for PMNS (or PMSN).
 */
public class PrivateMobileServerNotification {

	public static final String PMSN_IP = PMSNNetworkInfo.PMSN_IP;
	public static final int PMSN_PORT = PMSNNetworkInfo.PMSN_PORT;
	/**
	 * Max number of accepted connections
	 */
	private static final int MAX_NUM_OF_CONNECTIONS = 100;
	/**
	 * Singleton instance for PMNS
	 */
	private static PrivateMobileServerNotification PMSN_ServerInstance = new PrivateMobileServerNotification();

	/**
	 * Thread which listens on PMNS port for connections.
	 */
	private Thread listenToIncommingSockets;
	/**
	 * Server instance
	 */
	private ServerSocket serverSocket;
	/**
	 * Storage for all registered clients (receiver) by PMNS
	 */
	private ReceiverStorage registeredReceiver = new ReceiverStorage();

	private PrivateMobileServerNotification() {
	}

	/**
	 * Returns the single PMNS instance.
	 * 
	 * @return PMNS instance
	 */
	public static PrivateMobileServerNotification getServerInstance() {
		return PMSN_ServerInstance;
	}

	/**
	 * Starts PMNS. 
	 * A thread is listening on PMNS port to accept connections.
	 */
	public void start() {
		if (serverSocket != null) {
			printInfo("PSMN already running!");
			return;
		}

		try {
			printInfo("Start PMSN!");
			startPMSN();
		} catch (IOException e) {
			printError("Problem starting PSMN!");
			printException(e);
			return;
		}

		printInfo("PMSN is now listening on port: " + PMSN_PORT + " !");
		listenToIncommingSockets = new TListenToIncomingSockets(serverSocket, registeredReceiver);
		listenToIncommingSockets.start();
	}

	/**
	 * Closes the PMNS instance, deletes all registered receivers and goes
	 * back to default state.
	 */
	public void close() {
		if (serverSocket != null) {
			try {
				printInfo("Close PMSN!");
				closePMSN();
			} catch (IOException e) {
				printError("Server could not be closed");
				printException(e);
			}
			registeredReceiver.clearStorage();
			listenToIncommingSockets = null;
			serverSocket = null;
		} else {
			printInfo("PMSN has not been already started!");
		}
	}

	/**
	 * PMNS instance is created on PMNS port and a fixed max number of connections.
	 * 
	 * @throws IOException
	 */
	private void startPMSN() throws IOException {
		serverSocket = new ServerSocket(PMSN_PORT, MAX_NUM_OF_CONNECTIONS);
	}

	/**
	 * Closes the PMNS instance.
	 * 
	 * @throws IOException
	 */
	private void closePMSN() throws IOException {
		serverSocket.close();
	}

	private void printInfo(String infoMessage) {
		System.out.println("INFO: " + infoMessage);
	}

	private void printError(String errorMessage) {
		System.out.println("ERROR: " + errorMessage);
	}

	private void printException(Exception exception) {
		System.out.println("EXCEPTION: " + exception);
	}
}
