package com.server.main;

import com.pmsn.server.PrivateMobileServerNotification;

/**
 * Entry point to start the PMNS.
 */
public class Main {

	public static void main(String[] args) {
		startPMSN();
	}

	/**
	 * Starts the PMNS server instance.
	 */
	private static void startPMSN() {
		PrivateMobileServerNotification.getServerInstance().start();
	}

	/**
	 * Command Line Mode
	 * 
	 * private static final char START_PSMN_COMMAND = 's'; private static final char
	 * CLOSE_PSMN_COMMAND = 'c'; private static final char LEAVE_COMMAND_LINE = 'l';
	 * private static final int COMMAND_LENGTH = 1;
	 * 
	 * private static void startCommandLine() { printWelcomeText();
	 * 
	 * BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	 * String possibleCommand; boolean leaveCommandLine = false; try { while
	 * (!leaveCommandLine) { possibleCommand = br.readLine(); if
	 * (!(possibleCommand.length() == COMMAND_LENGTH)) { System.out.println("Unknown
	 * command!"); printNewEmptyLine(); continue; } switch
	 * (possibleCommand.charAt(0)) { case START_PSMN_COMMAND:
	 * PrivateMobileServerNotification.getServerInstance().start(); break; case
	 * CLOSE_PSMN_COMMAND:
	 * PrivateMobileServerNotification.getServerInstance().close(); break; case
	 * LEAVE_COMMAND_LINE: System.out.println("Leave command line and close
	 * server!"); PrivateMobileServerNotification.getServerInstance().close();
	 * leaveCommandLine = true; break; default: System.out.println("Unknown
	 * command!"); printNewEmptyLine(); } } } catch (IOException e) {
	 * System.out.println("An error occured within the command line!");
	 * System.out.println("Close PMSN!");
	 * PrivateMobileServerNotification.getServerInstance().close(); }
	 * System.out.println("Command line successfully closed!"); }
	 * 
	 * private static void printWelcomeText() { System.out.println("Welcome to PMSN
	 * command line"); printNewEmptyLine(); printCommandOptions();
	 * printNewEmptyLine(); }
	 * 
	 * private static void printCommandOptions() { System.out.println("Follwing
	 * commands are available:"); System.out.println("> Press " + START_PSMN_COMMAND
	 * + " and enter to start PMSN."); System.out.println("> Press " +
	 * CLOSE_PSMN_COMMAND + " and enter to close PMSN."); System.out.println(">
	 * Press " + LEAVE_COMMAND_LINE + " and enter to leave command line."); }
	 * 
	 * private static void printNewEmptyLine() { System.out.println(); }
	 **/
}
