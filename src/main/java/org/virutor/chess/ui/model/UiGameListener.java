package org.virutor.chess.ui.model;

import org.virutor.chess.model.Move;

public interface UiGameListener {

	public enum GameChangeType {
		MOVE_INDEX,
		COMPLETE_CHANGE,
		CONFIG_CHANGE,
	} 
	
	void onGenericChange(GameChangeType gameChangeType); 
	void onDoMove(Move move);
	void onUndoMove(Move move);
	
	
}
