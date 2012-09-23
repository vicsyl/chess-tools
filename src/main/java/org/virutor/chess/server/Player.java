package org.virutor.chess.server;


public interface Player extends GameStateChangeListener {
	String getName();	
	void play();
	void start();
	boolean isStarted();
	void stop();
	void quit();
}
