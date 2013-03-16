package org.virutor.chess.ui;

import org.virutor.chess.model.Piece;

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
	
	public byte asPiece() {
		
		switch(this) {
			case BLACK_BISHOP: 
			case WHITE_BISHOP:
				return Piece.PIECE_BISHOP;
			case BLACK_KING: 
			case WHITE_KING:
				return Piece.PIECE_KING;
			case BLACK_KNIGHT: 
			case WHITE_KNIGHT:
				return Piece.PIECE_KNIGHT;
			case BLACK_PAWN: 
			case WHITE_PAWN:
				return Piece.PIECE_PAWN;
			case BLACK_QUEEN: 
			case WHITE_QUEEN:
				return Piece.PIECE_QUEEN;
			case BLACK_ROOK: 
			case WHITE_ROOK:
				return Piece.PIECE_ROOK;
			default:
				throw new IllegalArgumentException("Unknown piece type");					
		}
	}
 

}
