package org.virutor.chess.uci;

import org.virutor.chess.model.io.LongAlgebraicMove;

public interface GameServerTemp {

	
	public static class InvalidMoveException extends Exception {

		public InvalidMoveException() {
			super();
		}

		public InvalidMoveException(String message, Throwable cause) {
			super(message, cause);
		}

		public InvalidMoveException(String message) {
			super(message);
		}

		public InvalidMoveException(Throwable cause) {
			super(cause);
		}
		
	}
	
	//TODO rethink...
	void play(LongAlgebraicMove move) throws InvalidMoveException;
	
	void notifyReady();
	
}
