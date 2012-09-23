package org.virutor.chess.log;

public class ConsoleLogger implements Logger {

	@Override
	public void info(String string) {
		System.out.println(string);
	}
	
}
