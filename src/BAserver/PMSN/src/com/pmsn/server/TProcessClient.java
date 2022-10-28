package com.pmsn.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import com.pmsn.constants.ServerFunctions;

/**
 * This thread processes received requests of PMNS. 
 */
public class TProcessClient extends Thread {

	private static final int RETRY_SENDING = 3;
	private static final int MILLISECONDS_WAIT_TO_RETRY_SENDING = 5 * 1000;
	private final Socket conncetion;
	private final ReceiverStorage receiverStorage;
	private BufferedReader reader;

	/**
	 * Client process thread is created. 
	 * If a receiver requests for registration by PMNS, it will be stored.
	 * 
	 * @param conncetion: Connection of receiver
	 * @param receiverStorage: Storage of receivers to save registered receivers
	 */
	public TProcessClient(Socket conncetion, ReceiverStorage receiverStorage) {
		this.conncetion = conncetion;
		this.receiverStorage = receiverStorage;
	}

	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(conncetion.getInputStream()));
			processConnection(conncetion);
		} catch (IOException e) {
			printError("Error while processing client!");
			printException(e);
		}
	}

	private void closeConnection() {
		try {
			conncetion.close();
		} catch (IOException e) {
			String error = "Closing connection failed!";
			printError(error);
			printException(e);
		}
	}

	/**
	 * Process received requests and check for validity of requests.
	 * 
	 * @param connection: Socket which has the sent request
	 * @throws IOException
	 */
	private void processConnection(Socket connection) throws IOException {
		// First line: ClientId
		UUID clientId = readClientId();
		if (clientId == null)
			return;

		// Second line: Role
		String role = readRole();
		if (role == null)
			return;

		// Receiver is client as he receives the notification
		if (role.equals(ServerFunctions.RECEIVER)) {
			// A client has (re-)connected to PMSN
			Receiver receiver = new Receiver(clientId, connection);
			registerNewReceiver(receiver);
		}
		// Sender is our private server as he request for push
		else if (role.equals(ServerFunctions.SENDER)) {
			// Third line: Notification message
			String notificationMessage = readNotificationMessage();
			if (notificationMessage == null)
				return;
			// If receiver with the client id given in the request is not registered,
			// further processing of request is canceled
			if (!isReceiverRegistered(clientId))
				return;
			sendMessageToReceiver(getReceiver(clientId), notificationMessage);
			closeConnection();
		}
	}

	/**
	 * Reads a line and validates for client ID.
	 * 
	 * @return Client ID as UUID
	 * @throws IOException
	 */
	private UUID readClientId() throws IOException {
		String clientIdAsText = reader.readLine();

		// Get client Id from request body and validate
		UUID clientId = validateUUID(clientIdAsText);
		if (clientId == null) {
			String errorMessage = "No valid ClientId!";
			printError(errorMessage);
			sendErrorMessage(errorMessage);
			return null;
		}
		return clientId;
	}

	/**
	 * Reads a line and validates for a role.
	 * 
	 * @return Role in request: Receiver or Sender
	 * @throws IOException
	 */
	private String readRole() throws IOException {
		String roleAsText = reader.readLine();

		// Get role from request body and validate
		String role = validateRole(roleAsText);
		if (role == null) {
			String errorMessage = "No valid role!";
			printError(errorMessage);
			sendErrorMessage(errorMessage);
			return null;
		}
		return role;
	}

	/**
	 * Stores a receiver to retrieve for pushing notifications.
	 * 
	 * @param receiver: Saved receiver
	 */
	private void registerNewReceiver(Receiver receiver) {
		receiverStorage.addNewReceiver(receiver);
	}

	private Receiver getReceiver(UUID receiverId) {
		return receiverStorage.getReceiver(receiverId);
	}

	/**
	 * Checks if receiver with this client Id is registered in PMNS.
	 * 
	 * @param receiverId: Client ID
	 * @return true if receiver is already registered, otherwise false
	 * @throws IOException
	 */
	private boolean isReceiverRegistered(UUID receiverId) throws IOException {
		if (!receiverStorage.isReceiverRegistered(receiverId)) {
			String errorMessage = "Receiver Id is not registered!";
			printError(errorMessage);
			sendErrorMessage(errorMessage);
			return false;
		}
		return true;
	}

	/**
	 * Reads a line and validates for a specific role.
	 * 
	 * @return Role in request: Receiver or Sender
	 * @throws IOException
	 */
	private String readNotificationMessage() throws IOException {
		String expectedNotificationMessage = reader.readLine();
		if (expectedNotificationMessage == null) {
			String errorMessage = "Notification message missing!";
			printError(errorMessage);
			sendErrorMessage(errorMessage);
			return null;
		}
		String notificationMessage = ServerFunctions.getNotificationMessage(expectedNotificationMessage);
		return notificationMessage;
	}

	/**
	 * Sends a notification message to a receiver
	 * 
	 * @param receiver: Receiver which will receive a notification
	 * @param notificationMessage: Notification message
	 */
	private void sendMessageToReceiver(Receiver receiver, String notificationMessage) {
		Thread TSendMessage = new Thread(() -> {
			try {
				sendMessageToReceiver(receiver, notificationMessage, RETRY_SENDING);
			} catch (InterruptedException e) {
				printError("Sending has been interrupt!");
				printException(e);
			}
		});
		TSendMessage.start();
	}

	/**
	 * Sends a notification message to a receiver and retry it a few times if sending fails.
	 * 
	 * @param receiver: Receiver which will receive a notification
	 * @param notificationMessage: Notification message
	 * @param retrySending: Fixed number to retry sending
	 * @throws InterruptedException
	 */
	private void sendMessageToReceiver(Receiver receiver, String notificationMessage, int retrySending)
			throws InterruptedException {
		// Sending has failed!
		if (retrySending == 0)
			return;
		try {
			sendNotification(receiver, notificationMessage);
		} catch (SocketException socketException) {
			printError("Client has no connection to PMSN");
			receiverStorage.disconnectReceiver(receiver);
			return;
		} catch (IOException e) {
			printError("Sending notification to receiver failed!");
			printException(e);

			// Retry sending after waiting a few seconds 
			waitAFewSeconds();
			printInfo("Retry sending!");
			sendMessageToReceiver(receiver, notificationMessage, retrySending - 1);
		}
	}

	/**
	 * This tread is stopped to wait a few seconds.
	 * 
	 * @throws InterruptedException
	 */
	private void waitAFewSeconds() throws InterruptedException {
		Thread.sleep(MILLISECONDS_WAIT_TO_RETRY_SENDING);
	}

	/**
	 * Sends a notification to a specific receiver over his connection to PMNS.
	 * 
	 * @param receiver: Specific receiver who will get a notification
	 * @param notificationMessage
	 * @throws IOException
	 */
	private void sendNotification(Receiver receiver, String notificationMessage) throws IOException {
		if (!receiver.isCurrentlyConnected()) {
			printInfo("Receiver with Id " + receiver.getReceiverId() + " currently not connected!");
			return;
		}
		Socket connectionToReceiver = receiver.getConnection();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connectionToReceiver.getOutputStream()));
		bw.write(ServerFunctions.POST_NOTIFICATION + notificationMessage);
		bw.newLine();
		bw.flush();
		printInfo("Notification text successfully sent: " + notificationMessage);
	}

	/**
	 * Sends an error message to sender of the request and closes the connection.
	 * 
	 * @param errorMessage
	 * @throws IOException
	 */
	private void sendErrorMessage(String errorMessage) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conncetion.getOutputStream()));
		bw.write(ServerFunctions.ERROR + errorMessage);
		bw.newLine();
		bw.flush();
		closeConnection();
	}

	private UUID validateUUID(String clientIdAsText) {
		return ServerFunctions.getClientID(clientIdAsText);
	}

	private String validateRole(String role) {
		return ServerFunctions.getRole(role);
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
