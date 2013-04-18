package org.virutor.chess.ui;

import org.virutor.chess.model.Field;
import org.virutor.chess.model.Piece;
import org.virutor.chess.model.Position;
import static org.virutor.chess.model.Position.*;

@Deprecated
public class UiPosition {

	public ColorPieceType[] pieces = new ColorPieceType[64];
	
	public UiPosition() {
		init();
	}
	
	private void init() {
		for(int i = 0; i < 64; i++) {
			pieces[i] = ColorPieceType.NO_PIECE;
		}
	}
	
	public void setPosition(Position modelPosition) {
		init();
		for(int i = 20; i < 100; i++) {
			if(i % 10 == 0 || i % 10 == 9) {
				continue;
			}
			int index = from120to64(i); 
			//TODO remove
			if(index < 0 || index > 63) {
				index = 22;
			}
			pieces[index] = getPieceType(modelPosition.board[i]);			
		}
	}
	
	private ColorPieceType getPieceType(Field field) {
		if(field.color == COLOR_FREE || field.color == COLOR_OFF_BOARD) {
			return ColorPieceType.NO_PIECE;
		}
		switch(field.pieceType) {
			case Piece.PIECE_KING:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_KING : ColorPieceType.BLACK_KING;
			case Piece.PIECE_BISHOP:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_BISHOP : ColorPieceType.BLACK_BISHOP;
			case Piece.PIECE_KNIGHT:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_KNIGHT : ColorPieceType.BLACK_KNIGHT;
			case Piece.PIECE_PAWN:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_PAWN : ColorPieceType.BLACK_PAWN;
			case Piece.PIECE_QUEEN:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_QUEEN : ColorPieceType.BLACK_QUEEN;
			case Piece.PIECE_ROOK:
				return field.color == COLOR_WHITE ? ColorPieceType.WHITE_ROOK : ColorPieceType.BLACK_ROOK;
			default:
				return ColorPieceType.NO_PIECE;
		}
	}
	
	public static int from120to64(int i) {
		return (i/10 - 2) * 8 + (i % 10) - 1;
	}
	
	public static int from64to120(int i) {
		return (i/8 + 2) * 10 + (i % 8) + 1;
	}
	

}
