package org.virutor.chess.standard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Piece;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.Continuation;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

public class SanMove {

	private static final String SHORT_CASTLE = "O-O";
	private static final String LONG_CASTLE = "O-O-O";
	
	
	private static final Map<Byte, String> PIECES_STRINGS = new HashMap<Byte, String>();
	private static final Map<Character, Byte> CHAR_BYTES = new HashMap<Character, Byte>();
	static {
		PIECES_STRINGS.put(Piece.PIECE_KING, "K");
		PIECES_STRINGS.put(Piece.PIECE_QUEEN, "Q");
		PIECES_STRINGS.put(Piece.PIECE_KNIGHT, "N");
		PIECES_STRINGS.put(Piece.PIECE_PAWN, "");
		PIECES_STRINGS.put(Piece.PIECE_ROOK, "R");
		PIECES_STRINGS.put(Piece.PIECE_BISHOP, "B");

		CHAR_BYTES.put('K', Piece.PIECE_KING);
		CHAR_BYTES.put('Q', Piece.PIECE_QUEEN);
		CHAR_BYTES.put('N', Piece.PIECE_KNIGHT);
		CHAR_BYTES.put('R', Piece.PIECE_ROOK);
		CHAR_BYTES.put('B', Piece.PIECE_BISHOP);
	}

	private String moveRepresentation;

	public static String format(Move move, Position position) {
		return format(move, position, MoveGenerator.generateMoves(position));
	} 
	public static String format(Move move, Position position, GeneratedMoves generatedMoves) {
		return new SanMove(move, position, generatedMoves).toString();
	} 
	
	public static Move parse(String input, Position startingPosition) {
		return parse(input, startingPosition, MoveGenerator.generateMoves(startingPosition));
	}
	
	public static Move parse(String input, Position startingPosition, GeneratedMoves generatedMoves) { 

		if(SHORT_CASTLE.equals(input)) {
			return getShortCastle(generatedMoves);
		} else if (LONG_CASTLE.equals(input)) {
			return getLongCastle(generatedMoves);				
		} else {
			return parseOrdinaryMove(generatedMoves, input);
		}

	}
	
	
	
	private static Move parseOrdinaryMove(GeneratedMoves generatedMoves, String input) {
		
		
		//TODO validate length >= 2
		
		Byte piece = CHAR_BYTES.get(input.charAt(0));
		if(piece == null) {
			piece = Piece.PIECE_PAWN;
		} else {
			input = input.substring(1);
		}
			

		//TODO validate length >= 2		
		int from120Index = Position.parseField(input);
		int fromRow = Position.OFF_BOARD;
		int fromColumn = Position.OFF_BOARD;
		int to120Index = Position.OFF_BOARD;
		boolean capture = false;
		byte pieceCaptured = Piece.NO_PIECE;
		
		if(from120Index == Position.OFF_BOARD) {
			fromColumn = Position.parseColumn(input.charAt(0));
			if(fromColumn == Position.OFF_BOARD) {
				fromRow = Position.parseRow(input.charAt(0));
			}
			if(fromColumn != Position.OFF_BOARD || fromRow != Position.OFF_BOARD) {
				input = input.substring(1);
			}
		} else {
			input = input.substring(2);
			if(input.length() == 0) {
				to120Index = from120Index;
				from120Index = Position.OFF_BOARD;
			}
		}
		
		//TODO validate length from here to the end

		if(to120Index == Position.OFF_BOARD) {
			if(input.charAt(0) == 'x') { 		
				capture = true;			
				input = input.substring(1);
			}
			
			if(input.length() >= 2 && to120Index == Position.OFF_BOARD) {
				to120Index = Position.parseField(input);
				if(to120Index != Position.OFF_BOARD) {
					input = input.substring(2);
				}
			}
			if(to120Index == Position.OFF_BOARD) {
				to120Index = from120Index;
				from120Index = Position.OFF_BOARD;				
			}
		}
		
		if(input.length() != 0) {
			if(input.charAt(0) == '=') {
				pieceCaptured = CHAR_BYTES.get(input.charAt(1));
				input = input.substring(2);
			}
		}

		//TODO chech(+) and checkMate(#)
		
		for(Move move : generatedMoves.moves) {
			
			if(move.piece_moved != piece) {
				continue;
			}
			if(move.to != to120Index) {
				continue;
			}
			if(from120Index != Position.OFF_BOARD && move.from != from120Index) {
				continue;
			}
			if(fromRow != Position.OFF_BOARD && (move.from / 10) - 1 != fromRow) {
				continue;
			}
			if(fromColumn != Position.OFF_BOARD && (move.from % 10) != fromColumn) {
				continue;
			}
			if(pieceCaptured != Piece.NO_PIECE && move.piece_captured != pieceCaptured) {
				continue;
			}
			return move;
		}
		
		throw new IllegalArgumentException("Cannot parse move " + input);
		
	}
	
