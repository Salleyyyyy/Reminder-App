package com.server.httpserver;

import java.util.UUID;

/**
 * See at Doc of PMNS.
 */
public class ServerFunctions {
	
	public static final String SENDER = "Sender";
	public static final String RECEIVER = "Receiver";
	public static final String ERROR = "ERROR: ";
	public static final String POST_NOTIFICATION = "NOTIFICATION: ";
	public static final String POST_CLIENT_ID = "ClientId: ";
	public static final String POST_ROLE = "Role: ";
	public static final String POST_CLIENT_TOKEN = "Token: ";
	public static final String REGEX_CLIENT_ID = POST_CLIENT_ID + ".+";
	public static final String REGEX_CLIENT_TOKEN = POST_CLIENT_TOKEN + ".+";
	public static final String REGEX_ROLE = POST_ROLE + SENDER + "|" + POST_ROLE + RECEIVER;
	
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
	
	public static String getClientToken(String expectedClientTokenAsText) {
		if(!expectedClientTokenAsText.matches(REGEX_CLIENT_TOKEN)) return null;
		
		String[] clientTokenPart = expectedClientTokenAsText.split(POST_CLIENT_TOKEN);
		// TODO Token validation
		return clientTokenPart[1];
	}
	
	public static String getRole(String expectedRole) {
		if(!expectedRole.matches(REGEX_ROLE)) return null;
		
		String[] clientRolePart = expectedRole.split(POST_ROLE);
		return clientRolePart[1];
	}
	
	public static String getNotificationMessage(String expectedNotificationMessage) {
		// Message can also be empty string
		String[] clientRolePart = expectedNotificationMessage.split(POST_NOTIFICATION);
		return clientRolePart[1];
	}
}
