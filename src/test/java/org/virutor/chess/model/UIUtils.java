package org.virutor.chess.model;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;

import static org.virutor.chess.model.Move.*;
import static org.virutor.chess.model.Piece.*;
import static org.virutor.chess.model.Position.*;

public class UIUtils {
	
	public static String colorPieceTypeString(byte color, byte pieceType) {
		StringBuilder sb = new StringBuilder();
		if(color == COLOR_WHITE) {
			sb.append("W");
		} else if(color == COLOR_BLACK) {
			sb.append("B");
		} else {
			return null;
		}
		switch(pieceType) {
			case PIECE_ROOK:
				sb.append("R");
				break;
			case PIECE_KNIGHT:
				sb.append("N");
				break;
			case PIECE_BISHOP:
				sb.append("B");
				break;
			case PIECE_QUEEN:
				sb.append("Q");
				break;
			case PIECE_KING:
				sb.append("K");
				break;
			case PIECE_PAWN:
				sb.append("p");
				break;
		}
		
		return sb.toString();
	}

	public static String prettyPositionString(Position position) {
		
		StringBuilder sb = new StringBuilder();
		
		for(int row = 7; row >= 0; row--) {			
			for(int pass = 0; pass < 4; pass++) {
				sb.append("\n");
				for(int column = 0; column < 8; column++) {
					int index = 21 + 10*row + column; 
					if(pass == 0 || pass == 3) {
						sb.append((row + column)%2 == 0 ? "      " : "██████"); 
					} else if(pass == 2) {
						sb.append((row + column)%2 == 0 ? "      " : (position.board[index].color == COLOR_FREE ? "██████" : "█    █"));
					} else {
						if(position.board[index].color == COLOR_FREE) {
							sb.append((row + column)%2 == 0 ? "      " : "██████");
						} else {
							sb.append((row + column)%2 == 0 ? "  " : "█ ");
							sb.append(colorPieceTypeString(position.board[index].color, position.board[index].pieceType));
							sb.append((row + column)%2 == 0 ? "  " : " █");
						}
					}
				}
			}
		}
		
		sb.append(" \n Castles: \n");
		if(position.castles[CASTLE_INDEX_WHITE_0_0]) {
			sb.append("WHITE_0_0 \n");
		}
		if(position.castles[CASTLE_INDEX_WHITE_0_0_0]) {
			sb.append("WHITE_0_0_0 \n");
		}
		if(position.castles[CASTLE_INDEX_BLACK_0_0]) {
			sb.append("BLACK_0_0 \n");
		}
		if(position.castles[CASTLE_INDEX_BLACK_0_0_0]) {
			sb.append("BLACK_0_0_0 \n");
		}

		sb.append(position.colorToMove == COLOR_WHITE ? "White to move \n" : "Black to move \n");
		if(position.possibleEpIndex != OFF_BOARD) {
			//sb.append("possible ep: " + getFieldName(position.possibleEpIndex));
			sb.append("possible ep: " + position.possibleEpIndex);
		}
		sb.append("Fullmove clock:" + position.fullMoveClock + "\n");
		sb.append("Halfmove clock:" + position.halfMoveClock + "\n");
		return sb.toString();
	}
	

	
	private static String[] CASTLES_STRINGS = new String[] {"0_0", "0_0_0", "0_0", "0_0_0"};
	
	public static String prettyStringMove(Move move) {

		String moveString = null;
		if(move.castle_ep_flag == CASTLE_EP_FLAG_NO_CASTLE || move.castle_ep_flag == CASTLE_EP_FLAG_EP) {
			//moveString = getFieldName(move.from) + getFieldName(move.to);
			moveString = "" + move.piece_moved + ":" + move.from + "-" + move.to;
			if(move.castle_ep_flag == CASTLE_EP_FLAG_EP) {
				moveString += "e.p.";
			}
		} else {			
			moveString = CASTLES_STRINGS[move.castle_ep_flag - 1]; 
		}

		return moveString;

	}

}
