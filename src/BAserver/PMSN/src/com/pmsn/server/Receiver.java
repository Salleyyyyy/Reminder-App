package com.pmsn.server;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * This class represents a receiver of PMNS. A receiver is a client which registers for expected notifications sent by a sender.
 */
public class Receiver {

	private UUID receiverId;
	private Socket connection;
	private boolean connected = true;

	/**
	 * A receiver has to register with a UUID. A connection to a receiver is kept open for pushing notifications. 
	 * 
	 * @param receiverId: Unique UUID of a client for registration
	 * @param connection: Socket to client to push notifications by PMNS
	 */
	public Receiver(UUID receiverId, Socket connection) {
		this.receiverId = receiverId;
		this.connection = connection;
	}

	/**
	 * Checks if the client is currently online.
	 * 
	 * @return true if client is currently connected to PMNS, otherwise false
	 */
	public boolean isCurrentlyConnected() {
		return connected;
	}

	/**
	 * Reconnects a receiver to PMNS.
	 * 
	 * @param connection: New socket of receiver
	 */
	public void reconnected(Socket connection) {
		this.connection = connection;
		connected = true;
	}

	public UUID getReceiverId() {
		return receiverId;
	}

	public Socket getConnection() {
		return connection;
	}

	/**
	 * Closes the connection of a receiver to PMNS and marks him as not connected.
	 */
	public void closeConnection() {
		connected = false;
		try {
			connection.close();
		} catch (IOException e) {
			printError("Closing connection to receiver failed!");
			printException(e);
		}
		connection = null;
	}

	private void printError(String errorMessage) {
		System.out.println("ERROR: " + errorMessage);
	}

	private void printException(Exception exception) {
		System.out.println("EXCEPTION: " + exception);
	}

	/*
	 * TODO Not yet implemented, in case of when receiver has lost connection: Store
	 * dismissed messages
	 * 
	 * private List<String> messagesFailedToSend = new ArrayList<>();
	 * 
	 * public synchronized void addFailedSentMessage(String messageFailedToSend) {
	 * messagesFailedToSend.add(messageFailedToSend); }
	 * 
	 * public synchronized boolean anyMissedMessages() { return
	 * messagesFailedToSend.isEmpty(); }
	 * 
	 * public synchronized String getAMissedMessage() { return
	 * messagesFailedToSend.get(0); }
	 * 
	 * public synchronized void removeMissedMessage(String missedMessage) {
	 * messagesFailedToSend.remove(missedMessage); }
	 */
}