	private static Move getShortCastle(GeneratedMoves generatedMoves) {
		
		for(Move move : generatedMoves.moves) {
			if(move.castle_ep_flag == Move.CASTLE_EP_FLAG_WHITE_0_0 || move.castle_ep_flag == Move.CASTLE_EP_FLAG_BLACK_0_0) {
				return move;
			}
		}
		throw new IllegalArgumentException("Cannot do short castle in this position");
		
	}
	
	private static Move getLongCastle(GeneratedMoves generatedMoves) {
		
		for(Move move : generatedMoves.moves) {
			if(move.castle_ep_flag == Move.CASTLE_EP_FLAG_WHITE_0_0_0 || move.castle_ep_flag == Move.CASTLE_EP_FLAG_BLACK_0_0_0) {
				return move;
			}
		}
		throw new IllegalArgumentException("Cannot do short castle in this position");
		
	}

	
	public SanMove(Move move, Position position) {
		this(move, position, MoveGenerator.generateMoves(position));
	}
	public SanMove(Move move, Position position, GeneratedMoves generatedMoves) {

		StringBuilder sb = new StringBuilder();
		
		switch(move.castle_ep_flag) {
			case Move.CASTLE_EP_FLAG_WHITE_0_0_0:
			case Move.CASTLE_EP_FLAG_BLACK_0_0_0:
				sb.append("O-O-O");
				break;
			case Move.CASTLE_EP_FLAG_WHITE_0_0:
			case Move.CASTLE_EP_FLAG_BLACK_0_0:
				sb.append("O-O");
				break;
			default:
				
				sb.append(PIECES_STRINGS.get(move.piece_moved));
				
				//this should all go well with e.p. moves
				if(Piece.PIECE_PAWN == move.piece_moved) {
					if(move.piece_captured != Piece.NO_PIECE) {				
						sb.append(Position.getFile(move.from));
					}					
				} else {
					
					switch(getAmbiguousMove(generatedMoves.moves, move)) {
						case BOTH_CONFLICTS: 
							sb.append(Position.FIELD_STRINGS[move.from]);
							break;
						case FILE_CONFLICT: 
							sb.append(Position.getRow(move.from));
							break;
						case ROW_CONFLICT: 
							sb.append(Position.getFile(move.from));
							break;
					}					
					
				}				
				
				if(move.piece_captured != Piece.NO_PIECE) {
					sb.append("x");
				}
				
				sb.append(Position.FIELD_STRINGS[move.to]);

				if(move.piece_promoted != Piece.NO_PIECE) {
					sb.append("=" + PIECES_STRINGS.get(move.piece_promoted));
				}
				break;
					
		}
		
		if(generatedMoves.continuation == Continuation.CHECK_MATE) {
			sb.append("#");
		} else if(MoveGenerator.isInCheck(MoveGenerator.doMove(position, move, generatedMoves))) {
			sb.append("+");
		}
		
		
		moveRepresentation = sb.toString();
		
	
		
	}
	
	private enum MoveConflict {
		NO_CONFLICT,
		ROW_CONFLICT,
		FILE_CONFLICT,
		BOTH_CONFLICTS		
	}
	
	MoveConflict getAmbiguousMove(List<Move> moves, Move move) {
		
		boolean fileConflict = false;
		boolean rowConflict = false;		
		
		for(Move moveIter : moves) {
			if(move.equals(moveIter)) {
				continue;
			}
			if(moveIter.piece_moved != move.piece_moved) {
				continue;
			}
			if(moveIter.to != move.to) { 
				continue;
			}
			
			if(Position.getFile(moveIter.from) == Position.getFile(move.from)) {
				fileConflict = true;
				continue;
			}
			if(Position.getRow(moveIter.from) == Position.getRow(move.from)) {
				rowConflict = true;
				continue;
			}
		}
		
		if(!fileConflict && !rowConflict) {
			return MoveConflict.NO_CONFLICT;
		}
		if(fileConflict && rowConflict) {
			return MoveConflict.BOTH_CONFLICTS;
		}
		return fileConflict ? MoveConflict.FILE_CONFLICT : MoveConflict.ROW_CONFLICT;
		
	}
	
	public String toString() {
		return moveRepresentation;
	}		
	
}
