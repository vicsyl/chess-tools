package org.virutor.chess.server;

import org.virutor.chess.model.Move;

public interface GameStateChangeListener {	
	
	/**
	 * 
	 * @param move
	 * 
	 * change is to some representation of move that contains all the information
	 * about the move, so that GameTextArea, for example,
	 * doesn't need to ask server for additional information about the Move
	 * just to print the SanMove... 
	 */
	void moveDone(Move move);	
	void notifyChange();
}
