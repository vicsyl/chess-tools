package org.virutor.chess.log;

public class LoggerServiceFactory {

	private static Logger logger = new ConsoleLogger();
	
	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		LoggerServiceFactory.logger = logger;
	}
	
}
