package org.virutor.chess.model.io;

import static org.virutor.chess.model.Move.*;
import static org.virutor.chess.model.Piece.*;
import static org.virutor.chess.model.Position.*;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;


public class LongAlgebraicMove {

	byte piecePromoted = NO_PIECE;
	int indexFrom = OFF_BOARD; 
	int indexTo = OFF_BOARD; 
	
	public byte getPiecePromoted() {
		return piecePromoted;
	}

	public int getIndexFrom() {
		return indexFrom;
	}

	public int getIndexTo() {
		return indexTo;
	}

	public static String getFieldName(int index) {
		char column = 'A';
		column += ((index%10) - 1);
		char row = '1';
		row += ((index/10) - 2);
		return new String(new char[]{column, row});
	}
	
	public String toString() {
		String s = getFieldName(indexFrom) + getFieldName(indexTo);
		if(piecePromoted != NO_PIECE) {
			s += "(" + piecePromoted + ")";
		}
		return s;
	}
	
	public LongAlgebraicMove(String str) {
		
		indexFrom = Position.parseField(str.substring(0, 2));
		indexTo = Position.parseField(str.substring(2, 4));
		if(str.length() > 4) {
			piecePromoted = parsePromotedPieceType(str.charAt(4)); 
		}
		
		if(getIndexFrom() == 21 && getIndexTo() == 21) {
			String g = null;
		}
		
	}
	
	public LongAlgebraicMove(Move move) {
		if(move.castle_ep_flag == CASTLE_EP_FLAG_NO_CASTLE || 
		   move.castle_ep_flag == CASTLE_EP_FLAG_EP) {
			indexFrom = move.from;
			indexTo = move.to;
			piecePromoted = move.piece_promoted;
		} else {
			switch(move.castle_ep_flag) {
				case CASTLE_EP_FLAG_WHITE_0_0:
					indexFrom = E1;
					indexTo = G1;
					break;
				case CASTLE_EP_FLAG_WHITE_0_0_0:
					indexFrom = E1;
					indexTo = C1;
					break;
				case CASTLE_EP_FLAG_BLACK_0_0:
					indexFrom = E8;
					indexTo = G8;
					break;
				case CASTLE_EP_FLAG_BLACK_0_0_0:
					indexFrom = E8;
					indexTo = C8;
					break;
			}
		}
	} 
	
	public static byte parsePromotedPieceType(char pieceType) {
		
		char toLower = String.valueOf(pieceType).toLowerCase().charAt(0);
		
		switch(toLower) {
			case 'q':
				return PIECE_QUEEN;
			case 'n':
				return PIECE_KNIGHT;
			case 'r':
				return PIECE_ROOK;
			case 'b':
				return PIECE_BISHOP;
			default:
				return -1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + indexFrom;
		result = prime * result + indexTo;
		result = prime * result + piecePromoted;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LongAlgebraicMove other = (LongAlgebraicMove) obj;
		if (indexFrom != other.indexFrom)
			return false;
		if (indexTo != other.indexTo)
			return false;
		if (piecePromoted != other.piecePromoted)
			return false;
		return true;
	}
	
	
}
