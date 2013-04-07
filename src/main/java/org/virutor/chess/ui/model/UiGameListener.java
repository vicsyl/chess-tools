package org.virutor.chess.ui.model;

import org.virutor.chess.model.Move;

public interface UiGameListener {

	public enum GameChangeType {
		MOVE_INDEX,
		COMPLETE_CHANGE	
	} 
	
	public static class UiGameChange {
		public GameChangeType gameChangeType;		
		public boolean currentPositionChanged;
		
		public UiGameChange(GameChangeType gameChangeType, boolean currentPositionChanged) {
			this.gameChangeType = gameChangeType;
			this.currentPositionChanged = currentPositionChanged;
		}

		public static UiGameChange getCompleteChange() {
			return new UiGameChange(GameChangeType.COMPLETE_CHANGE, true);
		}
		public static UiGameChange getStructureChange() {
			return new UiGameChange(GameChangeType.COMPLETE_CHANGE, false);
		}
		public static UiGameChange getIndexChanged() {
			return new UiGameChange(GameChangeType.MOVE_INDEX, true);
		}
		
	}
	
	void onGenericChange(UiGameChange uiGameChange); 
	void onDoMove(Move move);
	void onUndoMove(Move move);
	
	
}
