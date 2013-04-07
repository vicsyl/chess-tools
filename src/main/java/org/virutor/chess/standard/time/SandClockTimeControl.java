package org.virutor.chess.standard.time;

public class SandClockTimeControl implements TimeControl {

	private final int sandclockSeconds;

	public SandClockTimeControl(int sandclockSeconds) {
		this.sandclockSeconds = sandclockSeconds;
	}

	public int getSandclockSeconds() {
		return sandclockSeconds;
	}

	@Override
	public long getTotalFirstTime() {
		return sandclockSeconds * 1000;
	}
	
	
}
