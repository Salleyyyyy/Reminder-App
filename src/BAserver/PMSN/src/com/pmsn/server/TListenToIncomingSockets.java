package com.pmsn.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This thread listens to built connections to PMNS and process all incoming requests.
 */
public class TListenToIncomingSockets extends Thread {

	private final ServerSocket serverSocket;
	private final ReceiverStorage receiverStorage;

	/**
	 * Creates a thread to listen to sockets to PMNS.
	 * 
	 * @param serverSocket: Server Socket in order to accept new clients.
	 * @param receiverStorage: A storage to save receivers to retrieve for pushing notifications
	 */
	public TListenToIncomingSockets(ServerSocket serverSocket, ReceiverStorage receiverStorage) {
		this.serverSocket = serverSocket;
		this.receiverStorage = receiverStorage;
	}

	@Override
	public void run() {
		boolean listenToInCommingConnection = false;
		while (!listenToInCommingConnection) {
			Socket client = null;
			try {
				// Connection is accepted
				client = serverSocket.accept();
			} catch (SocketException e) {
				printInfo("Listening to incoming connection is stopped!");
				listenToInCommingConnection = true;
			} catch (IOException e) {
				printError("Error while waiting for connection!");
				printException(e);
			}
			if (client != null) {
				// Processing received request 
				Thread TProcessClient = new TProcessClient(client, receiverStorage);
				TProcessClient.start();
			}
		}
	}

	private void printInfo(String info) {
		System.out.println("INFO: " + info);
	}

	private void printError(String errorMessage) {
		System.out.println("ERROR: " + errorMessage);
	}

	private void printException(Exception exception) {
		System.out.println("EXCEPTION: " + exception);
	}
}
