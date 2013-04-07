package org.virutor.chess.standard.time;

public class SuddenDeathTimeControl implements TimeControl {

	private final int seconds;

	public SuddenDeathTimeControl(int seconds) {
		this.seconds = seconds;
	}

	public int getSeconds() {
		return seconds;
	}

	@Override
	public long getTotalFirstTime() {
		return seconds * 1000;
	}
	
	
}
