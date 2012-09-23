package org.virutor.chess.model.io;

import java.util.HashMap;
import java.util.Map;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Piece;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.Continuation;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

@Deprecated
public class RichMove {

	private static final Map<Byte, Character> PIECES_LETTERS = new HashMap<Byte, Character>();
	static {
		PIECES_LETTERS.put(Piece.PIECE_KING, 'K');
		PIECES_LETTERS.put(Piece.PIECE_QUEEN, 'Q');
		PIECES_LETTERS.put(Piece.PIECE_KNIGHT, 'N');
		PIECES_LETTERS.put(Piece.PIECE_PAWN, 'p');
		PIECES_LETTERS.put(Piece.PIECE_ROOK, 'R');
		PIECES_LETTERS.put(Piece.PIECE_BISHOP, 'B');
	}
	
	private Move move;
	private Position startPosition;
	private Position endPosition;
	private boolean check;
	
	//TODO
	//private boolean mate;
	private String richNotation;
	/*
	public RichMove(Move move, Position startPosition, Position endPosition, GeneratedMoves generatedMoves) {
		this.move = move;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		check = MoveGenerator.isInCheck(endPosition);
		
		StringBuilder sb = new StringBuilder();
		
		switch(move.castle_ep_flag) {
			case Move.CASTLE_EP_FLAG_WHITE_0_0_0:
			case Move.CASTLE_EP_FLAG_BLACK_0_0_0:
				sb.append("0-0-0");
				break;
			case Move.CASTLE_EP_FLAG_WHITE_0_0:
			case Move.CASTLE_EP_FLAG_BLACK_0_0:
				sb.append("0-0");
				break;
			default:
				sb.append(Position.FIELD_STRINGS[move.from]);
				sb.append(move.piece_captured == Piece.NO_PIECE ? "-" : "x");
				sb.append(Position.FIELD_STRINGS[move.to]);
				if(move.castle_ep_flag == Move.CASTLE_EP_FLAG_EP) {
					sb.append("(e.p.)");
				}
				if(move.piece_promoted != Piece.NO_PIECE) {
					sb.append(PIECES_LETTERS.get(move.piece_promoted));
				}
				break;
					
		}
		
		
		if(check) {
			sb.append("+");
		}
		
		switch(generatedMoves.continuation) {
			case CHECK_MATE:
				sb.append("#");
				sb.append(startPosition.colorToMove == Position.COLOR_WHITE ? "1-0" : "0-1");
				break;
			case STALEMATE:
				sb.append("1/2-1/2");
				break;
		}
		
		richNotation = sb.toString();
	
		
	}
*/
	public String getRichNotation() {
		return richNotation;
	}			
		
	
}
