package org.virutor.chess.standard.time;

public class MovesInSecondsTimeControl implements TimeControl {

	private final int moves;
	private final int seconds;

	public MovesInSecondsTimeControl(int moves, int seconds) {
		this.moves = moves;
		this.seconds = seconds;
	}

	public int getMoves() {
		return moves;
	}	

	public int getSeconds() {
		return seconds;
	}

	@Override
	public long getTotalFirstTime() {
		return seconds * 1000;
	}

}
