package com.pmsn.constants;

import java.util.UUID;

/**
 * This class contains helper functions and regular expressions to process incoming requests to PMNS.
 * Requests sent to PMNS must use following regex statement to be successfully processed by PMNS.
 */
public class ServerFunctions {
	
	/**
	 * A sender can be a server.
	 */
	public static final String SENDER = "Sender";
	/**
	 * A receiver registered at PMNS to get notified by a sender.
	 */
	public static final String RECEIVER = "Receiver";
	public static final String ERROR = "ERROR: ";
	/**
	 * A request to push a notification must start with this for a notification message.
	 */
	public static final String POST_NOTIFICATION = "NOTIFICATION: ";
	/**
	 * A request to register as receiver must start with this for his client Id.
	 */
	public static final String POST_CLIENT_ID = "ClientId: ";
	/**
	 * A request to register as for a certain role must start with this.
	 */
	public static final String POST_ROLE = "Role: ";
	public static final String POST_CLIENT_TOKEN = "Token: ";
	/**
	 * Regex to validate for client Ids
	 */
	public static final String REGEX_CLIENT_ID = POST_CLIENT_ID + ".+";
	public static final String REGEX_CLIENT_TOKEN = POST_CLIENT_TOKEN + ".+";
	/**
	 * Regex to validate for a role
	 */
	public static final String REGEX_ROLE = POST_ROLE + SENDER + "|" + POST_ROLE + RECEIVER;
	
	/**
	 * This method returns the client ID in the string.
	 * 
	 * @param expectedClientIdAsText: string which should contain the client ID as UUID
	 * @return client Id as UUID or null if regex match fails or string does not contain a parsable UUID.
	 */
	public static UUID getClientID(String expectedClientIdAsText) {
		if(!expectedClientIdAsText.matches(REGEX_CLIENT_ID)) return null;
	
		UUID clientId = null;
		try {
			String[] clientIdPart = expectedClientIdAsText.split(POST_CLIENT_ID);
			clientId = UUID.fromString(clientIdPart[1]);
		}
		catch(Exception e) {
			return null;
		}
		return clientId;
	}
	
	/**
	 * Returns the role in the string.
	 * 
	 * @param expectedRole: Sender or Receiver 
	 * @return Role or null if regex match fails or string does not contain a parsable UUID.
	 */
	public static String getRole(String expectedRole) {
		if(!expectedRole.matches(REGEX_ROLE)) return null;
		
		String[] clientRolePart = expectedRole.split(POST_ROLE);
		return clientRolePart[1];
	}
	
	/**
	 * Returns the notification message as string to send to a receiver.
	 * 
	 * @param expectedNotificationMessage: notification message in regex string
	 * @return notification message
	 */
	public static String getNotificationMessage(String expectedNotificationMessage) {
		// Message can also be empty string
		String[] clientRolePart = expectedNotificationMessage.split(POST_NOTIFICATION);
		return clientRolePart[1];
	}
}
