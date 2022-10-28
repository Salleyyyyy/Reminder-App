package com.pmsn.server;

import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class represents a storage for registered receivers of PMNS.
 */
public class ReceiverStorage {

	private HashMap<UUID, Receiver> receiverStore = new HashMap<>();

	/**
	 * Adds a new receiver to PMNS.
	 * 
	 * @param receiver: Receiver who registers to PMNS
	 */
	public void addNewReceiver(Receiver receiver) {
		UUID receiverID = receiver.getReceiverId();
		// Check if receiver is already registered in the receiver store
		if (receiverStore.containsKey(receiverID)) {
			printInfo("User with ID " + receiverID + " is already registered at PMSN");
			Receiver alreadyRegisteredUser = receiverStore.get(receiverID);
			// Receiver has probably reconnected to PMNS e.g. because of connection loss
			alreadyRegisteredUser.reconnected(receiver.getConnection());
			return;
		}
		printInfo("New User registered at PMSN: " + receiverID);
		receiverStore.put(receiverID, receiver);
	}

	/**
	 * Disconnects a receiver from PMNS.
	 * 
	 * @param receiver: disconnected receiver 
	 */
	public void disconnectReceiver(Receiver receiver) {
		// Receivers with once registered client Id are kept in the storage for reconnecting at a later time
		receiver.closeConnection();
	}

	/**
	 * Deletes all registered receivers in the storage and closes properly all connections.
	 */
	public void clearStorage() {
		Collection<Receiver> allRegisteredReceiver = receiverStore.values();
		for (Receiver receiver : allRegisteredReceiver) {
			receiver.closeConnection();
		}
		receiverStore.clear();
	}

	/**
	 * Checks if receiver with UUID is already registered.
	 * 
	 * @param receiverId: client Id of possible receiver
	 * @return true if receiver is already registered, otherwise false
	 */
	public boolean isReceiverRegistered(UUID receiverId) {
		return receiverStore.containsKey(receiverId);
	}

	public Receiver getReceiver(UUID receiverId) {
		return receiverStore.get(receiverId);
	}

	public Socket getConnection(UUID receiverId) {
		return getReceiver(receiverId).getConnection();
	}

	private void printInfo(String info) {
		System.out.println("INFO: " + info);
	}
}
