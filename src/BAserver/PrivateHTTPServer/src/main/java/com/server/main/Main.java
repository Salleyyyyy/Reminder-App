package com.server.main;

import com.server.httpserver.FCMForwardingServer;
import com.server.httpserver.LongPollingServer;
import com.server.httpserver.PMSNForwardingServer;

/**
 * Entry point to start all server instances.
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("Start all private server");
		// Start all push server
		LongPollingServer.getServerInstance().start();
		FCMForwardingServer.getServerInstance().start();
		PMSNForwardingServer.getServerInstance().start();
	}
}
