package com.server.forwarding;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import com.server.httpserver.ServerFunctions;
import com.server.networkinfo.PMSNNetworkInfo;

/**
 * This class forwards a message (notification) to PMSN that should reach the
 * specified client with Client ID given.
 */
public class PMSNForwarding implements IForwarding {

	/**
	 * IP of PMNS
	 */
	private static final String PSMN_IP = PMSNNetworkInfo.PMSN_IP;
	/**
	 * Port of PMNS
	 */
	private static final int PMSN_PORT = PMSNNetworkInfo.PMSN_PORT;

	/**
	 * Client ID of target client.
	 */
	private UUID clientId;

	/**
	 * Connection to PMNS.
	 */
	private Socket connectionToPMSN;

	/**
	 * Buffered Writer for sending text to PMNS.
	 */
	private BufferedWriter br;

	/**
	 * Constructor.
	 * 
	 * @param clientId: Client ID of the client that will receive the notifications
	 *                  from PMNS
	 */
	public PMSNForwarding(UUID clientId) {
		this.clientId = clientId;
	}

	/**
	 * Forwards a message with the notification info given.
	 * 
	 * @param notificationInfo: Notification info
	 */
	@Override
	public void forwardMessage(NotificationInfo notificationInfo) {
		// PMSN does not consider any priority
		String notificationMessage = notificationInfo.getNotificationMessage();
		try {
			forwardMessageToPMSN(notificationMessage);
		} catch (UnknownHostException e) {
			printException(e);
			return;
		} catch (IOException e) {
			printException(e);
			return;
		}
		printInfo("Sent message to PMSN: " + notificationMessage);
	}

	/**
	 * Forwards a message (notification)/ push request to PMSN with notification
	 * text given.
	 * 
	 * @param notificationMessage: Notififcation Text
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void forwardMessageToPMSN(String notificationMessage) throws UnknownHostException, IOException {
		connectToPMSN();
		prepareSending();

		sendClientId(clientId);
		sendSenderRole();
		sendNotificationMessage(notificationMessage);

		flushStream();
		closeConnectionToPMSN(connectionToPMSN);
	}

	/**
	 * Connects to PMNS.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connectToPMSN() throws UnknownHostException, IOException {
		connectionToPMSN = new Socket(PSMN_IP, PMSN_PORT);
	}

	/**
	 * Prepares for Sending (Initializing the IO writer)
	 * 
	 * @throws IOException
	 */
	private void prepareSending() throws IOException {
		br = new BufferedWriter(new OutputStreamWriter(connectionToPMSN.getOutputStream()));
	}

	/**
	 * Sends a client ID to PMNS.
	 * 
	 * @param clientId: Client ID to specify the target client
	 * @throws IOException
	 */
	private void sendClientId(UUID clientId) throws IOException {
		br.write(ServerFunctions.POST_CLIENT_ID + clientId);
		br.newLine();
	}

	/**
	 * Sends the role to PMSN. In case of the server, the role is SENDER.
	 * 
	 * @throws IOException
	 */
	private void sendSenderRole() throws IOException {
		br.write(ServerFunctions.POST_ROLE + ServerFunctions.SENDER);
		br.newLine();
	}

	/**
	 * Sends the notification text to PMSN.
	 * 
	 * @param notificationMessage: Notification Text
	 * @throws IOException
	 */
	private void sendNotificationMessage(String notificationMessage) throws IOException {
		br.write(ServerFunctions.POST_NOTIFICATION + notificationMessage);
		br.newLine();
	}

	/**
	 * Flushes the stream.
	 * 
	 * @throws IOException
	 */
	private void flushStream() throws IOException {
		br.flush();
	}

	/**
	 * Closes the connection to PMSN.
	 * 
	 * @param connectionToPMSN: Connected socket to PMNS
	 * @throws IOException
	 */
	private void closeConnectionToPMSN(Socket connectionToPMSN) throws IOException {
		connectionToPMSN.close();
	}

	private void printInfo(String infoMessage) {
		System.out.println("INFO: " + infoMessage);
	}

	private void printException(Exception exception) {
		System.out.println("Exception " + exception);
	}
}
