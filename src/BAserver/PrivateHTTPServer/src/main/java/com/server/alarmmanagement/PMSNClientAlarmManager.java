package com.server.alarmmanagement;

import java.util.UUID;

import com.server.forwarding.PMSNForwarding;

/*
 * This class inherits all functionalities of a general client alarmmanager and
 * is specialized for PMSN Push or forwarding a message (notification) to the
 * PMSN Back-End.
 */
public class PMSNClientAlarmManager extends ClientAlarmManager {

	/**
	 * PMSNForwarding instance for forwarding a push request to PMNS
	 */
	private PMSNForwarding psmnForwarding;

	/**
	 * This thread is actively polling for notifications to push them to the PMNS
	 * Back-End (our MNS solution)
	 */
	private Thread waitForForwardingMessageToPMSN = new Thread(() -> {
		while (true) {
			while (!anyNotificationAvailable())
				;
			psmnForwarding.forwardMessage(getNotificationInfo());
		}
	});

	/**
	 * Constructor.
	 * 
	 * @param clientId: Client ID of client
	 */
	public PMSNClientAlarmManager(UUID clientId) {
		super(clientId);
		psmnForwarding = new PMSNForwarding(clientId);
		waitForForwardingMessageToPMSN.start();
	}
}
