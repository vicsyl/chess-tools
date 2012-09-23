package org.virutor.chess.ui;

public enum ColorPieceType {

	NO_PIECE(-1),
	WHITE_PAWN(0),
	WHITE_ROOK(1),
	WHITE_KNIGHT(2),
	WHITE_BISHOP(3),
	WHITE_QUEEN(4),
	WHITE_KING(5),
	BLACK_PAWN(6),
	BLACK_ROOK(7),
	BLACK_KNIGHT(8),
	BLACK_BISHOP(9),
	BLACK_QUEEN(10),
	BLACK_KING(11);

	private int code;
	
	private ColorPieceType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	

}
