package org.virutor.chess.uci;

import org.virutor.chess.model.io.LongAlgebraicMove;

public interface GameServerTemp {

	public static class InvalidMoveException extends Exception {
		
	}
	
	void play(LongAlgebraicMove move) throws InvalidMoveException;
	
	void notifyReady();
	
}
