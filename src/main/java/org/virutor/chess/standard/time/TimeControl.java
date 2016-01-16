package org.virutor.chess.standard.time;

public interface TimeControl {

	//FIXME exception?
	public static final TimeControl UNKNOWN = () -> -1;

	//FIXME exception?
	public static final TimeControl NO_CONTROL = () -> -1;

	long getTotalFirstTime();
	
}
