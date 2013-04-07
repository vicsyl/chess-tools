package org.virutor.chess.standard.time;

public class IncrementalTimeControl implements TimeControl {

	private final int base;
	private final int increment;
	
	public IncrementalTimeControl(int base, int increment) {
		this.base = base;
		this.increment = increment;
	}

	public int getBase() {
		return base;
	}

	public int getIncrement() {
		return increment;
	}

	@Override
	public long getTotalFirstTime() {
		return base * 1000;
	}
	
}
