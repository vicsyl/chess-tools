package org.virutor.chess.model;

import static org.virutor.chess.model.Piece.*;
import static org.virutor.chess.model.Position.*;

public class Move {

	public static final byte CASTLE_EP_FLAG_NO_CASTLE = 0;
	public static final byte CASTLE_EP_FLAG_WHITE_0_0 = 1;
	public static final byte CASTLE_EP_FLAG_WHITE_0_0_0 = 2;
	public static final byte CASTLE_EP_FLAG_BLACK_0_0 = 3;
	public static final byte CASTLE_EP_FLAG_BLACK_0_0_0 = 4;
	public static final byte CASTLE_EP_FLAG_EP = 5;
	
	public int from = OFF_BOARD;
	public int to = OFF_BOARD;
	public byte castle_ep_flag = CASTLE_EP_FLAG_NO_CASTLE;
	public byte piece_moved = NO_PIECE;
	public byte piece_captured = NO_PIECE;
	public byte piece_promoted = NO_PIECE;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + castle_ep_flag;
		result = prime * result + from;
		result = prime * result + piece_captured;
		result = prime * result + piece_moved;
		result = prime * result + piece_promoted;
		result = prime * result + to;
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
		Move other = (Move) obj;
		if (castle_ep_flag != other.castle_ep_flag)
			return false;
		if (from != other.from)
			return false;
		if (piece_captured != other.piece_captured)
			return false;
		if (piece_moved != other.piece_moved)
			return false;
		if (piece_promoted != other.piece_promoted)
			return false;
		if (to != other.to)
			return false;
		return true;
	}
	
	
}
