package org.virutor.chess.model;

import static org.virutor.chess.model.Piece.*;
import static org.virutor.chess.model.Position.*;

public class Field {

	public Field effectiveClone() {
		Field ret = new Field();
		ret.pieceType = pieceType;
		ret.color = color;
		return ret;
	}
	
	//public int pieceIndex = -1;
	public byte pieceType = NO_PIECE;
	public byte color = COLOR_FREE;
	
}
