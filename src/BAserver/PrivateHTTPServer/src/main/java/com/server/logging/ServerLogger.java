package com.server.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is not used and can be ignored.
 */
public class ServerLogger {

	private Logger logger;
	private static final String PATH = "././././././logfiles/";
	private static final String FILE_ENDING = ".txt";

	public ServerLogger(String loggerName) {
		logger = Logger.getLogger(loggerName);
		logger.setLevel(Level.INFO);

		logger.setUseParentHandlers(false);
		Handler handler;
		try {
			File logFile = new File(PATH + loggerName + FILE_ENDING);
			logFile.createNewFile();
			handler = new FileHandler(logFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			return;
		}
		logger.addHandler(handler);
	}

	public void printInfo(String infoMessage) {
		logger.info(infoMessage);
	}
}
